package com.multi.domain.iot.server.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.server.handler.EnrollInformationRequestHandler;
import com.multi.domain.iot.server.handler.FetchPublicParameterRequestHandler;
import com.multi.domain.iot.server.handler.QueryAuditAgentAndIDVerifiersRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.server
 * @Author: duwei
 * @Date: 2022/11/17 16:51
 * @Description: 公共服务器启动类
 */
@Slf4j
@Component
public class PublicServerStarter {
    @Value("${public.server.listen-port}")
    private int port;
    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private FetchPublicParameterRequestHandler fetchPublicParameterRequestHandler;
    @Autowired
    private EnrollInformationRequestHandler enrollInformationRequestHandle;
    @Autowired
    private QueryAuditAgentAndIDVerifiersRequestHandler queryAuditAgentAndIDVerifiersRequestHandler;

    @PostConstruct
    public void init() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(fetchPublicParameterRequestHandler);
                        nioSocketChannel.pipeline().addLast(enrollInformationRequestHandle);
                        nioSocketChannel.pipeline().addLast(queryAuditAgentAndIDVerifiersRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
        if (channelFuture.isSuccess()) {
            log.info("公共服务器启动成功，正在 [{}] 端口进行监听!", this.port);
        } else {
            log.error("公共服务器在 [{}] 端口启动失败，请检查端口占用后重试!", this.port);
        }
        log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
