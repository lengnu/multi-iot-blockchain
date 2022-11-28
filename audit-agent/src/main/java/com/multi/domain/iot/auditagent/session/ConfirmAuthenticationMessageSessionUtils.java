package com.multi.domain.iot.auditagent.session;

import com.multi.domain.iot.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.session
 * @Author: duwei
 * @Date: 2022/11/22 15:44
 * @Description: 管理确认消息的会话
 */
@Slf4j
public class ConfirmAuthenticationMessageSessionUtils {
    // 临时存储各个验证者对于UD的确认消息
    private static volatile Map<String, Map<Integer, byte[]>> udConfirmAuthenticationMessageTemporaryMap = new HashMap<>();
    //对应的UD已经完成了PID的生成
    private static volatile Set<String> alreadyGeneratePIDSet = new HashSet<>();
    //正在收集对应UD的IDV发来的确认消息
    private static volatile Set<String> waitingCollectConfirmAuthenticationMessageSet = new HashSet<>();
    //正在等待对应的UD验证(pid - uid)
    private static volatile Map<String, String> pidIdentityProtectionInformationTemporaryMap = new HashMap<>();

    public static Collection<byte[]> getConfirmsInformation(String uid) {
        return udConfirmAuthenticationMessageTemporaryMap.get(uid).values();
    }

    /**
     * 判断该PID是否等待上链
     */
    public static boolean isWaitUpToBlockChain(String pid) {
        return pidIdentityProtectionInformationTemporaryMap.containsKey(pid);
    }

    /**
     * pid上链后将该pid从临时缓存里清楚
     */
    public static void removePidFromWaitUpToBlockChainTemporaryMap(String pid){
        pidIdentityProtectionInformationTemporaryMap.remove(pid);
    }

    /**
     * 所所有的IDV的消息是否都收集完毕
     */
    public synchronized static boolean isWaitEnoughConfirmMessage(String uid, int totalVerifierNumber) {
        return udConfirmAuthenticationMessageTemporaryMap.get(uid).size() == totalVerifierNumber;
    }

    public synchronized static void receiveOneConfirmMessage(String uid, Integer verifierId, byte[] confirmMessage, int totalVerifiersNumber, Domain domain) {
        udConfirmAuthenticationMessageTemporaryMap.get(uid).put(verifierId, confirmMessage);
        log.info("接收到了 [{}] 域内 ID为 [{}] 的身份验证者发来的确认消息，还需要等待 [{}] 个身份验证者的消息",
                domain.getDomainIdentity(), verifierId, totalVerifiersNumber - udConfirmAuthenticationMessageTemporaryMap.get(uid).size());
    }

    /**
     * 对应的UD是否还没开始确认
     */
    public synchronized static boolean isNotStartConfirm(String uid) {
        //既没有正在收集IDV发来的确认消息，也没有完成确认
        return !waitingCollectConfirmAuthenticationMessageSet.contains(uid)
                &&
                !alreadyGeneratePIDSet.contains(uid);
    }

    /**
     * 开始统计对某个UD的确认消息
     */
    public static void startCountConfirmMessage(String uid) {
        udConfirmAuthenticationMessageTemporaryMap.put(uid, new HashMap<>());
        waitingCollectConfirmAuthenticationMessageSet.add(uid);
    }

    /**
     * 做善后工作，清楚缓存，记录完成的ud
     */
    public static void afterMath(String uid, String pid, boolean success) {
        clearCache(uid);
        joinFinishSet(uid);
        if (success) {
            joinWaitUpToBlockChainMap(pid, uid);
        }
    }

    /**
     * 清楚存储的临时消息
     */
    private static void clearCache(String uid) {
        udConfirmAuthenticationMessageTemporaryMap.remove(uid);
        waitingCollectConfirmAuthenticationMessageSet.remove(uid);
    }

    /**
     * 对于已经生成了的PID，但是还没上链的临时放在里面
     */
    private static void joinWaitUpToBlockChainMap(String pid, String uid) {
        pidIdentityProtectionInformationTemporaryMap.put(pid, uid);
    }

    /**
     * 将对应的ud加入确认完成集合
     */
    private static void joinFinishSet(String uid) {
        alreadyGeneratePIDSet.add(uid);
    }

    public synchronized static boolean isGeneratePID(String uid) {
        return alreadyGeneratePIDSet.contains(uid);
    }
}
