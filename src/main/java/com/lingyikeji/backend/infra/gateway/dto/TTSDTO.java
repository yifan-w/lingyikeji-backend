package com.lingyikeji.backend.infra.gateway.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/22. */
@Getter
@Setter
public class TTSDTO {
  private final Map<String, String> app =
      Map.of("appid", "9336944303", "token", "access_token", "cluster", "volcano_tts");
  private final Map<String, String> user = Map.of("uid", "388808087185088");
  private final Map<String, Object> audio =
      new HashMap<>(
          Map.of("encoding", "mp3", "speed_ratio", 1.0, "volume_ratio", 1.0, "pitch_ratio", 1.0));
  private final Map<String, Object> request =
      new HashMap<>(
          Map.of(
              "reqid",
              "1",
              "text_type",
              "plain",
              "operation",
              "query",
              "with_frontend",
              0,
              "split_sentence",
              1));

  public TTSDTO(String text, boolean isMale) {
    this.getAudio().put("voice_type", isMale ? "BV701_streaming" : "BV700_V2_streaming");
    this.getRequest().put("text", text);
  }
}
