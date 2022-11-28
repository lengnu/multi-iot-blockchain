package com.multi.domain.iot.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.Enumeration;

/**
 * @Author: duwei
 * @Date: 2022/7/11 17:45
 * @Description: 获取IP工具类
 */
@Slf4j
public class IPUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    /**
     * 根据linux和windows系统获取本地IP
     * 获取不到返回null
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            if (isWindows()) {
                return Inet4Address.getLocalHost().getHostAddress();
            } else if (isLinux()) {
                NetworkInterface ens33 = NetworkInterface.getByName("ens33");
                Enumeration<InetAddress> inetAddresses = ens33.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet6Address) {
                        continue;
                    }
                    return inetAddress.getHostAddress();
                }
            }else {
                return Inet4Address.getLocalHost().getHostAddress();
            }
        } catch (SocketException | UnknownHostException e) {
            log.error(e.getMessage());
        }
        return null;
    }


}
