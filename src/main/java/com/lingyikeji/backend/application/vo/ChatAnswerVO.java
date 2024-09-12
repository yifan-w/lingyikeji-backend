package com.lingyikeji.backend.application.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/15. */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatAnswerVO {
  private String answer;
  private String video;
  private String vrJson;
  private String vrWav;
  private Map<String, String> testResults = Collections.emptyMap();

  public static ChatAnswerVO create(String answer, String video, String vrJson, String vrWav) {
    return new ChatAnswerVO(answer, video, vrJson, vrWav);
  }

  private ChatAnswerVO(String answer, String video, String vrJson, String vrWav) {
    this.answer = answer;
    this.video = video;
    this.vrJson = vrJson;
    this.vrWav = vrWav;
    this.testResults =
        Map.of(
            "chestXrayImg",
            "https://img0.baidu.com/it/u=363180392,2243958475&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=882",
            "chestSoundAudio",
            "https://www.pixelgeom.com/livestream/lingyi/chest_sound_demo.mp3");
  }
}
