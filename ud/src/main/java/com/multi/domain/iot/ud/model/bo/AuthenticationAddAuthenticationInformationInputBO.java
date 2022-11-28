package com.multi.domain.iot.ud.model.bo;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationAddAuthenticationInformationInputBO {
  private String pid;

  private String identityProtectionInformation;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(pid);
    args.add(identityProtectionInformation);
    return args;
  }
}
