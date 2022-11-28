package com.multi.domain.iot.verifier.session;


import com.multi.domain.iot.common.param.RegisterParams;
import org.apache.commons.codec.binary.Base64;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.session
 * @Author: duwei
 * @Date: 2022/11/22 15:04
 * @Description: 记录UD的身份信息和对应的份额, 以及auditAgent的地址和公钥
 */
public class GlobalRegisterParamsUtils {
    //身份保护信息位Key(经过Base64编码的)
    private static final Map<String, byte[]> udIdentityProtectionInformationShareMap = new ConcurrentHashMap<>();
    private static RegisterParams auditAgentRegisterParams;

    /**
     * 存储认证通过的UD的份额
     */
    public static void bindShares(byte[] udIdentityProtectionInformation, byte[] udShare) {
        udIdentityProtectionInformationShareMap.put(Base64.encodeBase64String(udIdentityProtectionInformation), udShare);
    }

    public static void storeAuditAgentAddress(RegisterParams registerParams){
        auditAgentRegisterParams = registerParams;
    }

    public static InetSocketAddress getAuditAgentAddress(){
        return new InetSocketAddress(auditAgentRegisterParams.getHost(), auditAgentRegisterParams.getListenPort());
    }

    public static boolean isGeneratedPid(String uid){
        return udIdentityProtectionInformationShareMap.containsKey(uid);
    }


}
