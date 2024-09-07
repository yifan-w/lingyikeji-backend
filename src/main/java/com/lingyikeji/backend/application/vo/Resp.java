package com.lingyikeji.backend.application.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Created by Yifan Wang on 2023/10/16. */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resp {
  private int code;
  private Object data;
  private String errorMsg;

  public static Resp SUCCESS = new Resp(0, null, null);

  public static Resp success(Object data) {
    return new Resp(0, data, null);
  }

  public static Resp error(String msg) {
    return new Resp(-1, null, msg);
  }
}
