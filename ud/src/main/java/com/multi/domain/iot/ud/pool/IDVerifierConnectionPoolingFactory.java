package com.multi.domain.iot.ud.pool;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.pool.ConnectionPoolFactory;
import com.multi.domain.iot.ud.handler.response.AuthenticationMessageResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;



/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:19
 * @Description: ID验证者的连接池
 */
@Component
public class IDVerifierConnectionPoolingFactory extends ConnectionPoolFactory {
    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private AuthenticationMessageResponseHandler authenticationMessageResponseHandler;

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
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(authenticationMessageResponseHandler);
                    }
                });
        return bootstrap.connect(new InetSocketAddress(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort())).sync().channel();
    }
}
