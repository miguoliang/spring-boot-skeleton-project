package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EventEntityRepository extends JpaRepository<EventEntity, String> {

  Optional<EventEntity> findFirstByUserIdAndTypeIsOrderByEventTimeDesc(String userId, String type);

  @Transactional(readOnly = true)
  @Query("""
    select e.ipAddress, count(e) as c from EventEntity e
    where e.userId = :userId and e.type in ('LOGIN', 'LOGIN_ERROR')
    group by e.ipAddress
    order by c desc
    """)
  List<Object[]> countByIpAddress(String userId);
}