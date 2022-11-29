package com.multi.domain.iot.verifier.param;

import com.multi.domain.iot.common.param.CurveElementParams;
import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.param.StorableProperties;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 23:26
 * @description IDV的个人参数
 */
@Data
public class IDVerifierPersonalParams implements StorableProperties {
    private CurveElementParams curveElementParams;
    private Element publicKey;
    private Element privateKey;
    private RegisterParams auditRegisterParams;
    private Integer id;
    private int secureParameter;

    public static IDVerifierPersonalParams getInstance(CurveMetaProperties curveMetaProperties) {
        IDVerifierPersonalParams idVerifierPersonalParams = new IDVerifierPersonalParams();
        CurveElementParams curveElementParams = CurveElementParams.getInstance(curveMetaProperties);
        idVerifierPersonalParams.setCurveElementParams(curveElementParams);
        idVerifierPersonalParams.setSecureParameter(curveElementParams.getSecureParameter());
        Element privateKey = curveElementParams.getZ().newRandomElement().getImmutable();
        Element publicKey = curveElementParams.getGeneratorG().powZn(privateKey).getImmutable();
        idVerifierPersonalParams.setPrivateKey(privateKey);
        idVerifierPersonalParams.setPublicKey(publicKey);
        return idVerifierPersonalParams;
    }

    @Override
    public void store(String filePath, boolean append) {
        this.curveElementParams.getCurveMetaProperties().store(filePath,false);
        try(OutputStream outputStream = new FileOutputStream(filePath,true)){
            Properties properties = new Properties();
            properties.setProperty("privateKey",this.privateKey.toString());
            properties.setProperty("publicKey",this.publicKey.toString());
            properties.setProperty("publicKeyBase64", Base64.encodeBase64String(this.publicKey.toBytes()));
            properties.setProperty("id",this.id.toString());
            properties.store(outputStream,"store idVerifier params");
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
