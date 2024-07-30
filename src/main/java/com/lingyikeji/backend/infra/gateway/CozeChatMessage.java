package com.lingyikeji.backend.infra.gateway;

import lombok.Data;

@Data
public class CozeChatMessage {
  private final String content;
  private final String role = "user";
  private final String content_type = "text";

  public static CozeChatMessage create(String content) {
    return new CozeChatMessage(content);
  }

  private CozeChatMessage(String content) {
    this.content = content;
  }
}
