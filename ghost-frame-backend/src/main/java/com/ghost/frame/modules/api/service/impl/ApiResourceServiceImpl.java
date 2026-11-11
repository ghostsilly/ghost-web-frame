package com.ghost.frame.modules.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.frame.modules.api.dto.ApiResourceApplyDTO;
import com.ghost.frame.modules.api.entity.ApiResource;
import com.ghost.frame.modules.api.enums.ApiResourceDiffType;
import com.ghost.frame.modules.api.enums.ApiResourceStatus;
import com.ghost.frame.modules.api.mapper.ApiResourceMapper;
import com.ghost.frame.modules.api.service.ApiResourceService;
import com.ghost.frame.modules.api.vo.ApiResourceApplyResultVO;
import com.ghost.frame.modules.api.vo.ApiResourceDiffItemVO;
import com.ghost.frame.modules.api.vo.ApiResourceDiffVO;
import com.ghost.frame.modules.api.vo.FieldChangeVO;
import com.ghost.frame.modules.common.enums.DeleteFlag;
import com.ghost.frame.utils.ApiResourceScannerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API 资源实现类
 * @author 34592
 */
@Service
public class ApiResourceServiceImpl extends ServiceImpl<ApiResourceMapper, ApiResource> implements ApiResourceService {
  @Autowired
  private ApiResourceScannerUtil apiResourceScannerUtil;
  @Autowired
  private ApiResourceMapper apiResourceMapper;

  @Override
  public ApiResourceDiffVO diff() {
    List<ApiResource> scanned = apiResourceScannerUtil.scan();
    // 自动带 deleted=0
    List<ApiResource> dbList = apiResourceMapper.selectList(null);   

    Map<String, ApiResource> dbMap = dbList.stream()
        .collect(
            Collectors.toMap(
                this::keyOf, 
                r -> r, 
                (a, b) -> a
            )
        );

    ApiResourceDiffVO vo = new ApiResourceDiffVO();
    vo.setScanTime(LocalDateTime.now());
    vo.setScannedCount(scanned.size());
    vo.setDbCount(dbList.size());

    Set<String> scannedKeys = new HashSet<>();
    int unchanged = 0;

    for (ApiResource s : scanned) {
      String key = keyOf(s);
      scannedKeys.add(key);
      ApiResource db = dbMap.get(key);

      if (db == null) {
        vo.getAdded().add(toItem(ApiResourceDiffType.ADDED, null, s, List.of()));
        continue;
      }
      List<FieldChangeVO> changes = compare(db, s);
      if (changes.isEmpty()) {
        unchanged++;
      } else {
        vo.getModified().add(toItem(ApiResourceDiffType.MODIFIED, db, s, changes));
      }
    }

    // 表里有、容器已无 → 删除类
    for (ApiResource db : dbList) {
      if (!scannedKeys.contains(keyOf(db))) {
        vo.getRemoved().add(toItem(ApiResourceDiffType.REMOVED, db, null, List.of()));
      }
    }

    vo.setUnchangedCount(unchanged);
    return vo;
  }

