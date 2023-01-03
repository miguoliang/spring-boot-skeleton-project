package com.muchencute.biz.service;

import cn.hutool.core.collection.CollUtil;
import com.muchencute.biz.model.BizLogTarget;
import com.muchencute.biz.model.Favorite;
import com.muchencute.biz.repository.FavoriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;

  @Autowired
  @Lazy
  public FavoriteService(FavoriteRepository favoriteRepository) {

    this.favoriteRepository = favoriteRepository;
  }

  public Favorite newFavorite(Favorite favorite) {

    return favoriteRepository.save(favorite);
  }

  public void deleteFavorite(Favorite favorite) {

    favoriteRepository.delete(favorite);
  }

  public Page<Favorite> getFavorites(String username, Set<String> targetTypes, Pageable pageable) {

    return CollUtil.isEmpty(targetTypes)
            ? favoriteRepository.findByUsername(username, pageable)
            : favoriteRepository.findByUsernameAndTarget_TargetTypeIn(username, targetTypes, pageable);
  }

  public Optional<Favorite> getFavorite(String username, String targetId, String targetType) {

    final var example = Example.of(
            Favorite.builder()
                    .username(username)
                    .target(BizLogTarget.builder()
                            .targetId(targetId)
                            .targetType(targetType)
                            .build())
                    .build()
    );
    return favoriteRepository.findOne(example);
  }

  public boolean exists(String username, String targetId, String targetType) {

    return favoriteRepository.existsByUsernameAndTarget_TargetIdAndTarget_TargetType(
            username, targetId, targetType);
  }
}
