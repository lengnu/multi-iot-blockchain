package com.multi.domain.iot.ud.param;

import com.multi.domain.iot.common.message.AuthenticationMessage;
import com.multi.domain.iot.common.param.*;
import com.multi.domain.iot.common.polynomial.VerifiablePolynomial;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.util.IPUtils;
import com.multi.domain.iot.ud.session.GlobalRegisterParamsUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.param
 * @Author: duwei
 * @Date: 2022/11/24 16:08
 * @Description: UD的个人参数
 */
@Data
@Slf4j
public class UDPersonalParams implements StorableProperties {
    private CurveElementParams curveElementParams;
    private byte[] PID;
    private Element s;
    private Element S;
    private byte[] identityInformation;
    private byte[] identityProtectionInformation;
    private int secureParameter;
    private VerifiablePolynomial verifiablePolynomial;
    private UDPersonalInformation udPersonalInformation;


    public AuthenticationMessage computeAuthenticationParams() {
        int totalVerifierNumber = GlobalRegisterParamsUtils.getTotalVerifierNumber();
        log.info("1.计算真实身份信息");
        computeIdentityInformation();

        log.info("2.计算真实身份保护信息");
        computeIdentityProtectInformation(identityInformation);

        log.info("3.选取随机多项式，为不同身份验证者计算认证信息");
        log.info("  3.1 随机生成多项式");
        this.verifiablePolynomial =
                new VerifiablePolynomial(
                        totalVerifierNumber /2 ,
                        this.curveElementParams.getZ(),
                        this.s,
                        this.curveElementParams.getG(),
                        this.curveElementParams.getGeneratorH());
        log.info("  3.2 为每个身份验证者计算份额");
        Map<Integer, byte[]> sharesMap = this.computeShares(this.verifiablePolynomial, GlobalRegisterParamsUtils.getIDVerifiersId());
        log.info("  3.3 计算多项式系数承诺");
        Map<Integer, byte[]> polynomialCoefficientsCommitmentMap = this.verifiablePolynomial.computePolynomialCoefficientsCommitment();
        log.info("  3,4 计算份额承诺");
        Map<Integer, byte[]> sharesCommitmentMap = this.verifiablePolynomial.computeSharesCommitment(sharesMap);
        log.info("  3.4 使用身份验证者公钥对份额加密");
        Map<Integer, byte[]> publicKeyProtectionSharesMap = this.verifiablePolynomial.computePublicKeyProtectionShares(GlobalRegisterParamsUtils.getIDVerifiersPublicKey(), sharesMap);
        log.info("  3.5 计算验证信息");
        Map<Integer, byte[]> verifiersInformation = this.computeVerifiersInformation(sharesCommitmentMap, GlobalRegisterParamsUtils.getIDVerifiersPublicKey(), this.curveElementParams.getPairing(), this.curveElementParams.getG());
        return new AuthenticationMessage(
                this.identityProtectionInformation,
                polynomialCoefficientsCommitmentMap,
                sharesCommitmentMap,
                publicKeyProtectionSharesMap,
                verifiersInformation,
                totalVerifierNumber,
                new InetSocketAddress(IPUtils.getLocalIp(), this.udPersonalInformation.getListenPort()),
                this.udPersonalInformation.getDomain()
        );
    }

    private Map<Integer, byte[]> computeShares(VerifiablePolynomial verifiablePolynomial, Set<Integer> idVerifiers) {
        return verifiablePolynomial.computeSharesOverInputSet(idVerifiers);
    }

    /**
     * 计算真实身份信息
     */
    private void computeIdentityInformation() {
        this.identityInformation = this.udPersonalInformation.computeIdentityInformation(this.secureParameter / 8);
    }

    private void computeIdentityProtectInformation(byte[] identityInformation) {
        this.s = this.curveElementParams.getZ().newRandomElement().getImmutable();
        this.S = this.curveElementParams.getGeneratorG().powZn(this.s).getImmutable();
        this.identityProtectionInformation =  computeIdentityProtectInformation(identityInformation, this.S, this.secureParameter);
    }

    /**
     * 计算真实身份保护信息
     */
    public byte[] computeIdentityProtectInformation(byte[] identityInformation, Element S, int secureParameter) {
        byte[] hash = ComputeUtils.hashToFixByteLength(S.toBytes(), 4 * secureParameter / 8);
        return ComputeUtils.xor(hash, identityInformation);
    }

    public static UDPersonalParams getInstance(CurveMetaProperties curveMetaProperties) {
        UDPersonalParams udPersonalParams = new UDPersonalParams();
        CurveElementParams curveElementParams = CurveElementParams.getInstance(curveMetaProperties);
        udPersonalParams.setCurveElementParams(curveElementParams);
        udPersonalParams.setSecureParameter(curveElementParams.getSecureParameter());
        return udPersonalParams;
    }

    /**
     * 计算验证信息
     */
    private Map<Integer, byte[]> computeVerifiersInformation(Map<Integer, byte[]> sharesCommitmentMap,
                                                            Map<Integer, byte[]> publicKeysMap,
                                                            Pairing pairing,
                                                            Field G) {
        Map<Integer, byte[]> result = new HashMap<>();
        sharesCommitmentMap.forEach((id, shareCommitment) -> {
            byte[] publicKey = publicKeysMap.get(id);
            result.put(id, computeVerifyInformation(shareCommitment, publicKey, pairing, G).toBytes());
        });
        return result;
    }

    private Element computeVerifyInformation(byte[] shareCommitment,
                                             byte[] publicKeyProtectionShare,
                                             Pairing pairing,
                                             Field G) {
        Element shareCommitmentElement = G.newElementFromBytes(shareCommitment).getImmutable();
        Element publicKeyElement= G.newElementFromBytes(publicKeyProtectionShare).getImmutable();
        return pairing.pairing(shareCommitmentElement, publicKeyElement).getImmutable();
    }


    @Override
    public void store(String filePath, boolean append) {
        this.curveElementParams.getCurveMetaProperties().store(filePath, false);
        try (OutputStream outputStream = new FileOutputStream(filePath, true)) {
            Properties properties = new Properties();
            properties.setProperty("s", this.s.toString());
            properties.setProperty("S", this.S.toString());
            properties.setProperty("PID",Base64.encodeBase64String(this.PID));
            properties.store(outputStream, "store ud params");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}

