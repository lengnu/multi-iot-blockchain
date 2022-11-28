package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.message.AuthenticationMessage;
import com.multi.domain.iot.common.pool.ConnectionPool;
import com.multi.domain.iot.common.protocol.request.AuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.ud.session.GlobalRegisterParamsUtils;
import com.multi.domain.iot.ud.param.UDPersonalInformation;
import com.multi.domain.iot.ud.param.UDPersonalParams;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:59
 * @description 查询AA和IDV在服务器的注册响应处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class QueryAuditAgentAndIDVerifiersResponseHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersResponsePacket> implements ApplicationContextAware {

    @Autowired
    private UDPersonalInformation udPersonalInformation;
    private ApplicationContext applicationContext;
    @Autowired
    private ConnectionPool connectionPool;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersResponsePacket responsePacket) throws Exception {
        if (!responsePacket.isSuccess()) {
            log.error(responsePacket.getReason());
            log.error("请等审计代理和身份验证者启动后再重新注册");
            System.exit(1);
        }
        log.info("参与设备UD获取公询审计代理和 [{}] 内身份验证者信息成功，开始本地计算认证信息",udPersonalInformation.getDomain().getDomainIdentity());
        GlobalRegisterParamsUtils.storeRegisterParams(responsePacket);
        UDPersonalParams udPersonalParams = this.applicationContext.getBean(UDPersonalParams.class);
        AuthenticationMessage authenticationMessage = udPersonalParams.computeAuthenticationParams();
        AuthenticationMessageRequestPacket request = new AuthenticationMessageRequestPacket();
        request.setAuthenticationMessage(authenticationMessage);
        log.info("参与设备UD验证信息生成完毕，开始将消息发送到每个身份验证者");
        connectionPool.broadcastMessage(GlobalRegisterParamsUtils.getIDVerifiersAddress().values(),request);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
