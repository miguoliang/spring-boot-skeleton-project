package com.muchencute.biz.service;

import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.model.BizLog;
import com.muchencute.biz.model.BizLog_;
import com.muchencute.biz.repository.BizLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
@Slf4j
@Transactional(transactionManager = "bizTransactionManager")
public class LogService {

  private final BizLogRepository bizLogRepository;

  @Autowired
  public LogService(BizLogRepository bizLogRepository) {

    this.bizLogRepository = bizLogRepository;
  }

  private static Specification<BizLog> contentOrModelOrUserRoleLike(String keyword) {

    return (root, query, criteriaBuilder) ->
      StrUtil.isBlank(keyword) ?
        criteriaBuilder.and() :
        criteriaBuilder.or(
          criteriaBuilder.like(root.get(BizLog_.CONTENT), "%" + keyword + "%"),
          criteriaBuilder.and(
            criteriaBuilder.like(root.get(BizLog_.MODULE), "%" + keyword + "%")
          ),
          criteriaBuilder.and(
            criteriaBuilder.like(root.get(BizLog_.USER_ROLE), "%" + keyword + "%")
          )
        );
  }

  private static Specification<BizLog> typeEqual(String type) {

    return (root, query, criteriaBuilder) ->
      StrUtil.isNotBlank(type) ?
        criteriaBuilder.equal(root.get(BizLog_.TYPE), type) :
        criteriaBuilder.and();
  }

  private static Specification<BizLog> ipEqual(String ip) {

    return (root, query, criteriaBuilder) ->
      StrUtil.isNotBlank(ip) ?
        criteriaBuilder.equal(root.get(BizLog_.IP), ip) :
        criteriaBuilder.and();
  }

  private static Specification<BizLog> usernameEqual(String username) {

    return (root, query, criteriaBuilder) ->
      StrUtil.isNotBlank(username) ?
        criteriaBuilder.equal(root.get(BizLog_.USERNAME), username) :
        criteriaBuilder.and();
  }

  private static Specification<BizLog> betweenDate(Calendar fromDate, Calendar toDate) {

    return (root, query, criteriaBuilder) -> {
      if (fromDate == null && toDate == null) {
        return criteriaBuilder.and();
      } else if (fromDate == null) {
        return criteriaBuilder.lessThanOrEqualTo(root.get(BizLog_.CREATED_AT), toDate);
      } else if (toDate == null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get(BizLog_.CREATED_AT), fromDate);
      } else {
        return criteriaBuilder.between(root.get(BizLog_.CREATED_AT), fromDate, toDate);
      }
    };
  }

  private static Specification<BizLog> moduleIs(String module) {

    return (root, query, criteriaBuilder) ->
      StrUtil.isNotBlank(module) ? criteriaBuilder.equal(root.get(BizLog_.MODULE), module)
        : criteriaBuilder.and();
  }

  public Page<BizLog> getLogs(String q, Calendar fromDate, Calendar toDate, String type,
                              String ip, String username, String module, Pageable pageable) {

    return bizLogRepository.findAll(
      Specification
        .where(contentOrModelOrUserRoleLike(q))
        .and(betweenDate(fromDate, toDate))
        .and(typeEqual(type))
        .and(ipEqual(ip))
        .and(moduleIs(module))
        .and(usernameEqual(username)), pageable);
  }
}
