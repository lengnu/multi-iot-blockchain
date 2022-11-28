package com.multi.domain.iot.ud.config;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.config
 * @Author: duwei
 * @Date: 2022/11/24 9:53
 * @Description: 配置一些通用Bean
 */
@Configuration
public class BeanConfig {
    @Bean
    public PacketCodecHandler packetCodecHandler() {
        return PacketCodecHandler.INSTANCE;
    }

}
