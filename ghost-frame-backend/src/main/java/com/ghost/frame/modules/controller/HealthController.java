
package com.ghost.frame.modules.controller;

import com.ghost.frame.common.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

  @RequestMapping("/health")
  public Response<String> health() {
    return Response.success("ok");
  }

}
