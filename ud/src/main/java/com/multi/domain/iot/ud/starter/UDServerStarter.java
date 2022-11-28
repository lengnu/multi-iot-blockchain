package com.multi.domain.iot.ud.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.ud.handler.request.GeneratePidRequestHandler;
import com.multi.domain.iot.ud.param.UDPersonalInformation;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.starter
 * @Author: duwei
 * @Date: 2022/11/21 17:25
 * @Description: UD作为服务器启动类
 */
@Slf4j
@Component
public class UDServerStarter {

    @Autowired
    private UDPersonalInformation udPersonalInformation;
    @Autowired
    private PacketCodecHandler packetCodecHandler;
    @Autowired
    private GeneratePidRequestHandler generatePidRequestHandler;

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
                        nioSocketChannel.pipeline().addLast(generatePidRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(udPersonalInformation.getListenPort()).sync();
        if (channelFuture.isSuccess()) {
            log.info("[{}] 域内参与设备在 [{}] 端口进行监听",
                    this.udPersonalInformation.getDomain().getDomainIdentity()
                    , this.udPersonalInformation.getListenPort());
        } else {
            log.error("[{}] 域内参与设备在 [{}] 端口启动失败，请检查端口占用后重试!",
                    this.udPersonalInformation.getDomain().getDomainIdentity(),
                    this.udPersonalInformation.getListenPort());
        }
    }
}
