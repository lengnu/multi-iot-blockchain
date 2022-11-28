package com.multi.domain.iot.common.param;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.param
 * @Author: duwei
 * @Date: 2022/11/24 11:36
 * @Description: TODO
 */
public class test {
    public static void main(String[] args) {
        CurveMetaProperties curveMetaProperties = new CurveMetaProperties("a.properties");
        CurveElementParams instance = CurveElementParams.getInstance(curveMetaProperties);
        curveMetaProperties.store("test.properties",true);
    }
}
