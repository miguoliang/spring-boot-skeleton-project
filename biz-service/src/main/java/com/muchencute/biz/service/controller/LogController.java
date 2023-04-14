package com.muchencute.biz.service.controller;

import com.muchencute.biz.batch.service.client.BizBatchServiceClient;
import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.model.BizLog;
import com.muchencute.biz.model.response.OperationResponse;
import com.muchencute.biz.service.FavoriteService;
import com.muchencute.biz.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@Tag(name = "日志管理")
@RestController
@RequestMapping("log")
public class LogController {

  private final BizBatchServiceClient bizBatchServiceClient;

  private final LogService logService;

  private final FavoriteService favoriteService;

  @Autowired
  public LogController(BizBatchServiceClient bizBatchServiceClient,
                       LogService logService, FavoriteService favoriteService) {

    this.bizBatchServiceClient = bizBatchServiceClient;
    this.logService = logService;
    this.favoriteService = favoriteService;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "展示日志")
  @SneakyThrows
  public Page<BizLog> getLogs(Pageable pageable,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) String ip,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String q,
                              @RequestParam(required = false) String module,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Calendar fromDate,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Calendar toDate,
                              @RequestParam(defaultValue = "false") boolean withFavoriteFlag) {

    final var page = logService.getLogs(q, fromDate, toDate, type, ip, username, module, pageable);
    final var usernameInToken = JwtHelper.getUsername();
    page.getContent().forEach(bizLog -> {
      if (withFavoriteFlag) {
        bizLog.setFavorite(favoriteService.exists(usernameInToken, bizLog.getTarget().getTargetId(),
          bizLog.getTarget().getTargetType()));
      }
    });
    return page;
  }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('log:export')")
  @Operation(summary = "导出日志")
  public OperationResponse exportLogs(@RequestParam(required = false) String type,
                                      @RequestParam(required = false) String ip,
                                      @RequestParam(required = false) String username,
                                      @RequestParam(required = false) String q,
                                      @RequestParam(required = false) String fromDate,
                                      @RequestParam(required = false) String toDate) {

    return bizBatchServiceClient.launchExportLogsJob(type, ip, username, q, fromDate, toDate);
  }
}
