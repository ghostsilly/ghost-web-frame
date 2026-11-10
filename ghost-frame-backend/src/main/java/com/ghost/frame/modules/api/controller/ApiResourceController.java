package com.ghost.frame.modules.api.controller;

import com.ghost.frame.common.Response;
import com.ghost.frame.modules.api.dto.ApiResourceApplyDTO;
import com.ghost.frame.modules.api.entity.ApiResource;
import com.ghost.frame.modules.api.service.ApiResourceService;
import com.ghost.frame.modules.api.vo.ApiResourceApplyResultVO;
import com.ghost.frame.modules.api.vo.ApiResourceDiffVO;
import com.ghost.frame.modules.common.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API资源控制器
 * @author 34592
 */
@RestController
@RequestMapping("${api}/api-resource")
@Tag(name = "API资源管理模块", description = "扫描容器API并人工确认后入库")
@RequiredArgsConstructor
public class ApiResourceController extends BaseController {

  private final ApiResourceService apiResourceService;

  @Operation(summary = "扫描并比对接口差异（只读，不写库）")
  @PostMapping("/scan-diff")
  public Response<ApiResourceDiffVO> scanDiff() {
    getUserId();
    return Response.success(apiResourceService.diff());
  }

  @Operation(summary = "应用已确认的接口差异")
  @PostMapping("/apply")
  public Response<ApiResourceApplyResultVO> apply(@Valid @RequestBody ApiResourceApplyDTO dto) {
    getUserId();
    return Response.success(apiResourceService.apply(dto));
  }

  @Operation(summary = "查询接口资源列表")
  @GetMapping("/list")
  public Response<List<ApiResource>> list() {
    getUserId();
    return Response.success(apiResourceService.listAll());
  }
}
