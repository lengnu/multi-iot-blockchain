package com.multi.domain.iot.verifier.handler.request;


import com.multi.domain.iot.common.message.ConfirmMessage;
import com.multi.domain.iot.common.protocol.request.AuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.response.AuthenticationMessageResponsePacket;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalInformation;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalParams;
import com.multi.domain.iot.verifier.pool.AuditAgentConnectionPollingFactory;
import com.multi.domain.iot.verifier.session.GlobalRegisterParamsUtils;
import com.multi.domain.iot.verifier.validator.AuthenticationMessageValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/21 13:06
 * @Description: UD发送验证消息到ID-Verifier请求处理器
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class AuthenticationMessageRequestHandler extends SimpleChannelInboundHandler<AuthenticationMessageRequestPacket> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private IDVerifierPersonalInformation idVerifierPersonalInformation;
    @Autowired
    private AuditAgentConnectionPollingFactory auditAgentConnectionPollingFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthenticationMessageRequestPacket requestPacket) throws Exception {
        log.info("收到参与设备UD发来的认证信息，开始进行认证");
        IDVerifierPersonalParams idVerifierPersonalParams = this.applicationContext.getBean(IDVerifierPersonalParams.class);
        Validator validator = new AuthenticationMessageValidator(
                requestPacket.getAuthenticationMessage(),
                idVerifierPersonalParams.getCurveElementParams()
        );
        AuthenticationMessageResponsePacket responsePacket = new AuthenticationMessageResponsePacket();
        responsePacket.setVerifierId(idVerifierPersonalParams.getId());
        boolean success = validator.verify();
        responsePacket.setSuccess(success);
        if (success) {
            //认证成功，将UD的身份保护信息和份额存储在本地
            GlobalRegisterParamsUtils.bindShares(requestPacket.getAuthenticationMessage().getIdentityProtectionInformation(),
                    requestPacket.getAuthenticationMessage().getPublicKeyProtectionShares().get(idVerifierPersonalParams.getId()));
            log.info("认证信息正确，开启向审计代理发送确认消息");
           ConfirmMessage confirmMessage =  new ConfirmMessage(
                   requestPacket.getAuthenticationMessage().getPublicKeyProtectionShares().get(idVerifierPersonalParams.getId()),
                   idVerifierPersonalParams.getPrivateKey().toBytes(),
                   idVerifierPersonalParams.getCurveElementParams().getZ(),
                   requestPacket.getAuthenticationMessage().getTotalVerifiersNumber(),
                   idVerifierPersonalParams.getId(),
                   idVerifierPersonalInformation.getDomain(),
                   requestPacket.getAuthenticationMessage().getUdAddress(),
                   requestPacket.getAuthenticationMessage().getIdentityProtectionInformation()
                   );
            ConfirmAuthenticationMessageRequestPacket confirmAuthenticationMessageRequestPacket = new ConfirmAuthenticationMessageRequestPacket(confirmMessage);
            InetSocketAddress auditAgentAddress = GlobalRegisterParamsUtils.getAuditAgentAddress();
            auditAgentConnectionPollingFactory.sendMessage(auditAgentAddress,confirmAuthenticationMessageRequestPacket);
        } else {
            log.error("认证信息错误");
        }
        ctx.writeAndFlush(responsePacket);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
