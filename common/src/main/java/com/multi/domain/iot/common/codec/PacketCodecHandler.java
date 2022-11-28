package com.multi.domain.iot.common.codec;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.codec
 * @Author: duwei
 * @Date: 2022/11/17 16:38
 * @Description: 编解码消息
 */
@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    private PacketCodecHandler() {

    }

    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    private PacketCodec packetCodec = PacketCodec.INSTANCE;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.channel().alloc().ioBuffer();
        packetCodec.encode(byteBuf, packet);
        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Packet decode = packetCodec.decode(byteBuf);
        list.add(decode);
    }
}
