
package com.ghost.frame.modules.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

  @RequestMapping("/health")
  public String health() {
    return "ok";
  }

}
