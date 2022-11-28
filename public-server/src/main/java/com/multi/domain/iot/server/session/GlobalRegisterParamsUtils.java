package com.multi.domain.iot.server.session;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.param.RegisterParams;
import com.multi.domain.iot.common.role.Role;
import lombok.extern.slf4j.Slf4j;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.session
 * @Author: duwei
 * @Date: 2022/11/21 16:59
 * @Description: 全局客户端的注册信息、包括公钥、IP、Port、域、身份
 */
@Slf4j
public class GlobalRegisterParamsUtils {
    /**
     * 为注册的用户分配ID
     */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
    private static RegisterParams auditAgentRegisterParams;
    private static final Map<Domain,Map<Integer, RegisterParams>> domainIDVerifiersRegisterParams = new ConcurrentHashMap<>();


    /**
     * 对AA和IDV审计注册
     * 返回 0：审计代理注册成功
     * 返回-1：审计代理注册失败
     * 返回 > 0 : IDV注册成功，返回值为分配给其ID
     */
    public static int register(RegisterParams registerParams){
        if (registerParams.getRole() == Role.AA){
            return registerAuditAgent(registerParams);
        }else if (registerParams.getRole() == Role.IDV){
            return registerIDVerifier(registerParams);
        }
        return Integer.MAX_VALUE;
    }

    synchronized private static int registerAuditAgent(RegisterParams registerParams){
        log.info("监听到有审计代理进行注册");
        if (auditAgentRegisterParams != null){
            return -1;
        }
        auditAgentRegisterParams = registerParams;
        return 0;
    }

    private static int registerIDVerifier(RegisterParams registerParams){
        Domain domain = registerParams.getDomain();
        if (!domainIDVerifiersRegisterParams.containsKey(domain)){
            domainIDVerifiersRegisterParams.put(domain,new HashMap<>());
        }
        Integer curIDVerifierId = ATOMIC_INTEGER.getAndIncrement();
        registerParams.setId(curIDVerifierId);
        domainIDVerifiersRegisterParams.get(domain).put(curIDVerifierId,registerParams);
        log.info("监听到 [{}] 域内有身份验证者进行注册，服务器为其分配ID为 [{}]",domain.getDomainIdentity(),curIDVerifierId);
        return curIDVerifierId;
    }

    public static RegisterParams getAuditAgentRegisterParams(){
        return auditAgentRegisterParams;
    }

    public static Map<Integer,RegisterParams> getDomainIDVerifiersRegisterParams(Domain domain) {
        return domainIDVerifiersRegisterParams.get(domain);
    }

    public static boolean containsDomain(Domain domain){
        return domainIDVerifiersRegisterParams.containsKey(domain);
    }

    public static boolean containAuditAgent(){
        return auditAgentRegisterParams != null;
    }
}
