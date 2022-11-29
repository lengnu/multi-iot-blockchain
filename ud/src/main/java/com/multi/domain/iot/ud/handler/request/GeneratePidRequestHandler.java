package com.multi.domain.iot.ud.handler.request;


import com.multi.domain.iot.common.protocol.request.GeneratePidRequestPacket;
import com.multi.domain.iot.common.protocol.response.GeneratePidResponsePacket;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.ud.param.UDPersonalInformation;
import com.multi.domain.iot.ud.param.UDPersonalParams;
import com.multi.domain.iot.ud.validator.PIDValidator;
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
 * @create 2022-11-22 21:35
 * @description 处理AA返回的PID，需要对其进行校验
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class GeneratePidRequestHandler extends SimpleChannelInboundHandler<GeneratePidRequestPacket> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private UDPersonalInformation personalInformation;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GeneratePidRequestPacket requestPacket) throws Exception {
        log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (requestPacket.isSuccess()) {
            log.info("收到审计代理生成的PID，开始进行本地校验");
            UDPersonalParams udPersonalParams = this.applicationContext.getBean(UDPersonalParams.class);
            Validator validator = new PIDValidator(requestPacket.getPidAndSignMessage(), udPersonalParams);
            boolean success = validator.verify();
            GeneratePidResponsePacket responsePacket = new GeneratePidResponsePacket();
            responsePacket.setPid(requestPacket.getPidAndSignMessage().getPid());
            responsePacket.setSuccess(success);
            if (success) {
                udPersonalParams.setPID(requestPacket.getPidAndSignMessage().getPid());
                udPersonalParams.store(personalInformation.getParamsSavePath(), false);
                log.info("设备UD校验PID成功，接受PID作为自己假名，请在文件 [{}] 下查看PID信息", personalInformation.getParamsSavePath());
                log.info("通知审计代理将PID信息上链");
            } else {
                log.error("设备UD校验PID失败");
                responsePacket.setReason("设备UD校验PID失败");
            }
            ctx.writeAndFlush(responsePacket);
        } else {
            log.error("审计代理生成PID失败，原因 : {}", requestPacket.getReason());
            log.error("程序异常退出");
            System.exit(1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
