package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:42
 * @description 查询AA和所有IDV的信息响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryAuditAgentAndIDVerifiersResponsePacket extends Packet {
    private RegisterParams auditRegisterParams;
    private Map<Integer,RegisterParams> idVerifierRegisterParams;
    private boolean success;
    private String reason;

    @Override
    public byte getMessageType() {
        return MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_RESPONSE;
    }
}
