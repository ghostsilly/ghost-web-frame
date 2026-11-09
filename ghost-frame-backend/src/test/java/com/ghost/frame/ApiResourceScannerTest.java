package com.ghost.frame;

import com.ghost.frame.modules.api.entity.ApiResource;
import com.ghost.frame.utils.ApiResourceScannerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApiResourceScannerTest {
  
  @Autowired
  private ApiResourceScannerUtil apiResourceScannerUtil;
  
  @Test
  void testScan() {
    List<ApiResource> apiResources = apiResourceScannerUtil.scan();
    for (ApiResource apiResource : apiResources) {
      System.out.println(apiResource.getPath());
    }
  }
}
