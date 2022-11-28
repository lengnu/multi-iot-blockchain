package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.message.AuthenticationMessage;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.request
 * @Author: duwei
 * @Date: 2022/11/21 13:00
 * @Description: UD发送认证消息到ID-Verifier请求数据报
 */
@Data
public class AuthenticationMessageRequestPacket extends Packet {
    private AuthenticationMessage authenticationMessage;

    @Override
    public byte getMessageType() {
        return MessageType.AUTHENTICATION_MESSAGE_REQUEST;
    }
}
