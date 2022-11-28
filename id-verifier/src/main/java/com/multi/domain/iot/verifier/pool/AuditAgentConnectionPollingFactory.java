package com.multi.domain.iot.verifier.pool;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.pool.ConnectionPoolFactory;
import com.multi.domain.iot.verifier.handler.response.ConfirmAuthenticationMessageResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 19:48
 * @description 连接审计代理的连接池工厂
 */
@Component
@Slf4j
public class AuditAgentConnectionPollingFactory extends ConnectionPoolFactory {
    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private ConfirmAuthenticationMessageResponseHandler confirmAuthenticationMessageResponseHandler;

    @Override
    protected Channel doCreateChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(confirmAuthenticationMessageResponseHandler);

                    }
                });
        return bootstrap.connect(new InetSocketAddress(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort())).sync().channel();
    }
}
