package com.lingyikeji.backend.infra.gateway;

import java.util.List;
import lombok.Data;

/** Created by Yifan Wang on 2024/8/5. */
@Data
public class OpenAIChatBody {
  public static OpenAIChatBody create(String message) {
    return new OpenAIChatBody(List.of(new OpenAIChatMessage(message)));
  }

  private OpenAIChatBody(List<OpenAIChatMessage> messages) {
    this.messages = messages;
  }

  @Data
  private static class OpenAIChatMessage {
    public OpenAIChatMessage(String content) {
      this.content = content;
    }

    private String role = "user";
    private String content;
  }

  private String model = "gpt-4o";
  private List<OpenAIChatMessage> messages;
}
