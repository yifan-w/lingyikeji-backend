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

  @Expose(serialize = false)
  private String vrJsonUrl;

  @Expose(serialize = false)
  private String vrWavUrl;

  public static PatientQA create(
      String q, String a, String videoUrl, String vrJsonUrl, String vrWavUrl) {
    return new PatientQA(q, a, videoUrl, vrJsonUrl, vrWavUrl);
  }

  private PatientQA(String q, String a, String videoUrl, String vrJsonUrl, String vrWavUrl) {
    this.q = q;
    this.a = a;
    this.videoUrl = videoUrl;
    this.vrJsonUrl = vrJsonUrl;
    this.vrWavUrl = vrWavUrl;
  }

  public String getVideoUrl() {
    return "https://www.pixelgeom.com/livestream/lingyi/"
        + this.videoUrl.substring(0, this.videoUrl.indexOf("."))
        + ".mp4";
  }

  public String getVrJsonUrl() {
    return "https://www.pixelgeom.com/livestream/lingyi/vr/json/" + this.vrJsonUrl;
  }

  public String getVrWavUrl() {
    return "https://www.pixelgeom.com/livestream/lingyi/vr/wav/" + this.vrWavUrl;
  }
}
