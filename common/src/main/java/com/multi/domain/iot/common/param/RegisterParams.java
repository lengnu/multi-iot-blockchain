package com.multi.domain.iot.common.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.role.Role;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-24 22:30
 * @description 需要在服务器的注册参数
 */
@Data
public class RegisterParams {
    private Integer id;
    private Role role;
    private byte[] publicKey;
    private String host;
    private int listenPort;
    private Domain domain;


    @Override
    public String toString(){
      return
              "id = " + id + "\n" +
              "role = " + role + "\n" +
              "publibKey = " + Base64.encodeBase64String(publicKey) + "\n" +
              "host = " + host + "\n" +
              "listenPort = " + listenPort + "\n" ;
    }
}
