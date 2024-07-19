package com.lingyikeji.backend.application.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Created by Yifan Wang on 2023/10/16. */
@Data
@AllArgsConstructor
public class Resp {
  private int code;
  private Object data;

  public static Resp SUCCESS = new Resp(0, null);

  public static Resp success(Object data) {
    return new Resp(0, data);
  }
}
