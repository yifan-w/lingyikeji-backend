package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/7. */
@Entity("patient")
@Getter
@Setter
public class Patient extends BaseEntity {
  @Id private String id;
  private Sex sex;
  private String name;
  private int age;
  private String desc;
  private String chiefComplaint;
  private String idleVideoUrl;

  @Expose(serialize = false)
  private List<PatientQA> patientQAList = new LinkedList<>();

  public static enum Sex {
    MALE,
    FEMALE
  }

  public Patient() {
    super();
  }

  public static Patient create(
      Sex sex,
      String name,
      int age,
      String desc,
      String chiefComplaint,
      String idleVideoUrl,
      List<PatientQA> patientQAList) {
    return new Patient(null, sex, name, age, desc, chiefComplaint, idleVideoUrl, patientQAList);
  }

  public Patient(
      String id,
      Sex sex,
      String name,
      int age,
      String desc,
      String chiefComplaint,
      String idleVideoUrl,
      List<PatientQA> patientQAList) {
    this.id = id;
    this.sex = sex;
    this.name = name;
    this.age = age;
    this.desc = desc;
    this.chiefComplaint = chiefComplaint;
    this.idleVideoUrl = idleVideoUrl;
    this.patientQAList = patientQAList;
  }

  public Set<String> getAllL1Categories() {
    return this.getPatientQAList().stream()
        .map(PatientQA::getL1Category)
        .filter(Objects::nonNull)
        // .filter(category -> !Objects.equals(category, "主诉"))
        .collect(Collectors.toSet());
  }

  public Optional<PatientQA> findQAByAnswer(String answer) {
    return this.getPatientQAList().stream()
        .filter(patientQA -> patientQA.getA().contains(answer))
        .findFirst();
  }

  public void updateWith(Patient patient) {
    this.setSex(patient.getSex());
    this.setName(patient.getName());
    this.setAge(patient.getAge());
    this.setChiefComplaint(patient.getChiefComplaint());
    this.setIdleVideoUrl(patient.getIdleVideoUrl());
    this.setPatientQAList(patient.getPatientQAList());
  }
}
