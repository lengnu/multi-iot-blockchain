package com.multi.domain.iot.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: multi-iot-blockchain
 * @BelongsPackage: com.multi.domain.iot.common.message
 * @Author: duwei
 * @Date: 2022/11/29 10:15
 * @Description: 登记到区块链上的消息
 */
@Data
@AllArgsConstructor
public class BlockChainRegisterMessage {
    private String pid;
    private String identityProtectionInformation;
    private InetSocketAddress inetSocketAddress;
}
