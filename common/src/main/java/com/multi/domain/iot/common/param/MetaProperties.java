package com.multi.domain.iot.common.param;

import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.param
 * @Author: duwei
 * @Date: 2022/11/24 14:55
 * @Description: 元参数，可用于生双线性对
 */
public interface MetaProperties {
    /**
     * 获取元参数集合
     */
    Map<String,String> getMetaProperties();
}
