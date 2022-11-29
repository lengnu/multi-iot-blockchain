package com.multi.domain.iot.auditagent.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationAddAuthenticationInformationInputBO {
  private String _pid;

  private String _identityProtectionInformation;

  private String _host;

  private BigInteger _port;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_pid);
    args.add(_identityProtectionInformation);
    args.add(_host);
    args.add(_port);
    return args;
  }
}
