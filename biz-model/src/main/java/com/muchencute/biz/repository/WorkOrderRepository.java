package com.muchencute.biz.repository;

import com.muchencute.biz.model.WorkOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends BaseRepository<WorkOrder, Long> {

  List<WorkOrder> findByStatus(String taskStatus);
}