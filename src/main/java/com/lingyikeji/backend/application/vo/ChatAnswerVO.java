package com.lingyikeji.backend.application.vo;

import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/15. */
@Getter
@Setter
public class ChatAnswerVO {
  private String answer;
  private String video;
  private String vrJson;
  private String vrWav;

  public static ChatAnswerVO create(String answer, String video, String vrJson, String vrWav) {
    return new ChatAnswerVO(answer, video, vrJson, vrWav);
  }

  private ChatAnswerVO(String answer, String video, String vrJson, String vrWav) {
    this.answer = answer;
    this.video = video;
    this.vrJson = vrJson;
    this.vrWav = vrWav;
  }
}
