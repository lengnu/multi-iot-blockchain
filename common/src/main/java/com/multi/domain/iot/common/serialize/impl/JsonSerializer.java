package com.multi.domain.iot.common.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.multi.domain.iot.common.serialize.SerializeAlgorithm;
import com.multi.domain.iot.common.serialize.Serializer;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.serialize.impl
 * @Author: duwei
 * @Date: 2022/11/17 16:34
 * @Description: JSON序列化算法
 */
public class JsonSerializer implements Serializer {

    private JsonSerializer(){

    }

    public static final Serializer INSTANCE = new JsonSerializer();

    @Override
    public byte getSerializerAlgorithm() {
        return SerializeAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public <T> T deserialize(Class<T> tClass, byte[] data) {
        return JSON.parseObject(data,tClass);
    }
}
