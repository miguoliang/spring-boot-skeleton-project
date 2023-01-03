package com.muchencute.biz.service.controller;

import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.model.Favorite;
import com.muchencute.biz.repository.BizLogRepository;
import com.muchencute.biz.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "用户收藏管理")
@RestController
@RequestMapping(path = "/favorite")
@Slf4j
public class FavoriteController {

  private final FavoriteService favoriteService;

  private final BizLogRepository bizLogRepository;

  @Autowired
  public FavoriteController(FavoriteService favoriteService, BizLogRepository bizLogRepository) {

    this.favoriteService = favoriteService;
    this.bizLogRepository = bizLogRepository;
  }

  @Operation(summary = "查询目标收藏状态")
  @RequestMapping(method = RequestMethod.HEAD)
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public ResponseEntity<?> statFavorite(@RequestParam String targetId,
                                        @RequestParam String targetType) {

    final var username = JwtHelper.getUsername();
    return favoriteService.exists(username, targetId, targetType)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
  }

  @Operation(summary = "用户收藏列表查询")
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public Page<Favorite> getFavorites(
          @RequestParam(required = false, name = "type", defaultValue = "") Set<String> types,
          @RequestParam(defaultValue = "false") boolean withLog,
          Pageable pageable) {

    final var page = favoriteService.getFavorites(JwtHelper.getUsername(), types, pageable);
    page.getContent().forEach(favorite -> {
      if (withLog) {
        bizLogRepository.findFirstByTarget_TargetIdAndTarget_TargetTypeOrderByIDDesc(
                        favorite.getTarget().getTargetId(), favorite.getTarget().getTargetType())
                .ifPresent(bizLog -> {
                  favorite.setLogContent(bizLog.getContent());
                  favorite.setLogCreatedAt(bizLog.getCreatedAt());
                });
      }
    });
    return page;
  }

  @Operation(summary = "添加收藏")
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public Favorite newFavorite(@RequestBody Favorite favorite) {

    favorite.setUsername(JwtHelper.getUsername());
    return favoriteService.newFavorite(favorite);
  }

  @Operation(summary = "删除收藏")
  @DeleteMapping
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public void deleteFavorite(@RequestParam String targetId, @RequestParam String targetType) {

    favoriteService.getFavorite(JwtHelper.getUsername(), targetId, targetType)
            .ifPresent(favoriteService::deleteFavorite);
  }
}
