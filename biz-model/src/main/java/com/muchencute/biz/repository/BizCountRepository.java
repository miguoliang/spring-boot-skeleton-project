package com.muchencute.biz.repository;

import com.muchencute.biz.model.BizCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BizCountRepository extends BaseRepository<BizCount, Long> {

  // 此处要忽略掉 score 字段，因为 score 字段要配合 group by 才能运行，否则会报错。
  @Transactional(readOnly = true)
  Optional<BizCount> findByTypeAndDateAndTarget_TargetIdAndTarget_TargetType(String type,
      String date,
      String targetId, String targetType);

  @Transactional(readOnly = true)
  @Query("""
      select b.date, sum(b.count) from BizCount b
      where b.type = :type and b.date between :startDate and :endDate
      group by b.date
      """)
  Collection<Object[]> countByTypeAndDateBetween(String type, String startDate, String endDate);

  @Transactional(readOnly = true)
  @Query("""
      select b.target, sum(b.count) as c from BizCount b
      where b.type in ('图谱想定文档应用', '图谱研究报告应用')
      group by b.target
      order by c desc
      """)
  Page<Object[]> applicationCountRank(Pageable pageable);

  @Transactional(readOnly = true)
  @Query("""
          select b.target, sum(b.count) as c from BizCount b
          where b.type = '图谱搜索' and b.target.targetId <> ''
          group by b.target
          order by c desc
      """)
  Page<Object[]> keywordCountRank(Pageable pageable);

  @Transactional(readOnly = true)
  @Query("""
      select b.target.targetType, sum(b.count) as c from BizCount b
      where b.type = :refererType and b.createdAt between :startDate and :endDate
      group by b.target.targetType
      order by c desc
      """)
  Collection<Object[]> countByRefererType(String refererType, Calendar startDate, Calendar endDate);

  @Transactional(readOnly = true)
  @Query("""
      select b.date, count(distinct b.target.targetId) from BizCount b
      where b.type = '搜索用户' and b.target.targetType = '用户' and b.date between :startDate and :endDate
      group by b.date
      """)
  Collection<Object[]> countSearchUser(String startDate, String endDate);
}