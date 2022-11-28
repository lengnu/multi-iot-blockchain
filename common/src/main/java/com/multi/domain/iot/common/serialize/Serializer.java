package com.multi.domain.iot.common.serialize;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.serialize
 * @Author: duwei
 * @Date: 2022/11/17 16:31
 * @Description: 序列化器
 */
public interface Serializer {
    /**
     * 序列化算法
     * @return  序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * 序列化算法
     * @param object    序列化对象
     * @return  序列化后字节
     */
    byte[] serialize(Object object);

    /**
     * 反序列化算法
     * @param tClass    反序列化对象类型
     * @param data      反序列化数据
     * @param <T>       泛型-对象类型
     * @return  反序列化的对象
     */
    <T> T deserialize(Class<T> tClass,byte[] data);
}
