package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.Patient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/9. */
@Getter
@Setter
@NoArgsConstructor
public class PatientVO {
  private String id;
  private Patient.Sex sex;
  private String name;
  private int age;
  private String chiefComplaint;
  private String idleVideoUrl;

  public static PatientVO fromPatient(Patient patient) {
    return new PatientVO(
        patient.getId(),
        patient.getSex(),
        patient.getName(),
        patient.getAge(),
        patient.getChiefComplaint(),
        patient.getIdleVideoUrl());
  }

  private PatientVO(
      String id,
      Patient.Sex sex,
      String name,
      int age,
      String chiefComplaint,
      String idleVideoUrl) {
    this.id = id;
    this.sex = sex;
    this.name = name;
    this.age = age;
    this.chiefComplaint = chiefComplaint;
    this.idleVideoUrl = idleVideoUrl;
  }
}
