package com.multi.domain.iot.server.handler;

import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.protocol.request.FetchPublicParameterRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/17 16:45
 * @Description: 客户请求公共参数处理器
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FetchPublicParameterRequestHandler extends SimpleChannelInboundHandler<FetchPublicParameterRequestPacket> {
    @Autowired
    private CurveMetaProperties curveMetaProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterRequestPacket requestPacket) throws Exception {
        FetchPublicParameterResponsePacket responsePacket = new FetchPublicParameterResponsePacket();
        responsePacket.setCurveMetaProperties(curveMetaProperties);
        responsePacket.setSuccess(true);
        ctx.writeAndFlush(responsePacket);
    }
}
