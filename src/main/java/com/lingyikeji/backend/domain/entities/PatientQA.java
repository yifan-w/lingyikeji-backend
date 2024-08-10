package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/7. */
@Embedded
@Getter
@Setter
@NoArgsConstructor
public class PatientQA {
  private String q;
  private String a;

  @Expose(serialize = false)
  private String videoUrl;

  public static PatientQA create(String q, String a, String videoUrl) {
    return new PatientQA(q, a, videoUrl);
  }

  private PatientQA(String q, String a, String videoUrl) {
    this.q = q;
    this.a = a;
    this.videoUrl = videoUrl;
  }

  public String getVideoUrl() {
    return "https://www.pixelgeom.com/api/lingyi/getLivestream?id="
        + videoUrl.substring(0, videoUrl.length() - 3);
  }

  public String toString() {
    return "{\"q\":\"" + q + "\",\"a\":\"" + a + "\"}";
  }
}
