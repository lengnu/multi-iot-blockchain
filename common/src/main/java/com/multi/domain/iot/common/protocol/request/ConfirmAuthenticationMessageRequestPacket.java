package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.message.ConfirmMessage;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.protocol.request
 * @Author: duwei
 * @Date: 2022/11/22 14:59
 * @Description: 确认消息请求数据包
 */
@Data
@NoArgsConstructor
public class ConfirmAuthenticationMessageRequestPacket extends Packet {
    private ConfirmMessage confirmMessage;

    public ConfirmAuthenticationMessageRequestPacket(ConfirmMessage confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    @Override
    public byte getMessageType() {
        return MessageType.CONFIRM_AUTHENTICATION_MESSAGE_REQUEST;
    }
}
