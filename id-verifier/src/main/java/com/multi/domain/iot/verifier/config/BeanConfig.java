package com.multi.domain.iot.verifier.config;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 21:15
 * @description 通用Bean配置类
 */
@Configuration
public class BeanConfig {
    @Bean
    public PacketCodecHandler packetCodecHandler(){
        return PacketCodecHandler.INSTANCE;
    }
}
