package com.multi.domain.iot.auditagent.param;

import com.multi.domain.iot.common.param.CurveElementParams;
import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.param.StorableProperties;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 21:46
 * @description 审计代理的个人参数，后面通过BeanFactory放入工厂
 */
@Data
public class AuditAgentPersonalParams implements StorableProperties {
    private CurveElementParams curveElementParams;
    private Element publicKey;
    private Element msk;
    private int secureParameter;

    public static AuditAgentPersonalParams getInstance(CurveMetaProperties curveMetaProperties) {
        AuditAgentPersonalParams auditAgentPersonalParams = new AuditAgentPersonalParams();
        CurveElementParams curveElementParams = CurveElementParams.getInstance(curveMetaProperties);
        auditAgentPersonalParams.setCurveElementParams(curveElementParams);
        auditAgentPersonalParams.setSecureParameter(curveElementParams.getSecureParameter());
        Element msk = curveElementParams.getZ().newRandomElement().getImmutable();
        Element publicKey = curveElementParams.getGeneratorG().powZn(msk).getImmutable();
        auditAgentPersonalParams.setMsk(msk);
        auditAgentPersonalParams.setPublicKey(publicKey);
        return auditAgentPersonalParams;
    }

    @Override
    public void store(String filePath, boolean append) {
        this.curveElementParams.getCurveMetaProperties().store(filePath,false);
        try(OutputStream outputStream = new FileOutputStream(filePath,true)){
            Properties properties = new Properties();
            properties.setProperty("msk",this.msk.toString());
            properties.setProperty("publicKey",this.publicKey.toString());
            properties.store(outputStream,"store auditAgent params");
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
