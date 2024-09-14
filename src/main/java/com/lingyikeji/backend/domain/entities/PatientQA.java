package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Embedded;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/7. */
@Embedded
@Getter
@Setter
@NoArgsConstructor
public class PatientQA {
  private String l1Category;
  private String q;
  private String a;
  private String videoUrl;
  private String vrJsonUrl;
  private String vrWavUrl;

  public static PatientQA create(
      String l1Category, String q, String a, String videoUrl, String vrJsonUrl, String vrWavUrl) {
    return new PatientQA(l1Category, q, a, videoUrl, vrJsonUrl, vrWavUrl);
  }

  private PatientQA(
      String l1Category, String q, String a, String videoUrl, String vrJsonUrl, String vrWavUrl) {
    this.l1Category = l1Category;
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
    return Optional.ofNullable(this.vrJsonUrl)
        .map(jsonUrl -> "https://www.pixelgeom.com/livestream/lingyi/vr/json/" + jsonUrl)
        .orElse(null);
  }

  public String getVrWavUrl() {
    return Optional.ofNullable(this.vrWavUrl)
        .map(wavUrl -> "https://www.pixelgeom.com/livestream/lingyi/vr/wav/" + wavUrl)
        .orElse(null);
  }
}
