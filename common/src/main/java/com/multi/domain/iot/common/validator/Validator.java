package com.multi.domain.iot.common.validator;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.verify
 * @Author: duwei
 * @Date: 2022/11/22 9:03
 * @Description: 验证器接口
 */
@FunctionalInterface
public interface Validator {
      boolean verify();
}
