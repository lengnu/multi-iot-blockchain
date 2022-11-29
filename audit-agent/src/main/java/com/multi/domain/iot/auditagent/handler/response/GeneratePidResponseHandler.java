package com.multi.domain.iot.auditagent.handler.response;

import com.multi.domain.iot.auditagent.service.AuthenticationService;
import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.protocol.response.GeneratePidResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:30
 * @description UD验证AA生成的PID之后，返回是否成功，然后AA执行下面处理
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class GeneratePidResponseHandler extends SimpleChannelInboundHandler<GeneratePidResponsePacket> {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GeneratePidResponsePacket responsePacket) throws Exception {
        String pid = Base64.encodeBase64String(responsePacket.getPid());
        boolean success = responsePacket.isSuccess();
        if (success && ConfirmAuthenticationMessageSessionUtils.isWaitUpToBlockChain(pid)) {
            boolean registerBlockChain = authenticationService.addAuthenticationInformation(ConfirmAuthenticationMessageSessionUtils.getBlockChainRegisterMessage(pid));
            ConfirmAuthenticationMessageSessionUtils.removePidFromWaitUpToBlockChainTemporaryMap(pid);
            if (registerBlockChain){
                log.info("pid登记上链成功");
            }else {
                log.error("pid登记上链失败");
            }
        } else {
            log.error(responsePacket.getReason());
        }
        log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
