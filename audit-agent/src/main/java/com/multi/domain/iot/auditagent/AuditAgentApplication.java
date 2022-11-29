package com.multi.domain.iot.auditagent;

import com.multi.domain.iot.auditagent.model.bo.AuthenticationAddAuthenticationInformationInputBO;
import com.multi.domain.iot.auditagent.service.AuthenticationService;
import com.multi.domain.iot.common.message.BlockChainRegisterMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigInteger;
import java.net.InetSocketAddress;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 21:20
 * @description 审计代理主类
 */
@SpringBootApplication
public class AuditAgentApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(AuditAgentApplication.class, args);

    }
}
