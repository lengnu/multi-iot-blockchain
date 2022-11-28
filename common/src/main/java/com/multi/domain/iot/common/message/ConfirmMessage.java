package com.multi.domain.iot.common.message;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;


/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-25 19:58
 * @description IDVerifier向AuditAgen发送的确认消息
 */
@Data
@NoArgsConstructor
public class ConfirmMessage {
    private byte[] alpha;
    private Domain domain;
    private int totalIDVerifiersNumber;
    private int verifierId;
    private InetSocketAddress udAddress;
    private byte[] identityProtectionInformation;

    public ConfirmMessage(byte[] Y_i, byte[] sk_i, Field Z,
                          int totalIDVerifiersNumber, int verifierId,
                          Domain domain,InetSocketAddress udAddress,
                          byte[] identityProtectionInformation) {
        this.domain = domain;
        this.verifierId = verifierId;
        this.totalIDVerifiersNumber = totalIDVerifiersNumber;
        this.alpha = ComputeUtils.hashMessageToZq(
                        ComputeUtils.concatByteArray(Y_i, sk_i), Z).
                toBytes();
        this.udAddress = udAddress;
        this.identityProtectionInformation = identityProtectionInformation;
    }
}
