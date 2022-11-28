package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.response
 * @Author: duwei
 * @Date: 2022/11/18 16:00
 * @Description: 注册信息响应包
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollInformationResponsePacket extends Packet {
    private boolean success;
    private int id;
    private String reason;
    private RegisterParams auditAgentRegisterParams;
    @Override
    public byte getMessageType() {
        return MessageType.ENROLL_INFORMATION_RESPONSE;
    }
}
