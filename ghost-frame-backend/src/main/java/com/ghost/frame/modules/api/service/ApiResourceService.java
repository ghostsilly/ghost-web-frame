package com.ghost.frame.modules.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.frame.modules.api.dto.ApiResourceApplyDTO;
import com.ghost.frame.modules.api.entity.ApiResource;
import com.ghost.frame.modules.api.vo.ApiResourceApplyResultVO;
import com.ghost.frame.modules.api.vo.ApiResourceDiffVO;

import java.util.List;

/**
 * API资源服务类
 * @author 34592
 */
public interface ApiResourceService extends IService<ApiResource> {

  ApiResourceDiffVO diff();

  ApiResourceApplyResultVO apply(ApiResourceApplyDTO dto);

  List<ApiResource> listAll();
}
