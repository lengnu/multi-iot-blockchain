package com.multi.domain.iot.auditagent.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationIsAuthorizedInputBO {
  private String _pid;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_pid);
    return args;
  }
}
