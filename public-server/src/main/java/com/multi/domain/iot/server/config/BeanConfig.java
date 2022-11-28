package com.multi.domain.iot.server.config;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.param.CurveMetaProperties;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 20:39
 * @description 配置通用Bean
 */
@Configuration
public class BeanConfig {
    @Bean
    public PacketCodecHandler packetCodecHandler() {
        return PacketCodecHandler.INSTANCE;
    }

    @Bean
    public LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder() {
        return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4);
    }

    @Bean
    public CurveMetaProperties curveMetaProperties(
            @Value("${curveMetaPropertiesPath}") String curveMetaPropertiesPath){
        return new CurveMetaProperties(curveMetaPropertiesPath);
    }
}
