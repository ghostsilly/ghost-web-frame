package com.ghost.frame.utils;

import cn.hutool.core.util.StrUtil;
import com.ghost.frame.modules.api.entity.ApiResource;
import com.ghost.frame.modules.api.enums.ApiResourceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 后端接口资源扫描器
 * 只读容器路由，不做任何持久化；入库由 apply 接口在人工确认后完成
 * @author 34592
 */
@Component
@RequiredArgsConstructor
public class ApiResourceScannerUtil {
  
  private final ApplicationContext applicationContext;
  
  @Value("${api}")
  private String apiPrefix;

  public List<ApiResource> scan() {
    // 必须按 bean 名取：springdoc 也注册了 springDocHandlerMapping，
    // 按类型注入会抛 NoUniqueBeanDefinitionException
    RequestMappingHandlerMapping mapping = applicationContext.getBean(
        "requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

    List<ApiResource> result = new ArrayList<>();
    for (var entry : mapping.getHandlerMethods().entrySet()) {
      RequestMappingInfo info = entry.getKey();
      HandlerMethod handlerMethod = entry.getValue();
      Class<?> beanType = handlerMethod.getBeanType();

      String moduleName = resolveModule(beanType);
      String summary = resolveSummary(handlerMethod);
      String handler = beanType.getName() + "#" + handlerMethod.getMethod().getName();

      Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
      // 未限定方法，空串表示任意
      List<String> methodNames = methods.isEmpty()
          ? List.of("") : methods.stream().map(Enum::name).toList();

      for (String path : info.getPatternValues()) {
        // 排除 /v3/api-docs、/doc.html、/webjars、/error 等框架端点
        if (!path.startsWith(apiPrefix)) {
          continue;
        }
        for (String method : methodNames) {
          ApiResource r = new ApiResource();
          r.setPath(path);
          r.setMethod(method);
          r.setModule(moduleName);
          r.setSummary(summary);
          r.setHandler(handler);
          r.setStatus(ApiResourceStatus.EXISTS);
          result.add(r);
        }
      }
    }
    // 排序让每次扫描结果稳定，便于前端比对
    result.sort(Comparator.comparing(ApiResource::getPath)
        .thenComparing(ApiResource::getMethod));
    return result;
  }

  /**
   * 根据 beanType 解析模块名
   * @param beanType    bean 类型
   * @return  模块名
   */
  public String resolveModule(Class<?> beanType){
    // @Tag 是 @Repeateble，多个时 getAnnotation 返回 null，必须用 getAnnotationsByType
    Tag[] annotationsByType = beanType.getAnnotationsByType(Tag.class);
    if(annotationsByType.length > 0 && StrUtil.isNotBlank(annotationsByType[0].name())){
      return annotationsByType[0].name();
    }
    return beanType.getSimpleName();
  }

  /**
   * 根据 handlerMethod 解析接口摘要
   * @param handlerMethod 处理方法
   * @return  接口摘要
   */
  private String resolveSummary(HandlerMethod handlerMethod) {
    Operation op = handlerMethod.getMethodAnnotation(Operation.class);
    if (op != null && StrUtil.isNotBlank(op.summary())) {
      return op.summary();
    }
    return handlerMethod.getMethod().getName();
  }
}
