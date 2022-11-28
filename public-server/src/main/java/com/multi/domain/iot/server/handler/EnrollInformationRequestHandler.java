package com.multi.domain.iot.server.handler;

import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
import com.multi.domain.iot.common.role.Role;
import com.multi.domain.iot.server.session.GlobalRegisterParamsUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/18 15:57
 * @Description: 处理各个客户端的注册信息
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class EnrollInformationRequestHandler extends SimpleChannelInboundHandler<EnrollInformationRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationRequestPacket requestPacket) throws Exception {
        int id = GlobalRegisterParamsUtils.register(requestPacket.getRegisterParams());
        EnrollInformationResponsePacket packet = new EnrollInformationResponsePacket();
        if (id < 0) {
            packet.setSuccess(false);
            packet.setReason("审计代理只能存在一个，不能重复注册");
        } else {
            packet.setId(id);
            packet.setSuccess(true);
        }
        if (requestPacket.getRegisterParams().getRole() == Role.IDV) {
            packet.setAuditAgentRegisterParams(GlobalRegisterParamsUtils.getAuditAgentRegisterParams());
        }
        ctx.writeAndFlush(packet);
    }

}
