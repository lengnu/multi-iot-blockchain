package com.multi.domain.iot.verifier.validator;

import com.multi.domain.iot.common.message.AuthenticationMessage;
import com.multi.domain.iot.common.param.CurveElementParams;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.verifier.session.GlobalRegisterParamsUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.validator
 * @Author: duwei
 * @Date: 2022/11/22 9:07
 * @Description: UD认证信息验证器
 */
@Slf4j
public class AuthenticationMessageValidator implements Validator {
    /**
     * UD传过来的认证信息
     */
    private final AuthenticationMessage authenticationMessage;
    /**
     * 用来进行验证的公共椭圆曲线参数
     */
    private final CurveElementParams curveElementParams;

    public AuthenticationMessageValidator(AuthenticationMessage authenticationMessage, CurveElementParams curveElementParams) {
        this.authenticationMessage = authenticationMessage;
        this.curveElementParams = curveElementParams;
    }

    @Override
    public boolean verify() {
        return this.verifyUnique(Base64.encodeBase64String(authenticationMessage.getIdentityProtectionInformation())) && this.verifySharesCorrectness() && this.verifySharesConsistency();
    }

    /**
     * 验证份额的正确性，即份额是按照构造的多项式生成
     */
    private boolean verifySharesCorrectness() {
        log.info("开始验证秘密共享份额的正确性");
        Map<Integer, byte[]> polynomialCoefficientsCommitment = authenticationMessage.getPolynomialCoefficientsCommitment();
        Map<Integer, byte[]> sharesCommitment = authenticationMessage.getSharesCommitment();
        for (Map.Entry<Integer, byte[]> shareCommitment : sharesCommitment.entrySet()) {
            if (!verifyShareCorrectness(polynomialCoefficientsCommitment, shareCommitment.getKey(), shareCommitment.getValue())) {
                log.error("正确性验证失败");
                return false;
            }
        }
        log.info("正确性验证通过");
        return true;
    }

    /**
     * 验证份额一致性
     */
    private boolean verifySharesConsistency() {
        log.info("开始批量验证秘密共享份额的一致性");
        boolean success = this.verifySharesConsistency(this.authenticationMessage.getPublicKeyProtectionShares(),
                this.authenticationMessage.getVerifyInformation());
        if (success) {
            log.info("一致性验证通过");
        } else {
            log.error("一致性验证失败");
        }
        return success;
    }

    private boolean verifySharesConsistency(Map<Integer, byte[]> publicKeySharesProtection,
                                            Map<Integer, byte[]> verifyInformation
    ) {
        Pairing pairing = this.curveElementParams.getPairing();
        Field G = pairing.getG1();
        Field GT = pairing.getGT();
        Element h = this.curveElementParams.getGeneratorH();
        return this.verifySharesConsistency(publicKeySharesProtection, verifyInformation,
                pairing, G, GT, h);
    }

    /**
     * 批量验证份额的一致性，即UD生成份额的密文确实是由经过承诺的份额生成
     */
    private boolean verifySharesConsistency(Map<Integer, byte[]> publicKeySharesProtection,
                                            Map<Integer, byte[]> verifyInformation,
                                            Pairing pairing,
                                            Field G,
                                            Field GT,
                                            Element h) {
        Element left = ComputeUtils.calculateMultiplication(verifyInformation.values(), GT);
        Element right = pairing.pairing(h, ComputeUtils.calculateAccumulation(publicKeySharesProtection.values(), G));
        return left.equals(right);
    }


    private boolean verifyShareCorrectness(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i,
                                           byte[] shareCommitment) {
        return this.verifyShareCorrectness(polynomialCoefficientsCommitment, i,this.curveElementParams.getZ(), shareCommitment, this.curveElementParams.getG());
    }

    private boolean verifyShareCorrectness(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i, Field Zq,
                                           byte[] shareCommitment, Field G) {
        Element one = G.newOneElement();
        Element iElement = Zq.newElement(i).getImmutable();
        //TODO 指数、可优化 -> 倍乘
        polynomialCoefficientsCommitment.forEach((coefficientIndex, coefficientCommitment) -> {
            Element coefficientIndexElement = Zq.newElement(coefficientIndex).getImmutable();
            Element coefficientCommitmentElement = G.newElementFromBytes(coefficientCommitment).getImmutable();
            Element exponential = iElement.powZn(coefficientIndexElement).getImmutable();
            Element cur = coefficientCommitmentElement.powZn(exponential).getImmutable();
            one.mul(cur);
        });
        Element shareCommitmentElement = G.newElementFromBytes(shareCommitment).getImmutable();
        return shareCommitmentElement.equals(one);
    }

    private boolean verifyUnique(String uid){
        log.info("开始验证该用户是否第一次请求生成PID");
        boolean result =  GlobalRegisterParamsUtils.isGeneratedPid(uid);
        if (result){
            log.error("该用户已经生成过PID，不能重复生成");
        }else {
            log.info("该用户第一次请求，可以为其生成PID");
        }
        return !result;
    }
}
