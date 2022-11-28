package com.multi.domain.iot.verifier.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.verifier.handler.request.AuthenticationMessageRequestHandler;
import com.multi.domain.iot.verifier.param.IDVerifierPersonalInformation;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.starter
 * @Author: duwei
 * @Date: 2022/11/21 14:10
 * @Description: 验证者作为服务器启动类
 */
@Slf4j
@Component
@Data
@Order(1)
public class IDVerifierServerStarter {
    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private IDVerifierPersonalInformation idVerifierPersonalInformation;
    @Autowired
    private AuthenticationMessageRequestHandler authenticationMessageRequestHandler;

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
                        nioSocketChannel.pipeline().addLast(authenticationMessageRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(this.idVerifierPersonalInformation.getListenPort()).sync();
        if (channelFuture.isSuccess()) {
            log.info("[{}] 域内身份验证者在 [{}] 端口进行监听",
                    this.idVerifierPersonalInformation.getDomain().getDomainIdentity()
                    , this.idVerifierPersonalInformation.getListenPort());
        } else {
            log.error("[{}] 域内身份验证者在 [{}] 端口启动失败，请检查端口占用后重试!",
                    this.idVerifierPersonalInformation.getDomain().getDomainIdentity(),
                    this.idVerifierPersonalInformation.getListenPort());
        }
    }
}
