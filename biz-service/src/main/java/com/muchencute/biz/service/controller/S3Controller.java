package com.muchencute.biz.service.controller;

import cn.hutool.core.collection.CollUtil;
import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.s3.minio.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Tag(name = "获取上传或下载地址")
@RestController
@RequestMapping("s3")
@Slf4j
public class S3Controller {

  private final static Map<String, Set<String>> LEGAL_DIRECTORIES = new HashMap<>() {{
    put("import_datasource", Set.of("datasource:import"));
    put("images_and_videos", Set.of("profile"));
    put("import_document", Set.of("document:import"));
    put("export_document", Set.of("document:export"));
    put("import_evaluation", Set.of("evaluation:import_and_export"));
    put("export_work_orders", Set.of("work_order:import_and_export"));
    put("export_evaluation", Set.of("evaluation:import_and_export"));
    put("export_logs", Set.of("log:export"));
    put("export_report", Set.of("report:export", "report:read", "report:crud"));
    put("export_research", Set.of("research:export"));
    put("export_vertex", Set.of("vertex:export"));
  }};

  private final static Set<String> NEED_USERNAME = Set.of("import_document", "import_work_orders");

  private final MinioService minioService;

  private final Pattern objectNamePattern = Pattern.compile(
          "^/[^/]+/(?<path>[^/]+)/(?<date>[^/]+)/(?<name>[^/]+)$");

  @Autowired
  public S3Controller(MinioService minioService) {

    this.minioService = minioService;
  }

  @GetMapping(value = "putURL")
  @SneakyThrows
  @Operation(summary = "获取上传地址")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> uploadPreSignedURL(@RequestParam String name,
                                                   @RequestParam String path,
                                                   @RequestParam(defaultValue = "false") boolean keepName,
                                                   @RequestHeader Map<String, String> headers) {

    authorize(path);

    final var legalHeaders = headers.entrySet().stream()
            .filter(it -> it.getKey().startsWith("x-amz-meta-"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    final var response = ResponseEntity.ok();

    if (NEED_USERNAME.contains(path)) {
      final var username = JwtHelper.getUsername();
      legalHeaders.put("x-amz-meta-username", username);
      response.header("x-amz-meta-username", username);
    }
    final var encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
    legalHeaders.put("x-amz-meta-origin-filename", encodedName);
    response.header("x-amz-meta-origin-filename", encodedName);

    final var url = minioService.putPreSignedURL(name, path, legalHeaders, keepName);
    return response.body(url);
  }

  @GetMapping("getURL")
  @Operation(summary = "获取下载地址")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public String downloadPreSignedURL(@RequestParam String name) {

    final var matcher = objectNamePattern.matcher(name);
    if (!matcher.matches()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    final var path = matcher.group("path");
    final var date = matcher.group("date");
    final var filename = matcher.group("name");

    authorize(path);

    return minioService.getPreSignedURL(
            String.format("%s/%s/%s", path, date, filename));
  }

  @SneakyThrows
  private void authorize(String path) {

    if (!LEGAL_DIRECTORIES.containsKey(path) ||
            CollUtil.intersection(JwtHelper.getScopes(), LEGAL_DIRECTORIES.get(path)).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "权限不足！");
    }
  }
}
