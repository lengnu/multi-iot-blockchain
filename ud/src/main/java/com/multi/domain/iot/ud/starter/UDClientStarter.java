package com.multi.domain.iot.ud.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.protocol.request.FetchPublicParameterRequestPacket;
import com.multi.domain.iot.ud.handler.response.FetchPublicParameterResponseHandler;
import com.multi.domain.iot.ud.handler.response.QueryAuditAgentAndIDVerifiersResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.start
 * @Author: duwei
 * @Date: 2022/11/17 17:03
 * @Description: 通信设备UD客户端启动器
 */
@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "public.server")
public class UDClientStarter {
    private String host;
    private int port;

    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private FetchPublicParameterResponseHandler fetchPublicParameterResponseHandler;
    @Autowired
    private QueryAuditAgentAndIDVerifiersResponseHandler queryAuditAgentAndIDVerifiersResponseHandler;


   @PostConstruct
    private void init() throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast( new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(fetchPublicParameterResponseHandler);
                        nioSocketChannel.pipeline().addLast(queryAuditAgentAndIDVerifiersResponseHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port)).sync();
        if (channelFuture.isSuccess()) {
            log.info("通信设备UD连接公共服务器成功，开始获取公共参数");
            Channel channel = channelFuture.channel();
            FetchPublicParameterRequestPacket requestPacket = new FetchPublicParameterRequestPacket();
            channel.writeAndFlush(requestPacket);
        }
    }
}
