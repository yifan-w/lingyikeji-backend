package com.lingyikeji.backend.facade.vo;

import com.lingyikeji.backend.domain.entities.Patient;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/12. */
@Getter
@Setter
public class PatientVO {
  private String patientId;
  private String name;
  private String age;
  private String gender;
  private String departmentId;
  private String departmentName;
  private String chiefComplaint;
  private String idleVideoUrl;

  public static PatientVO fromPatient(Patient patient) {
    return new PatientVO(
        patient.getId(),
        patient.getName(),
        patient.getAge() + "岁",
        patient.getSex() == Patient.Sex.MALE ? "1" : "2",
        "66b8719ecfe54b4ce9c69074",
        "呼吸内科",
        patient.getChiefComplaint(),
        patient.getIdleVideoUrl());
  }

  private PatientVO(
      String patientId,
      String name,
      String age,
      String gender,
      String departmentId,
      String departmentName,
      String chiefComplaint,
      String idleVideoUrl) {
    this.patientId = patientId;
    this.name = name;
    this.age = age;
    this.gender = gender;
    this.departmentId = departmentId;
    this.departmentName = departmentName;
    this.chiefComplaint = chiefComplaint;
    this.idleVideoUrl = idleVideoUrl;
  }

  public String getIdleVideoUrl() {
    return "https://www.pixelgeom.com/livestream/lingyi/" + this.idleVideoUrl;
  }
}
