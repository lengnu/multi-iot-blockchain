package com.multi.domain.iot.common.protocol;

import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol
 * @Author: duwei
 * @Date: 2022/11/17 11:26
 * @Description: 消息数据包
 */
@Data
public abstract class Packet {
    /**
     * 当前数据包版本
     */
    byte version = 1;

    public abstract byte getMessageType();

}
