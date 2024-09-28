package com.lingyikeji.backend.infra.gateway.dto;

import com.lingyikeji.backend.domain.entities.Patient;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/** Created by Yifan Wang on 2024/9/22. */
@Getter
@Setter
public class TTSDTO {
  private final Map<String, String> app =
      new HashMap<>(Map.of("appid", "9336944303", "token", "access_token"));
  private final Map<String, String> user = Map.of("uid", "388808087185088");
  private final Map<String, Object> audio =
      new HashMap<>(
          Map.of("encoding", "mp3", "speed_ratio", 1.0, "volume_ratio", 1.0, "pitch_ratio", 1.0));
  private final Map<String, Object> request =
      new HashMap<>(
          Map.of(
              "text_type", "plain", "operation", "query", "with_frontend", 0, "split_sentence", 1));

  public TTSDTO(String id, String text, Patient patient) {
    this.getApp()
        .put("cluster", patient.getSex() == Patient.Sex.MALE ? "volcano_tts" : "volcano_icl");
    this.getAudio()
        .put(
            "voice_type",
            StringUtils.isEmpty(patient.getVoiceType())
                ? (patient.getSex() == Patient.Sex.MALE ? "BV701_streaming" : "BV115_streaming")
                : patient.getVoiceType());
    this.getRequest().put("text", text);
    this.getRequest().put("reqid", id);
  }
}
