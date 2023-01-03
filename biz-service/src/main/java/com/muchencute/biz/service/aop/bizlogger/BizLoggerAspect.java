package com.muchencute.biz.service.aop.bizlogger;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.keycloak.model.KeycloakRole;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.KeycloakService;
import com.muchencute.biz.model.BizLog;
import com.muchencute.biz.model.BizLogTarget;
import com.muchencute.biz.repository.BizLogRepository;
import com.muchencute.biz.service.aop.resolver.Resolve;
import com.muchencute.biz.service.aop.resolver.Resolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class BizLoggerAspect {

  private final ApplicationContext applicationContext;

  private final BizLogRepository bizLogRepository;

  private final UserEntityRepository userEntityRepository;

  private final KeycloakService keycloakService;

  private final PlatformTransactionManager keycloakTransactionManager;

  @Autowired
  public BizLoggerAspect(ApplicationContext applicationContext, BizLogRepository bizLogRepository,
                         UserEntityRepository userEntityRepository, KeycloakService keycloakService,
                         @Qualifier("keycloakTransactionManager") PlatformTransactionManager keycloakTransactionManager) {

    this.applicationContext = applicationContext;
    this.bizLogRepository = bizLogRepository;
    this.userEntityRepository = userEntityRepository;
    this.keycloakService = keycloakService;
    this.keycloakTransactionManager = keycloakTransactionManager;
  }

  public static HttpServletRequest getRequest() {

    return ((ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest();
  }

  private ArrayList<String> resolveValuesNotResponse(ProceedingJoinPoint joinPoint,
                                                     Resolve... resolves) {

    final var contentValues = new ArrayList<String>();
    Arrays.stream(resolves).forEachOrdered(resolve -> {
      final var beanPath = resolve.value();
      contentValues.add(beanPath.startsWith("response") ? "" :
              getValueFromResolve(joinPoint, resolve));
    });
    return contentValues;
  }

  private void resolveValuesNeedResponse(ProceedingJoinPoint joinPoint, Object proceed,
                                         Resolve[] resolves, List<String> values) {

    for (var i = 0; i < resolves.length; i++) {
      final var beanPath = resolves[i].value();
      if (beanPath.startsWith("response")) {
        values.set(i, getValueFromResolve(joinPoint, proceed, resolves[i]));
      }
    }
  }

  private String getValueFromResolve(ProceedingJoinPoint joinPoint, Resolve resolve) {

    return getValueFromResolve(joinPoint, null, resolve);
  }

  private String getValueFromResolve(ProceedingJoinPoint joinPoint, Object proceed,
                                     Resolve resolve) {

    try {
      final var resolver = (Resolver) applicationContext.getBean(resolve.resolver());
      return resolver.getProperty(joinPoint, proceed, resolve.value()).toString();
    } catch (Exception e) {
      log.error("日志取值出错！ beanPath = {}", resolve.value(), e);
      return "";
    }
  }

  @Around("@annotation(bizLogger)")
  @SneakyThrows
  public Object around(ProceedingJoinPoint joinPoint, BizLogger bizLogger) {

    // 执行前
    final var module = bizLogger.module();
    final var type = bizLogger.type();
    final var contentFormat = bizLogger.contentFormat();
    final var contentResolves = resolveValuesNotResponse(joinPoint,
            bizLogger.contentFormatArguments());
    final var targetResolves = resolveValuesNotResponse(joinPoint, bizLogger.targetId(),
            bizLogger.targetName(), bizLogger.targetType());

    final var bizLog = new BizLog();
    final var username = JwtHelper.getUsername();
    bizLog.setUsername(username);
    bizLog.setModule(module);
    bizLog.setType(type);
    bizLog.setIp(JakartaServletUtil.getClientIP(getRequest()));

    // 函数执行
    final var proceed = joinPoint.proceed();

    // 执行后
    resolveValuesNeedResponse(joinPoint, proceed, bizLogger.contentFormatArguments(),
            contentResolves);
    resolveValuesNeedResponse(joinPoint, proceed, new Resolve[]{bizLogger.targetId(),
            bizLogger.targetName(), bizLogger.targetType()}, targetResolves);
    bizLog.setContent(String.format(contentFormat, contentResolves.toArray()));

    try {
      bizLog.setTarget(BizLogTarget.builder()
              .targetId(targetResolves.get(0))
              .targetName(targetResolves.get(1))
              .targetType(targetResolves.get(2))
              .build());

      // 因为所包裹的执行是否在事务中，在哪个事务中并不确定，并且 findAll 需要在事务中执行才能解决 lazy load 的问题
      // 所以，此处手工指定 findAll 在事务中执行。
      new TransactionTemplate(keycloakTransactionManager).executeWithoutResult(transactionStatus ->
              userEntityRepository.findByUsernameAndRealmId(username, keycloakService.getRealm())
                      .flatMap(userEntity -> userEntity.getRoles()
                              .stream()
                              .filter(KeycloakRole::getClientRole)
                              .findFirst())
                      .ifPresent(keycloakRole -> bizLog.setUserRole(keycloakRole.getName())));

      bizLogRepository.save(bizLog);
    } catch (Exception e) {
      log.error("保存日志出错！", e);
    }

    return proceed;
  }
}
