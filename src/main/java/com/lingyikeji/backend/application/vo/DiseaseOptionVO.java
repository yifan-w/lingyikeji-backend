package com.lingyikeji.backend.application.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/23. */
@Getter
@Setter
public class DiseaseOptionVO {
  private List<String> correctDiseaseOptions;
  private List<String> incorrectDiseaseOptions;

  public DiseaseOptionVO(List<String> correctDiseaseOptions, List<String> incorrectDiseaseOptions) {
    this.correctDiseaseOptions = correctDiseaseOptions;
    this.incorrectDiseaseOptions = incorrectDiseaseOptions;
  }
}
