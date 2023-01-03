package com.muchencute.biz.repository;

import com.muchencute.biz.model.BizLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BizLogRepository extends BaseRepository<BizLog, Long> {

  Optional<BizLog> findFirstByTarget_TargetIdAndTarget_TargetTypeOrderByIDDesc(String targetId,
                                                                               String targetType);

  Page<BizLog> findByTypeInAndTarget_TargetId(Collection<String> types, String targetId,
                                              Pageable pageable);
}