package com.multi.domain.iot.common.domain;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.domain
 * @Author: duwei
 * @Date: 2022/11/21 15:29
 * @Description: 节点所处的域
 */
public enum Domain {
    /**
     * 医院域
     */
    HOSPITAL("hospital"),
    /**
     * 温度采集域
     */
    TEMPERATURE_CAPTURE("temperature_capture"),
    /**
     * 湿度采集域
     */
    HUMIDITY_CAPTURE("humidity_capture");

    private String domainIdentity;

    Domain(String domainIdentity) {
        this.domainIdentity = domainIdentity;
    }

    public String getDomainIdentity() {
        return domainIdentity;
    }



}
