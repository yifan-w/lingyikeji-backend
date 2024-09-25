package com.lingyikeji.backend.application.vo;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/23. */
@Getter
@Setter
public class DiagnosisStatsVO {
  private String correctOption;
  private String chosenOption;

  public DiagnosisStatsVO(String correctOption, String chosenOption) {
    this.correctOption = correctOption;
    this.chosenOption = chosenOption;
  }

  public boolean isMatch() {
    return Objects.equals(correctOption, chosenOption);
  }
}
