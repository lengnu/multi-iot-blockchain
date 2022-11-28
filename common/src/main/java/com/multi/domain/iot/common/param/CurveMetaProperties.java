package com.multi.domain.iot.common.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.param
 * @Author: duwei
 * @Date: 2022/11/24 14:57
 * @Description: 椭圆曲线元参数信息
 */
@Data
@NoArgsConstructor
public class CurveMetaProperties implements MetaProperties, StorableProperties,Serializable {
    private static final long serialVersionUID = 1545394302608235284L;
    private String type;
    private String q;
    private String h;
    private String r;
    private String exp2;
    private String exp1;
    private String sign1;
    private String sign0;
    private Map<String, String> propertiesMap;
    private String generatorG;
    private String generatorH;
    private int secureParameter;

    private interface Constant {
        String TYPE = "type";
        String Q = "q";
        String H = "h";
        String R = "r";
        String EXP1 = "exp1";
        String EXP2 = "exp2";
        String SIGN1 = "sign1";
        String SIGN0 = "sign0";
        String GENERATOR_G = "generatorG";
        String GENERATOR_H = "generatorH";
        String SECURE_PARAMETER = "secureParameter";
    }

    public CurveMetaProperties(String propertiesFilePath) {
        propertiesMap = new HashMap<>();
        try (InputStream inputStream = new FileInputStream(propertiesFilePath)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            this.type = properties.getProperty(Constant.TYPE);
            propertiesMap.put(Constant.TYPE, this.type);

            this.q = properties.getProperty(Constant.Q);
            propertiesMap.put(Constant.Q, this.q);

            this.h = properties.getProperty(Constant.H);
            propertiesMap.put(Constant.H, this.h);

            this.r = properties.getProperty(Constant.R);
            propertiesMap.put(Constant.R, this.r);

            this.sign0 = properties.getProperty(Constant.SIGN0);
            propertiesMap.put(Constant.SIGN0, this.sign0);

            this.sign1 = properties.getProperty(Constant.SIGN1);
            propertiesMap.put(Constant.SIGN1, this.sign1);

            this.exp1 = properties.getProperty(Constant.EXP1);
            propertiesMap.put(Constant.EXP1, this.exp1);

            this.exp2 = properties.getProperty(Constant.EXP2);
            propertiesMap.put(Constant.EXP2, this.exp2);

            this.generatorG = properties.getProperty(Constant.GENERATOR_G);
            propertiesMap.put(Constant.GENERATOR_G, this.generatorG);

            this.generatorH = properties.getProperty(Constant.GENERATOR_H);
            propertiesMap.put(Constant.GENERATOR_H, this.generatorH);

            this.secureParameter = Integer.parseInt(properties.getProperty(Constant.SECURE_PARAMETER));
            propertiesMap.put(Constant.SECURE_PARAMETER, String.valueOf(this.secureParameter));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getMetaProperties() {
        return this.propertiesMap;
    }

    @Override
    public void store(String filePath, boolean append) {
        try (OutputStream outputStream = new FileOutputStream(filePath, append)) {
            Properties properties = new Properties();
            properties.putAll(this.propertiesMap);
            properties.store(outputStream, "store meta properties");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