  /**
   * 应用差异
   * @param dto 应用差异的DTO
   * @return 应用结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ApiResourceApplyResultVO apply(ApiResourceApplyDTO dto) {
    int inserted = 0, updated = 0, removed = 0, skipped = 0;

    // 新增
    for (ApiResource r : dto.getInserts()) {
      // 幂等保护：扫描到应用之间若已被别人加过，跳过而不是抛唯一索引冲突
      Long count = apiResourceMapper.selectCount(new LambdaQueryWrapper<ApiResource>()
          .eq(ApiResource::getPath, r.getPath())
          .eq(ApiResource::getMethod, StrUtil.nullToEmpty(r.getMethod())));
      if (count != null && count > 0) { skipped++; continue; }
      // 防止前端传了 id 变成指定主键插入
      r.setId(null);
      r.setStatus(ApiResourceStatus.EXISTS);
      r.setDeleted(DeleteFlag.NOT_DELETED);
      apiResourceMapper.insert(r);
      inserted++;
    }

    // 更新：只覆盖扫描管的字段，remark 一律不动
    for (ApiResource r : dto.getUpdates()) {
      if (r.getId() == null) { skipped++; continue; }
      ApiResource db = apiResourceMapper.selectById(r.getId());
      // 已被其他人删掉
      if (db == null) { skipped++; continue; }

      ApiResource patch = new ApiResource();
      patch.setId(r.getId());
      patch.setModule(r.getModule());
      patch.setSummary(r.getSummary());
      patch.setHandler(r.getHandler());
      patch.setStatus(ApiResourceStatus.EXISTS);
      // null 字段不更新，remark 得以保留
      apiResourceMapper.updateById(patch);
      updated++;
    }

    // 删除：只置状态，保留记录，避免权限配置变成孤儿引用
    for (Long id : dto.getRemoveIds()) {
      if (id == null) {
        continue;
      }
      ApiResource patch = new ApiResource();
      patch.setId(id);
      patch.setStatus(ApiResourceStatus.REMOVED);
      apiResourceMapper.updateById(patch);
      removed++;
    }

    ApiResourceApplyResultVO vo = new ApiResourceApplyResultVO();
    vo.setInserted(inserted);
    vo.setUpdated(updated);
    vo.setRemoved(removed);
    vo.setSkipped(skipped);
    return vo;
  }

  @Override
  public List<ApiResource> listAll() {
    return apiResourceMapper.selectList(null);
  }

  /** 
   * 路径 + 方法构成唯一键，与 uk_ar_path_method 一致 
   */
  private String keyOf(ApiResource r) {
    return r.getPath() + "|" + StrUtil.nullToEmpty(r.getMethod());
  }

  /**
   * 只比较会随代码变化的四个字段，remark 是人工维护的，不能被扫描覆盖
   */
  private List<FieldChangeVO> compare(ApiResource db, ApiResource s) {
    List<FieldChangeVO> list = new ArrayList<>();
    addIfChanged(list, "module",  db.getModule(),  s.getModule());
    addIfChanged(list, "summary", db.getSummary(), s.getSummary());
    addIfChanged(list, "handler", db.getHandler(), s.getHandler());
    // 扫描结果恒为 EXISTS，所以库里是 REMOVED 就说明接口恢复了
    if (db.getStatus() != ApiResourceStatus.EXISTS) {
      list.add(new FieldChangeVO(
          "status",
          db.getStatus().getDesc(), 
          ApiResourceStatus.EXISTS.getDesc()
      ));
    }
    return list;
  }

  /**
   * 如果字段有变更则添加到列表
   * @param list 列表
   * @param field 字段名
   * @param oldV  旧值
   * @param newV  新值
   */
  private void addIfChanged(List<FieldChangeVO> list, String field, Object oldV, Object newV) {
    if (!Objects.equals(oldV, newV)) {
      list.add(new FieldChangeVO(field,
          oldV == null ? null : String.valueOf(oldV),
          newV == null ? null : String.valueOf(newV)));
    }
  }

  /**
   * 组装差异项
   *
   * @param diffType 差异类型
   * @param db       表中现有记录；ADDED 时为 null
   * @param scanned  本次扫描结果；REMOVED 时为 null
   * @param changes  字段级变更明细；仅 MODIFIED 有值
   */
  private ApiResourceDiffItemVO toItem(
      ApiResourceDiffType diffType,
      ApiResource db,
      ApiResource scanned,
      List<FieldChangeVO> changes
  ){
    // 展示值优先取扫描结果，接口已从容器移除时回退到表中旧值
    ApiResource source = scanned != null ? scanned : db;

    ApiResourceDiffItemVO item = new ApiResourceDiffItemVO();
    item.setDiffType(diffType);
    item.setId(db == null ? null : db.getId());
    item.setPath(source != null ? source.getPath() : null);
    item.setMethod(source != null ? source.getMethod() : null);
    item.setModule(source != null ? source.getModule() : null);
    item.setSummary(source != null ? source.getSummary() : null);
    item.setHandler(source != null ? source.getHandler() : null);
    item.setDbStatus(db == null ? null : db.getStatus());
    item.setChanges(changes == null ? new ArrayList<>() : changes);
    return item;
  }
}
