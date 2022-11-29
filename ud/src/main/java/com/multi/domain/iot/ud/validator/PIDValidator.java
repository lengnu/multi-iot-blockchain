package com.multi.domain.iot.ud.validator;

import com.multi.domain.iot.common.message.PidAndSignMessage;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.ud.param.UDPersonalParams;
import com.multi.domain.iot.ud.session.GlobalRegisterParamsUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 22:12
 * @description PID验证器
 */
@Slf4j
public class PIDValidator implements Validator {
    private PidAndSignMessage pidAndSignMessage;
    private UDPersonalParams udPersonalParams;

    public PIDValidator(PidAndSignMessage pidAndSignMessage, UDPersonalParams udPersonalParams) {
        this.pidAndSignMessage = pidAndSignMessage;
        this.udPersonalParams = udPersonalParams;
    }

    @Override
    public boolean verify() {
        Field G = udPersonalParams.getCurveElementParams().getG();
        Field Zq = udPersonalParams.getCurveElementParams().getZ();
        Element g = udPersonalParams.getCurveElementParams().getGeneratorG().getImmutable();
        byte[] publicKey = GlobalRegisterParamsUtils.getAuditAgentRegisterParams().getPublicKey();
        Element pkM = G.newElementFromBytes(publicKey).getImmutable();
        return verify(Zq, G, pkM, g);
    }

    private boolean verify(Field Zq, Field G, Element pkM, Element g) {
        Element PID = G.newElementFromBytes(this.pidAndSignMessage.getPid()).getImmutable();
        Element hash = Zq.newElementFromBytes(this.pidAndSignMessage.getHash()).getImmutable();
        Element sign = Zq.newElementFromBytes(this.pidAndSignMessage.getSign()).getImmutable();
        Element hashNew = ComputeUtils.H3(PID, Zq).getImmutable();
        return verifyHash(hash, hashNew) &&
                verifySign(g, hashNew, pkM, PID, sign);
    }

    private boolean verifyHash(Element hash, Element hashNew) {
        log.info("开始校验Hash");
        boolean success = hashNew.equals(hash);
        if (success) {
            log.info("Hash校验通过");
        } else {
            log.error("Hash校验失败");
        }
        return success;
    }

    private boolean verifySign(Element g,
                               Element hashNew,
                               Element pkM,
                               Element PID,
                               Element sign) {
        log.info("开始校验签名");
        Element front = pkM.powZn(hashNew).getImmutable();
        Element right = front.mul(PID).getImmutable();
        Element left = g.powZn(sign).getImmutable();
        boolean success = right.equals(left);
        if (success) {
            log.info("签名校验通过");
        } else {
            log.error("签名校验失败");
        }
        return success;
    }
}
