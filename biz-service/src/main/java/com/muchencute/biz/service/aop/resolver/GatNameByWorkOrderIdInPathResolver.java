package com.muchencute.biz.service.aop.resolver;

import com.muchencute.biz.repository.WorkOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GatNameByWorkOrderIdInPathResolver implements Resolver {

  private final WorkOrderRepository workOrderRepository;

  @Autowired
  public GatNameByWorkOrderIdInPathResolver(WorkOrderRepository workOrderRepository) {

    this.workOrderRepository = workOrderRepository;
  }

  @Override
  public Object getProperty(ProceedingJoinPoint joinPoint, Object proceed, String beanPath) {

    final var value = DefaultResolver.getValueFromRequestPath(beanPath);
    final var workOrder = workOrderRepository.findById(Long.parseLong(value.toString()));
    return workOrder.isPresent() ? workOrder.get().getName() : "";
  }
}
