package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.common.protocol.response.ConfirmAuthenticationMessageResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 19:52
 * @description 确认消息响应
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ConfirmAuthenticationMessageResponseHandler extends SimpleChannelInboundHandler<ConfirmAuthenticationMessageResponsePacket> {
    private ConfirmAuthenticationMessageResponseHandler() {

    }

    public static final ConfirmAuthenticationMessageResponseHandler INSTANCE = new ConfirmAuthenticationMessageResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfirmAuthenticationMessageResponsePacket responsePacket) throws Exception {
        log.info("接收到审计代理发回的确认收到消息");
    }
}
