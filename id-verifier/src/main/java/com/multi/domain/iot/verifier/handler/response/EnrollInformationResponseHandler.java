package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalInformation;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalParams;
import com.multi.domain.iot.verifier.session.GlobalRegisterParamsUtils;
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
 * @create 2022-11-19 19:37
 * @description 注册信息响应
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class EnrollInformationResponseHandler extends SimpleChannelInboundHandler<EnrollInformationResponsePacket>  implements ApplicationContextAware{
   @Autowired
   private IDVerifierPersonalInformation idVerifierPersonalInformation;
   private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()){
            log.info("身份验证者信息注册成功，公共服务器为其分配的ID为 [{}]",responsePacket.getId());
            IDVerifierPersonalParams idVerifierPersonalParams = this.applicationContext.getBean(IDVerifierPersonalParams.class);
            idVerifierPersonalParams.setId(responsePacket.getId());
            idVerifierPersonalParams.store(idVerifierPersonalInformation.getParamsSavePath(),false);
            GlobalRegisterParamsUtils.storeAuditAgentAddress(responsePacket.getAuditAgentRegisterParams());
            log.info("身份验证者参数保存在本地，请在文件 [{}] 下查看", idVerifierPersonalInformation.getParamsSavePath());
            log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }else {
            log.error(responsePacket.getReason());
            log.error("程序异常退出");
            System.exit(1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
