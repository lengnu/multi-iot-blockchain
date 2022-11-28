package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.protocol.request.QueryAuditAgentAndIDVerifiersRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.ud.param.UDPersonalInformation;
import com.multi.domain.iot.ud.param.UDPersonalParams;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
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
@Component
@ChannelHandler.Sharable
public class FetchPublicParameterResponseHandler extends SimpleChannelInboundHandler<FetchPublicParameterResponsePacket> implements ApplicationContextAware {
    @Autowired
    private UDPersonalInformation udPersonalInformation;
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            log.info("参与设备UD获取公共参数成功,向公共服务器查询审计代理和 [{}] 内所注册的身份验证者信息", udPersonalInformation.getDomain().getDomainIdentity());
            UDPersonalParams udPersonalParams = UDPersonalParams.getInstance(responsePacket.getCurveMetaProperties());
            udPersonalParams.setUdPersonalInformation(udPersonalInformation);
            //注册到容器中
            registerUDPersonalParams(udPersonalParams);
            ctx.writeAndFlush(new QueryAuditAgentAndIDVerifiersRequestPacket(udPersonalInformation.getDomain()));
        } else {
            log.error("发送未知错误，程序异常退出!");
            System.exit(1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void registerUDPersonalParams(UDPersonalParams udPersonalParams) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        beanFactory.registerSingleton("udPersonalParams", udPersonalParams);
    }
}
