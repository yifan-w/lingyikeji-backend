package com.lingyikeji.backend.infra.gateway.dto;

import com.lingyikeji.backend.domain.entities.PatientQA;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/13. */
@Getter
@Setter
public class PatientQADTO {
  private String q;
  private String a;

  public static PatientQADTO fromPatientQA(PatientQA patientQA) {
    return new PatientQADTO(patientQA.getQ(), patientQA.getA());
  }

  private PatientQADTO(String q, String a) {
    this.q = q;
    this.a = a;
  }
}
