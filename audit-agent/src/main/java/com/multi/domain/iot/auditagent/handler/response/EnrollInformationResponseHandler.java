package com.multi.domain.iot.auditagent.handler.response;

import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-19 19:37
 * @description 注册信息响应
 */
@Slf4j
@Component
public class EnrollInformationResponseHandler extends SimpleChannelInboundHandler<EnrollInformationResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()){
            log.info("审计代理信息注册成功");
        }else {
            log.error(responsePacket.getReason());
            log.error("程序异常退出");
            System.exit(1);
        }
    }
}
