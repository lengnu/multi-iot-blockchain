package com.multi.domain.iot.auditagent.param;

import com.multi.domain.iot.common.util.IPUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 21:40
 * @description 审计代理个人信息
 */
@Component
@Data
@ConfigurationProperties(prefix = "audit-agent")
public class AuditAgentPersonalInformation {
    private int listenPort;
    private String host = IPUtils.getLocalIp();
    private String paramsSavaPath;
}
