package com.multi.domain.iot.auditagent.handler.response;

import com.multi.domain.iot.auditagent.param.AuditAgentPersonalInformation;
import com.multi.domain.iot.auditagent.param.AuditAgentPersonalParams;
import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.common.role.Role;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-17 23:03
 * @description 拉去公共参数响应Handler
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FetchPublicParameterResponseHandler extends SimpleChannelInboundHandler<FetchPublicParameterResponsePacket> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private AuditAgentPersonalInformation auditAgentPersonalInformation;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            log.info("审计代理获取公共参数成功,开始本地生成自己的公私钥");
            CurveMetaProperties curveMetaProperties = responsePacket.getCurveMetaProperties();
            AuditAgentPersonalParams auditAgentPersonalParams = AuditAgentPersonalParams.getInstance(curveMetaProperties);
            auditAgentPersonalParams.store(auditAgentPersonalInformation.getParamsSavaPath(), false);
            //将个人参数放入到BeanFactory中
            this.registerAuditAgentPersonalParams(auditAgentPersonalParams);
            log.info("审计代理本地公私钥生成成功，请在文件 [{}] 下查看", auditAgentPersonalInformation.getParamsSavaPath());
            log.info("审计代理将自己的公钥、主机、监听端口号注册到公共服务器");
            ctx.writeAndFlush(wrapEnrollInformation(auditAgentPersonalParams));
        } else {
            log.error("发生未知错误，程序异常终止!");
            System.exit(1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void registerAuditAgentPersonalParams(AuditAgentPersonalParams auditAgentPersonalParams) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        beanFactory.registerSingleton("auditAgentPersonalParams", auditAgentPersonalParams);
    }

    private Packet wrapEnrollInformation(AuditAgentPersonalParams auditAgentPersonalParams) {
        RegisterParams registerParams = new RegisterParams();
        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
        registerParams.setHost(this.auditAgentPersonalInformation.getHost());
        registerParams.setListenPort(this.auditAgentPersonalInformation.getListenPort());
        registerParams.setRole(Role.AA);
        registerParams.setPublicKey(auditAgentPersonalParams.getPublicKey().toBytes());
        requestPacket.setRegisterParams(registerParams);
        return requestPacket;
    }
}
