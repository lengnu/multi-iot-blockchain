package com.multi.domain.iot.server.handler;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.request.QueryAuditAgentAndIDVerifiersRequestPacket;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.server.session.GlobalRegisterParamsUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:54
 * @description 查询AA和IDV在服务器的注册的信息
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class QueryAuditAgentAndIDVerifiersRequestHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersRequestPacket requestPacket) throws Exception {
        Domain domain = requestPacket.getDomain();
        log.info("检测到 [{}] 域内的参与设备UD查询该域下的身份验证者和审计代理信息", domain.getDomainIdentity());
        QueryAuditAgentAndIDVerifiersResponsePacket responsePacket = new QueryAuditAgentAndIDVerifiersResponsePacket();
        if (GlobalRegisterParamsUtils.containsDomain(domain)
                && GlobalRegisterParamsUtils.containAuditAgent()) {
            responsePacket.setSuccess(true);
            responsePacket.setAuditRegisterParams(GlobalRegisterParamsUtils.getAuditAgentRegisterParams());
            responsePacket.setIdVerifierRegisterParams(GlobalRegisterParamsUtils.getDomainIDVerifiersRegisterParams(domain));
        } else {
            responsePacket.setSuccess(false);
            responsePacket.setReason("审计代理或 [" + domain.getDomainIdentity() + "] 域内身份验证者未注册");
        }
        ctx.writeAndFlush(responsePacket);
    }
}
