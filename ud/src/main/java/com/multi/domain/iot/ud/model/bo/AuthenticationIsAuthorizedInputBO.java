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
public class AuthenticationIsAuthorizedInputBO {
  private String pid;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(pid);
    return args;
  }
}