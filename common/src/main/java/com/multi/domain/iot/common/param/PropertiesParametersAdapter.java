package com.multi.domain.iot.common.param;

import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.param
 * @Author: duwei
 * @Date: 2022/11/24 15:07
 * @Description: 各种参数的适配器，继承自JPBC的PropertiesParameters，用来作为PairingFactory的传入参数
 */
public class PropertiesParametersAdapter extends PropertiesParameters {
    public PropertiesParametersAdapter(MetaProperties metaProperties) {
        this.parameters.putAll(metaProperties.getMetaProperties());
    }

    public PropertiesParametersAdapter() {
    }
}
