package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.param.CurveMetaProperties;
import com.multi.domain.iot.common.param.MetaProperties;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.response
 * @Author: duwei
 * @Date: 2022/11/17 11:32
 * @Description: 获取公共参数元信息响应数据包，包含椭圆曲线元信息
 */
@Data
public class FetchPublicParameterResponsePacket extends Packet {
    private CurveMetaProperties curveMetaProperties;
    private boolean success;

    @Override
    public byte getMessageType() {
        return MessageType.FETCH_PUBLIC_PARAMETER_RESPONSE;
    }
}
