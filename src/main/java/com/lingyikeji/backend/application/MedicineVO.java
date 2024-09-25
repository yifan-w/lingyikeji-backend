package com.lingyikeji.backend.application;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/23. */
@Getter
@Setter
public class MedicineVO {
  private List<String> correctMedicines;
  private List<String> incorrectMedicines;

  public MedicineVO(List<String> correctMedicines, List<String> incorrectMedicines) {
    this.correctMedicines = correctMedicines;
    this.incorrectMedicines = incorrectMedicines;
  }
}
