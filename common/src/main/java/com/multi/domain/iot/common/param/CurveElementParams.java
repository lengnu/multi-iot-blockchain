package com.multi.domain.iot.common.param;

import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.param
 * @Author: duwei
 * @Date: 2022/11/24 15:05
 * @Description: 椭圆曲线参数
 */
@Data
public class CurveElementParams {
    private Pairing pairing;
    private Field G;
    private Field Z;
    private Element generatorG;
    private Element generatorH;
    private CurveMetaProperties curveMetaProperties;
    private int secureParameter;

    private CurveElementParams() {
    }

    public static CurveElementParams getInstance(CurveMetaProperties curveMetaProperties) {
        CurveElementParams curveElementParams = new CurveElementParams();
        curveElementParams.setCurveMetaProperties(curveMetaProperties);
        PropertiesParametersAdapter propertiesParametersAdapter = new PropertiesParametersAdapter(curveMetaProperties);
        Pairing pairing = PairingFactory.getPairing(propertiesParametersAdapter);
        curveElementParams.setPairing(pairing);
        curveElementParams.setG(pairing.getG1());
        curveElementParams.setZ(pairing.getZr());
        curveElementParams.setSecureParameter(curveMetaProperties.getSecureParameter());
        byte[] hashG = ComputeUtils.sha512(curveMetaProperties.getGeneratorG().getBytes());
        byte[] hashH = ComputeUtils.sha512(curveMetaProperties.getGeneratorH().getBytes());
        curveElementParams.setGeneratorG(
                pairing.getG1().newElementFromHash(hashG,0,hashG.length).getImmutable()
                );
        curveElementParams.setGeneratorH(
                pairing.getG1().newElementFromHash(hashH,0,hashH.length).getImmutable()
        );
        return curveElementParams;
    }
}
