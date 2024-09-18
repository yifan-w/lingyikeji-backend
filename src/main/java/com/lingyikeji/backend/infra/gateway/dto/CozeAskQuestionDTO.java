package com.lingyikeji.backend.infra.gateway.dto;

import com.lingyikeji.backend.domain.entities.Patient;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/18. */
@Getter
@Setter
public class CozeAskQuestionDTO {
  private String question = "";
  private List<PatientQADTO> qa_list = new LinkedList<>();
  private String medical_record = "";

  public static CozeAskQuestionDTO create(Patient patient, String question) {
    return new CozeAskQuestionDTO(
        question,
        patient.getPatientQAList().stream().map(PatientQADTO::fromPatientQA).toList(),
        patient.getDesc());
  }

  private CozeAskQuestionDTO(String question, List<PatientQADTO> qa_list, String medical_record) {
    this.question = question;
    this.qa_list = qa_list;
    this.medical_record = medical_record;
  }
}
