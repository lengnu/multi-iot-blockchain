package com.multi.domain.iot.ud.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.util.ComputeUtils;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.param
 * @Author: duwei
 * @Date: 2022/11/24 10:17
 * @Description: UD的个人信息
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "ud")
@ToString
@Data
public class UDPersonalInformation {
    private int listenPort;
    private String domainIdentity;
    private String name;
    private String email;
    private String id;
    private String phone;
    private Domain domain;
    private String paramsSavePath;

    @PostConstruct
    private void init() {
        try {
            this.domain = Domain.valueOf(this.domainIdentity.toUpperCase());
        } catch (Exception e) {
            log.error("域标识 [{}] 不存在，请重启程序并选择要注册的域!",this.domainIdentity);
            System.exit(1);
        }
    }

    public byte[] computeIdentityInformation(int singleInformationByteLength) {
        return ComputeUtils.convertStringToFixLengthAndConcat(singleInformationByteLength,this.name, this.id, this.email, this.phone);
    }
}
