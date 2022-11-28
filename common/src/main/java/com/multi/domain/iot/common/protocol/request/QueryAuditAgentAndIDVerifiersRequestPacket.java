package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:37
 * @description 查询AA和所有IDV的信息请求
 */
@Data
@AllArgsConstructor
public class QueryAuditAgentAndIDVerifiersRequestPacket extends Packet {
    //查询制定域下的验证者
    private Domain domain;
    @Override
    public byte getMessageType() {
        return MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_REQUEST;
    }
}
