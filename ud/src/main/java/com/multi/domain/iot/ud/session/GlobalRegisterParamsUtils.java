package com.multi.domain.iot.ud.session;

import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import org.apache.commons.codec.binary.Base64;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-25 13:32
 * @description 维护全局会话
 */
public class GlobalRegisterParamsUtils {

    private static RegisterParams auditAgentRegisterParams;
    private static Map<Integer, RegisterParams> domainIDVerifiersRegisterParams;

    private static volatile Map<Integer,InetSocketAddress> idVerifiersAddressMap;
    private static final Object idVerifiersAddressMapLock = new Object();

    private static volatile Map<Integer,byte[]> idVerifiersPublicKeyMap;
    private static final Object idVerifiersPublicKeyMapLock = new Object();

    public static void storeRegisterParams(QueryAuditAgentAndIDVerifiersResponsePacket queryAuditAgentAndIDVerifiersResponsePacket) {
        auditAgentRegisterParams = queryAuditAgentAndIDVerifiersResponsePacket.getAuditRegisterParams();
        domainIDVerifiersRegisterParams = queryAuditAgentAndIDVerifiersResponsePacket.getIdVerifierRegisterParams();
    }

    public static RegisterParams getAuditAgentRegisterParams() {
        return auditAgentRegisterParams;
    }

    public static int getTotalVerifierNumber() {
        return domainIDVerifiersRegisterParams.size();
    }

    public static Set<Integer> getIDVerifiersId() {
        return domainIDVerifiersRegisterParams.keySet();
    }

    public static Map<Integer, byte[]> getIDVerifiersPublicKey() {
        if (idVerifiersPublicKeyMap == null){
            synchronized (idVerifiersPublicKeyMapLock){
                if (idVerifiersPublicKeyMap == null){
                    Map<Integer, byte[]> result = new HashMap<>();
                    domainIDVerifiersRegisterParams.forEach((id, registerParams) -> {
                        result.put(id, registerParams.getPublicKey());
                    });
                    idVerifiersPublicKeyMap = result;
                }
            }
        }
        return idVerifiersPublicKeyMap;
    }

    public static Map<Integer, InetSocketAddress> getIDVerifiersAddress() {
        if (idVerifiersAddressMap == null){
            synchronized (idVerifiersAddressMapLock){
                if (idVerifiersAddressMap == null){
                    Map<Integer, InetSocketAddress> result = new HashMap<>();
                    domainIDVerifiersRegisterParams.forEach((id, registerParams) -> {
                        result.put(id, new InetSocketAddress(registerParams.getHost(), registerParams.getListenPort()));
                    });
                    idVerifiersAddressMap = result;
                }
            }
        }
        return idVerifiersAddressMap;
    }

}
