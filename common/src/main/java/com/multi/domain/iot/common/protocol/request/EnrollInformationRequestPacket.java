package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import com.multi.domain.iot.common.role.Role;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.request
 * @Author: duwei
 * @Date: 2022/11/18 15:37
 * @Description: 注册信息到服务器，为了别的节点可以找到他
 */
@Data
public class EnrollInformationRequestPacket extends Packet {
    private RegisterParams registerParams;
    @Override
    public byte getMessageType() {
        return MessageType.ENROLL_INFORMATION_REQUEST;
    }
}
