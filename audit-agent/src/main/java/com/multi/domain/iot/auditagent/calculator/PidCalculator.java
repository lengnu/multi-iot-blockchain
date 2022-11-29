package com.multi.domain.iot.auditagent.calculator;

import com.multi.domain.iot.auditagent.param.AuditAgentPersonalParams;
import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.message.PidAndSignMessage;
import com.multi.domain.iot.common.protocol.request.GeneratePidRequestPacket;
import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;


import java.util.Collection;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 20:33
 * @description UD的身份PID计算器
 */
@Slf4j
public class PidCalculator {
    /**
     * 计算返回给UD的PID及验证信息
     */
    public static GeneratePidRequestPacket calculatePIDPacket(
            String uid,
            byte[] identityProtectionInformation,
            AuditAgentPersonalParams auditAgentPersonalParams,
            boolean success) {
        log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        GeneratePidRequestPacket requestPacket = new GeneratePidRequestPacket();
        requestPacket.setSuccess(success);
        if (!success) {
            log.error("PID生成错误，身份验证者可能掉线或验证未通过");
            requestPacket.setReason("身份验证者可能掉线或验证未通过");
        } else {
            log.info("各个身份验证者的确认信息都已收集完毕，开始为UD用户生成PID");
            Field G = auditAgentPersonalParams.getCurveElementParams().getG();
            Element g = auditAgentPersonalParams.getCurveElementParams().getGeneratorG().getImmutable();
            Field Zq = auditAgentPersonalParams.getCurveElementParams().getZ();
            Element msk = auditAgentPersonalParams.getMsk().getImmutable();

            Collection<byte[]> confirmsInformation = ConfirmAuthenticationMessageSessionUtils.getConfirmsInformation(uid);
            Element alpha = ComputeUtils.calculateAccumulation(confirmsInformation, Zq).getImmutable();
            Element beta = Zq.newRandomElement().getImmutable();
            Element exponential = alpha.mul(
                    beta.add(ComputeUtils.hashMessageToZq(identityProtectionInformation, Zq)).getImmutable()
            ).getImmutable();
            //1.计算PID
            Element PID = g.powZn(exponential).getImmutable();
            //2.计算hash
            Element hash = ComputeUtils.H3(PID, Zq).getImmutable();
            //3.计算sign
            Element back = hash.mul(msk).getImmutable();
            Element sign = exponential.add(back).getImmutable();
            PidAndSignMessage pidAndSignMessage = new PidAndSignMessage();
            pidAndSignMessage.setPid(PID.toBytes());
            pidAndSignMessage.setHash(hash.toBytes());
            pidAndSignMessage.setSign(sign.toBytes());
            requestPacket.setPidAndSignMessage(pidAndSignMessage);
            log.info("PID生成完毕，开始发送给对应的设备UD");
        }


        return requestPacket;
    }
}
