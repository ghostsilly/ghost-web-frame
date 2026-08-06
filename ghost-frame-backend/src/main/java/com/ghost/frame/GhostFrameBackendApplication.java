package com.ghost.frame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ghost.frame.modules")
public class GhostFrameBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(GhostFrameBackendApplication.class, args);
  }

}
