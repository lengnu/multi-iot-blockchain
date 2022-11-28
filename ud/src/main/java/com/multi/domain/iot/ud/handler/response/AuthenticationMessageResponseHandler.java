package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.protocol.response.AuthenticationMessageResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.handler.response
 * @Author: duwei
 * @Date: 2022/11/21 14:26
 * @Description: UD收到IDVerifier的是否通过验证消息
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class AuthenticationMessageResponseHandler extends SimpleChannelInboundHandler<AuthenticationMessageResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthenticationMessageResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            log.info("收到来自身份验证者 [{}] 的认证通过消息", responsePacket.getVerifierId());
        } else {
            log.error("收到来自身份验证者 [{}] 的认证失败消息，请检查是否已经完成认证或认证信息生成过程是否正确", responsePacket.getVerifierId());
        }
    }
}
