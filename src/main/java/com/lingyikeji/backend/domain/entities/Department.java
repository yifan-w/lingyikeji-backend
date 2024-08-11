package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/1. */
@Entity("department")
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseEntity {
  @Id private String id;
  private String name;

  @Expose(serialize = false)
  @Reference
  private List<Department> subDepartments = new LinkedList<>();

  @Reference private List<Patient> patientList = new LinkedList<>();

  public static Department create(
      String name, List<Department> subDepartments, List<Patient> patientList) {
    return new Department(null, name, subDepartments, patientList);
  }

  private Department(
      String id, String name, List<Department> subDepartments, List<Patient> patientList) {
    super();
    this.id = id;
    this.name = name;
    this.subDepartments = subDepartments;
    this.patientList = patientList;
  }
}
