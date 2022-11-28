package com.multi.domain.iot.common.pool;

import com.multi.domain.iot.common.protocol.Packet;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:17
 * @Description: 连接池接口
 */
public interface ConnectionPool {
    /**
     * 根据地址获取连接，如果没有则创建连接
     */
    Channel getChannel(InetSocketAddress inetSocketAddress) throws InterruptedException;

    /**
     * 向指定地址发送消息
     */
    void sendMessage(InetSocketAddress inetSocketAddress,Packet packet) throws InterruptedException;


    /**
     *向指定的地址广播消息
     */
    void broadcastMessage(Collection<InetSocketAddress> ids, Packet packet) throws InterruptedException;
}
