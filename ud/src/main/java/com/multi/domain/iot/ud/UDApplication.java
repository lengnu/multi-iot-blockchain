package com.multi.domain.iot.ud;

import com.multi.domain.iot.ud.model.bo.AuthenticationAddAuthenticationInformationInputBO;
import com.multi.domain.iot.ud.model.bo.AuthenticationIsAuthorizedInputBO;
import com.multi.domain.iot.ud.service.AuthenticationService;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud
 * @Author: duwei
 * @Date: 2022/11/24 9:56
 * @Description: UD的主启动类
 */
@SpringBootApplication
public class UDApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(UDApplication.class);
        AuthenticationService service = context.getBean(AuthenticationService.class);
    }
}
