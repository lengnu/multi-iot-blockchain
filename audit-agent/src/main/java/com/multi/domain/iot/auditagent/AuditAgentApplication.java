package com.multi.domain.iot.auditagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 21:20
 * @description 审计代理主类
 */
@SpringBootApplication
public class AuditAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditAgentApplication.class,args);
    }
}
