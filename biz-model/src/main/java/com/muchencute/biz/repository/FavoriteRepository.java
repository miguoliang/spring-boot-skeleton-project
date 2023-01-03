package com.muchencute.biz.repository;

import com.muchencute.biz.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface FavoriteRepository extends BaseRepository<Favorite, Long> {

  @Transactional(readOnly = true)
  boolean existsByUsernameAndTarget_TargetIdAndTarget_TargetType(String username, String targetId,
                                                                 String targetType);

  @Transactional(readOnly = true)
  Page<Favorite> findByUsernameAndTarget_TargetTypeIn(String username, Set<String> targetTypes,
                                                      Pageable pageable);

  @Transactional(readOnly = true)
  Page<Favorite> findByUsername(String username, Pageable pageable);
}