package com.multi.domain.iot.verifier.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.util.IPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 23:21
 * @description 身份验证者个人信息
 */
@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "idv")
public class IDVerifierPersonalInformation {
    private int listenPort;
    private String domainIdentity;
    private Domain domain;
    private String paramsSavePath;
    private String host = IPUtils.getLocalIp();

    @PostConstruct
    private void init() {
        try {
            this.domain = Domain.valueOf(this.domainIdentity.toUpperCase());
        } catch (Exception e) {
            log.error("域标识 [{}] 不存在，请重启程序并选择要注册的域!",this.domainIdentity);
            System.exit(1);
        }
    }
}
