package com.muchencute.biz.service.controller;

import com.muchencute.biz.batch.service.client.BizBatchServiceClient;
import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.model.WorkOrder;
import com.muchencute.biz.model.response.OperationResponse;
import com.muchencute.biz.service.WorkOrderService;
import com.muchencute.biz.service.aop.bizlogger.BizLogger;
import com.muchencute.biz.service.aop.resolver.GatNameByWorkOrderIdInPathResolver;
import com.muchencute.biz.service.aop.resolver.Resolve;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workOrders")
@Tag(name = "工单")
@Transactional(transactionManager = "bizTransactionManager")
public class WorkOrderController {

  private final WorkOrderService workOrderService;

  private final BizBatchServiceClient bizBatchServiceClient;

  @Autowired
  public WorkOrderController(WorkOrderService workOrderService,
                             BizBatchServiceClient bizBatchServiceClient) {

    this.workOrderService = workOrderService;
    this.bizBatchServiceClient = bizBatchServiceClient;
  }

  @BizLogger(module = "任务管理", type = "新建",
          contentFormat = "新建任务工单【%s】",
          contentFormatArguments = @Resolve("request.body.name"),
          targetId = @Resolve("request.body.ID"),
          targetName = @Resolve("request.body.name"),
          targetType = @Resolve("'workOrders'"))
  @Operation(summary = "新增工单")
  @PostMapping
  @PreAuthorize("hasAnyAuthority('work_order:crud')")
  @SneakyThrows
  public WorkOrder newWorkOrder(@RequestBody WorkOrder workOrder) {

    workOrder.setCreator(JwtHelper.getUsername());
    return workOrderService.newWorkOrder(workOrder);
  }

  @BizLogger(module = "任务管理", type = "编辑",
          contentFormat = "编辑任务工单【%s】",
          contentFormatArguments = @Resolve("request.body.name"),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve("request.body.name"),
          targetType = @Resolve("'workOrders'"))
  @Operation(summary = "修改工单")
  @PutMapping("{id}")
  @PreAuthorize("hasAnyAuthority('work_order:crud')")
  public WorkOrder updateWorkOrder(@PathVariable Long id, @RequestBody WorkOrder workOrder) {

    return workOrderService.updateWorkOrder(id, workOrder);
  }

  @Operation(summary = "根据id查询工单")
  @GetMapping("{id}")
  @PreAuthorize("hasAnyAuthority('work_order:read')")
  public WorkOrder getWorkOrder(@PathVariable Long id) {

    return workOrderService.getWorkOrder(id);
  }

  @Operation(summary = "查询所有工单")
  @GetMapping
  @PreAuthorize("hasAnyAuthority('work_order:read')")
  @Transactional(transactionManager = "bizTransactionManager")
  public Page<WorkOrder> getWorkOrders(Pageable pageable) {

    return workOrderService.getWorkOrders(pageable);
  }

  @BizLogger(
          type = "删除",
          module = "任务管理",
          contentFormat = "删除任务【%s】",
          contentFormatArguments = @Resolve(value = "request.path.id", resolver = GatNameByWorkOrderIdInPathResolver.class),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve(value = "request.path.id", resolver = GatNameByWorkOrderIdInPathResolver.class),
          targetType = @Resolve("'workOrders'")
  )
  @Operation(summary = "删除工单")
  @DeleteMapping("{id}")
  @PreAuthorize("hasAnyAuthority('work_order:crud')")
  public void deleteWorkOrder(@PathVariable Long id) {

    workOrderService.deleteWorkOrder(id);
  }

  @Operation(summary = "导出工单列表")
  @PostMapping("export")
  @PreAuthorize("hasAnyAuthority('work_order:import_and_export')")
  public OperationResponse exportWorkOrders() {

    return bizBatchServiceClient.launchExportWorkOrdersJob();
  }
}
