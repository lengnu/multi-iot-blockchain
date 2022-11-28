package com.multi.domain.iot.common.session;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.sessio
 * @Author: duwei
 * @Date: 2022/11/21 16:58
 * @Description: 存储各个实体的会话信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Integer id;
    private byte[] publicKey;
    private Role role;
    private String host;
    private int listenPort;
    private Domain domain;
}