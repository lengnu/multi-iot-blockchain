package com.multi.domain.iot.common.pool;

import com.multi.domain.iot.common.protocol.Packet;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:21
 * @Description: 连接池工厂
 */
public abstract class ConnectionPoolFactory implements ConnectionPool {
    protected Map<InetSocketAddress, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
     public void broadcastMessage(Collection<InetSocketAddress> ids, Packet packet) throws InterruptedException {
        for (InetSocketAddress id : ids) {
            getChannel(id).writeAndFlush(packet);
        }
    }

    @Override
    public Channel getChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        if (channelMap.get(inetSocketAddress) == null) {
            synchronized (this){
                if (channelMap.get(inetSocketAddress) == null){
                    createChannel(inetSocketAddress);
                }
            }
        }
        return channelMap.get(inetSocketAddress);
    }

    @Override
    public void sendMessage(InetSocketAddress inetSocketAddress,Packet packet) throws InterruptedException {
        getChannel(inetSocketAddress).writeAndFlush(packet);
    }

    private void createChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        Channel channel = doCreateChannel(inetSocketAddress);
        channelMap.put(inetSocketAddress, channel);
    }

    protected abstract Channel doCreateChannel(InetSocketAddress inetSocketAddress) throws InterruptedException;
}
