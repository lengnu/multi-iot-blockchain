package com.multi.domain.iot.ud.model.bo;

import java.lang.Object;
import java.lang.String;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
