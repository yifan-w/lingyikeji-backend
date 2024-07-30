package com.lingyikeji.backend.infra.gateway;

import lombok.Data;

import java.util.List;

@Data
public class CozeChatBody {
  private final String bot_id;
  private final String user_id;
  private final boolean stream = true;
  private final List<CozeChatMessage> additional_messages;

  public static CozeChatBody create(String bot_id, String user_id, List<CozeChatMessage> additional_messages) {
    return new CozeChatBody(bot_id, user_id, additional_messages);
  }

  private CozeChatBody(String bot_id, String user_id, List<CozeChatMessage> additional_messages) {
    this.bot_id = bot_id;
    this.user_id = user_id;
    this.additional_messages = additional_messages;
  }
}
