package com.muchencute.biz.service;

import cn.hutool.core.bean.BeanUtil;
import com.muchencute.biz.model.WorkOrder;
import com.muchencute.biz.repository.WorkOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("bizTransactionManager")
public class WorkOrderService {

  private final WorkOrderRepository workOrderRepository;

  @Autowired
  public WorkOrderService(WorkOrderRepository workOrderRepository) {

    this.workOrderRepository = workOrderRepository;
  }

  public WorkOrder newWorkOrder(WorkOrder workOrder) {

    return workOrderRepository.save(workOrder);
  }

  public WorkOrder getWorkOrder(Long id) {

    return workOrderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("工单不存在！"));
  }

  public Page<WorkOrder> getWorkOrders(Pageable pageable) {

    return workOrderRepository.findAll(pageable);
  }

  public WorkOrder updateWorkOrder(Long id, WorkOrder workOrder) {

    final var entity = workOrderRepository.findById(id).orElseThrow();
    BeanUtil.copyProperties(workOrder, entity, "ID", "creator", "createdAt", "updatedAt");
    return workOrderRepository.save(entity);
  }

  public void deleteWorkOrder(Long id) {

    workOrderRepository.deleteById(id);
  }
}
