package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:00
 * @description AA向UD返回PID响应报文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratePidResponsePacket extends Packet {
   private boolean success;
   private String reason;
   private byte[] pid;
    @Override
    public byte getMessageType() {
        return MessageType.GENERATE_PID_RESPONSE;
    }
}
