package com.ghost.frame.modules.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页结果类
 * @author 34592
 */
@Data
public class PageResult<T> {
  /** 
   * 当前页码 
   */
  private long current;
  /** 
   * 每页条数 
   */
  private long size;
  /**
   * 总记录数 
   */
  private long total;
  /**
   * 总页数 
   */
  private long pages;
  /** 
   * 数据列表 
   */
  private List<T> records;

  public static <T> PageResult<T> of(IPage<T> page) {
    PageResult<T> result = new PageResult<>();
    result.setCurrent(page.getCurrent());
    result.setSize(page.getSize());
    result.setTotal(page.getTotal());
    result.setPages(page.getPages());
    result.setRecords(page.getRecords());
    return result;
  }
}
