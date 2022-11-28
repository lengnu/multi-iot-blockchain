package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 19:53
 * @description 确认消息请求数据包
 */
@Data
public class ConfirmAuthenticationMessageResponsePacket extends Packet {
    @Override
    public byte getMessageType() {
        return MessageType.CONFIRM_AUTHENTICATION_MESSAGE_RESPONSE;
    }
}
