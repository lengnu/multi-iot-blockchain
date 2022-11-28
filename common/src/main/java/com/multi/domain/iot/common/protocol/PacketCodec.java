package com.multi.domain.iot.common.protocol;

import com.multi.domain.iot.common.protocol.request.*;
import com.multi.domain.iot.common.protocol.message.MessageType;
import com.multi.domain.iot.common.protocol.response.*;
import com.multi.domain.iot.common.serialize.Serializer;
import com.multi.domain.iot.common.serialize.impl.JsonSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol
 * @Author: duwei
 * @Date: 2022/11/17 16:28
 * @Description: 编码解码器
 */
public class PacketCodec {
    /**
     * 魔数
     */
    private static final int MAGIC_NUMBER = 0x12345678;
    public static final PacketCodec INSTANCE = new PacketCodec();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;

    /**
     * 序列化器
     */
    private Serializer serializer;

    private PacketCodec() {
        serializer = JsonSerializer.INSTANCE;
        ;
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(MessageType.FETCH_PUBLIC_PARAMETER_REQUEST, FetchPublicParameterRequestPacket.class);
        packetTypeMap.put(MessageType.FETCH_PUBLIC_PARAMETER_RESPONSE, FetchPublicParameterResponsePacket.class);
        packetTypeMap.put(MessageType.ENROLL_INFORMATION_REQUEST, EnrollInformationRequestPacket.class);
        packetTypeMap.put(MessageType.ENROLL_INFORMATION_RESPONSE, EnrollInformationResponsePacket.class);
        packetTypeMap.put(MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_REQUEST, QueryAuditAgentAndIDVerifiersRequestPacket.class);
        packetTypeMap.put(MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_RESPONSE, QueryAuditAgentAndIDVerifiersResponsePacket.class);
        packetTypeMap.put(MessageType.AUTHENTICATION_MESSAGE_REQUEST, AuthenticationMessageRequestPacket.class);
        packetTypeMap.put(MessageType.AUTHENTICATION_MESSAGE_RESPONSE, AuthenticationMessageResponsePacket.class);
        packetTypeMap.put(MessageType.CONFIRM_AUTHENTICATION_MESSAGE_REQUEST, ConfirmAuthenticationMessageRequestPacket.class);
        packetTypeMap.put(MessageType.CONFIRM_AUTHENTICATION_MESSAGE_RESPONSE, ConfirmAuthenticationMessageResponsePacket.class);
        packetTypeMap.put(MessageType.GENERATE_PID_REQUEST, GeneratePidRequestPacket.class);
        packetTypeMap.put(MessageType.GENERATE_PID_RESPONSE, GeneratePidResponsePacket.class);
    }

    /**
     * 编码器，将数据包编码为byteBuf
     *
     * @param byteBuf
     * @param packet
     * @return
     */
    public void encode(ByteBuf byteBuf, Packet packet) {
        //1.写入魔数
        byteBuf.writeInt(MAGIC_NUMBER);

        //2.写入版本号
        byteBuf.writeByte(packet.getVersion());

        //3.写入序列化算法
        byteBuf.writeByte(serializer.getSerializerAlgorithm());

        //4.写入消息类型
        byteBuf.writeByte(packet.getMessageType());

        //5.写入数据长度
        byte[] bytes = serializer.serialize(packet);
        byteBuf.writeInt(bytes.length);

        //6.写入实际数据
        byteBuf.writeBytes(bytes);
    }


    /**
     * 解码器，将ByteBuf变为对应的Packet
     *
     * @param byteBuf
     * @return
     */
    public Packet decode(ByteBuf byteBuf) {
        //1.跳过魔数
        byteBuf.skipBytes(4);

        //2.跳过版本
        byteBuf.skipBytes(1);

        //3.拿到序列化算法
        byte serializeAlgorithm = byteBuf.readByte();

        //4.拿到消息类型
        byte messageType = byteBuf.readByte();
        //5.消息长度
        int length = byteBuf.readInt();

        //6.读取实际消息
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(messageType);

        //有对应的消息类型和序列化器
        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    private Class<? extends Packet> getRequestType(byte messageType) {
        return packetTypeMap.get(messageType);
    }

}
