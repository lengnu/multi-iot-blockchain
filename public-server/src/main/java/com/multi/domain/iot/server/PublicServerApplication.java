package com.multi.domain.iot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 20:50
 * @description 公共服务器启动类
 */
@SpringBootApplication
public class PublicServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublicServerApplication.class,args);
    }
}
