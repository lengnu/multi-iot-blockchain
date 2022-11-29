package com.multi.domain.iot.auditagent.handler.request;

import com.multi.domain.iot.auditagent.calculator.PidCalculator;
import com.multi.domain.iot.auditagent.param.AuditAgentPersonalParams;
import com.multi.domain.iot.auditagent.pool.UDConnectionPoolingFactory;
import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.message.BlockChainRegisterMessage;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.request.GeneratePidRequestPacket;
import com.multi.domain.iot.common.protocol.response.ConfirmAuthenticationMessageResponsePacket;
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
import java.util.concurrent.TimeUnit;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.handler.request
 * @Author: duwei
 * @Date: 2022/11/22 15:43
 * @Description: 处理来自IDV的验证消息
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class ConfirmAuthenticationMessageRequestHandler extends SimpleChannelInboundHandler<ConfirmAuthenticationMessageRequestPacket> implements ApplicationContextAware {

    @Autowired
    private UDConnectionPoolingFactory udConnectionPoolingFactory;
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfirmAuthenticationMessageRequestPacket requestPacket) {
        byte[] identityProtectionInformation = requestPacket.getConfirmMessage().getIdentityProtectionInformation();
        Integer verifierId = requestPacket.getConfirmMessage().getVerifierId();
        byte[] alpha = requestPacket.getConfirmMessage().getAlpha();
        Domain domain = requestPacket.getConfirmMessage().getDomain();
        int totalConfirmNumber = requestPacket.getConfirmMessage().getTotalIDVerifiersNumber();
        String uid = Base64.encodeBase64String(identityProtectionInformation);
        //要将PID返回给UD的地址
        InetSocketAddress udAddress = requestPacket.getConfirmMessage().getUdAddress();
        //某个UD还没有生成PID
        if (!ConfirmAuthenticationMessageSessionUtils.isGeneratePID(uid)) {
            //关于某个UD-IDV第一次发来确认消息
            if (ConfirmAuthenticationMessageSessionUtils.isNotStartConfirm(uid)) {
                synchronized (ConfirmAuthenticationMessageSessionUtils.class) {
                    if (ConfirmAuthenticationMessageSessionUtils.isNotStartConfirm(uid)) {
                        ConfirmAuthenticationMessageSessionUtils.startCountConfirmMessage(uid);
                        //2.开启定时任务，5s后判断是否收集完毕
                        ctx.channel().eventLoop().schedule(() -> {
                            //根据具体情况生成PID，然后发送给对应的UD
                            boolean waitEnoughConfirmMessage = ConfirmAuthenticationMessageSessionUtils.isWaitEnoughConfirmMessage(uid, totalConfirmNumber);
                            AuditAgentPersonalParams auditAgentPersonalParams = this.applicationContext.getBean(AuditAgentPersonalParams.class);
                            GeneratePidRequestPacket generatePidRequestPacket = PidCalculator.calculatePIDPacket(uid, identityProtectionInformation, auditAgentPersonalParams, waitEnoughConfirmMessage);
                            //做善后工作，主要是清除临时缓存，并将需要上链信息临时缓存，等待UD发来的确认消息然后上链
                            String pid = Base64.encodeBase64String(generatePidRequestPacket.getPidAndSignMessage().getPid());
                            ConfirmAuthenticationMessageSessionUtils.afterMath(
                                    uid,
                                    pid,
                                    new BlockChainRegisterMessage(pid, uid, udAddress),
                                    generatePidRequestPacket.isSuccess()
                            );

                            try {
                                udConnectionPoolingFactory.sendMessage(udAddress, generatePidRequestPacket);
                            } catch (InterruptedException ignored) {
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            }
            ConfirmAuthenticationMessageSessionUtils.receiveOneConfirmMessage(
                    uid, verifierId, alpha, totalConfirmNumber, domain
            );
        }
        ctx.writeAndFlush(new ConfirmAuthenticationMessageResponsePacket());

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

