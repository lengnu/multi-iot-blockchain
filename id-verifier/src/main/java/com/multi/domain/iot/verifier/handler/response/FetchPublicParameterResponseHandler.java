package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.common.role.Role;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalInformation;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalParams;
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
 * @description 拉取公共参数响应Handler
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class FetchPublicParameterResponseHandler extends SimpleChannelInboundHandler<FetchPublicParameterResponsePacket> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private IDVerifierPersonalInformation idVerifierPersonalInformation;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            log.info("身份验证者获取公共参数成功,开始本地生成自己的公私钥");
            CurveMetaProperties curveMetaProperties = responsePacket.getCurveMetaProperties();
            IDVerifierPersonalParams idVerifierPersonalParams = IDVerifierPersonalParams.getInstance(curveMetaProperties);
            //将个人参数放入到BeanFactory中
            this.registerIDVerifierPersonalParams(idVerifierPersonalParams);
            log.info("身份验证者本地公私钥生成成功");
            log.info("审计代理将自己的公钥、主机、监听端口号注册到公共服务器");
            ctx.writeAndFlush(wrapEnrollInformation(idVerifierPersonalParams));
        } else {
            log.error("发生未知错误，程序异常终止!");
            System.exit(1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void registerIDVerifierPersonalParams(IDVerifierPersonalParams idVerifierPersonalParams) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        beanFactory.registerSingleton("idVerifierPersonalParams", idVerifierPersonalParams);
    }

    private Packet wrapEnrollInformation(IDVerifierPersonalParams idVerifierPersonalParams) {
        RegisterParams registerParams = new RegisterParams();
        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
        registerParams.setHost(this.idVerifierPersonalInformation.getHost());
        registerParams.setListenPort(this.idVerifierPersonalInformation.getListenPort());
        registerParams.setRole(Role.IDV);
        registerParams.setDomain(this.idVerifierPersonalInformation.getDomain());
        registerParams.setPublicKey(idVerifierPersonalParams.getPublicKey().toBytes());
        requestPacket.setRegisterParams(registerParams);
        return requestPacket;
    }
}
