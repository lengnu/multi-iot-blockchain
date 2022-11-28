package com.multi.domain.iot.common.message;

import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-28 11:22
 * @description PID及其验证消息
 */
@Data
public class PidAndSignMessage {
    private byte[] pid;
    private byte[] hash;
    private byte[] sign;
}
