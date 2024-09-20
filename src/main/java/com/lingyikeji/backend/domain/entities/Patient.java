package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
  private String silentVideoUrl;
  private Map<String, String> testResults;

  @Expose(serialize = false)
  private List<PatientQA> patientQAList = new LinkedList<>();

  public static enum Sex {
    MALE,
    FEMALE
  }

  public Patient() {
    super();
  }

  public Set<String> getAllL1Categories() {
    return this.getPatientQAList().stream()
        .map(PatientQA::getL1Category)
        .filter(Objects::nonNull)
        // .filter(category -> !Objects.equals(category, "主诉"))
        .collect(Collectors.toSet());
  }

  public List<PatientQA> findQAByAnswer(String answer) {
    return this.getPatientQAList().stream()
        .filter(patientQA -> patientQA.getA().contains(answer))
        .toList();
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
