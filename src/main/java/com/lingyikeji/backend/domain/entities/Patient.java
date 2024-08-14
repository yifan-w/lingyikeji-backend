package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.LinkedList;
import java.util.List;
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

  public static Patient create(String name, int age, List<PatientQA> patientQAList) {
    return new Patient(null, name, age, patientQAList);
  }

  private Patient(String id, String name, int age, List<PatientQA> patientQAList) {
    super();
    this.id = id;
    this.name = name;
    this.age = age;
    this.patientQAList = patientQAList;
  }
}
