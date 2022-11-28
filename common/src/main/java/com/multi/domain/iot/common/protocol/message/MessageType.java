package com.multi.domain.iot.common.protocol.message;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.message
 * @Author: duwei
 * @Date: 2022/11/17 11:27
 * @Description: 消息类型
 */
public interface MessageType {
    /**
     * 获取公共参数请求数据包
     */
    byte FETCH_PUBLIC_PARAMETER_REQUEST = 1;
    /**
     * 获取公共参数响应数据包
     */
    byte FETCH_PUBLIC_PARAMETER_RESPONSE = 2;
    /**
     * 注册公钥请求
     */
    byte ENROLL_INFORMATION_REQUEST = 3;
    /**
     * 注册公钥响应
     */
    byte ENROLL_INFORMATION_RESPONSE = 4;
    /**
     * 查询AA和所有IDV的信息请求，包括IP和公钥
     */
    byte QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_REQUEST = 5;
    /**
     * 查询AA和所有IDV的信息响应，包括IP和公钥
     */
    byte QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_RESPONSE = 6;
    /**
     * UD发送验证信息请求
     */
    byte AUTHENTICATION_MESSAGE_REQUEST = 7;
    /**
     * UD转发验证消息响应
     */
    byte AUTHENTICATION_MESSAGE_RESPONSE = 8;
    /**
     * IDV向AA发送的确认消息请求
     */
    byte CONFIRM_AUTHENTICATION_MESSAGE_REQUEST = 9;
    /**
     * IDV向AA发送的确认消息响应
     */
    byte CONFIRM_AUTHENTICATION_MESSAGE_RESPONSE = 10;
    /**
     * AA向UD返回假名请求
     */
    byte GENERATE_PID_REQUEST = 11;
    /**
     * AA向UD返回假名的响应
     */
    byte GENERATE_PID_RESPONSE = 12;
}
