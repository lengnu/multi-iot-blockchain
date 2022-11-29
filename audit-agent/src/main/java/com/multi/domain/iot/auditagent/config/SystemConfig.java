package com.multi.domain.iot.auditagent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(
    prefix = "system"
)
public class SystemConfig {
  private String peers;

  private int groupId;

  private String certPath;

  private String hexPrivateKey;

  @NestedConfigurationProperty
  private ContractConfig contract;
}
