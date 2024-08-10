package com.lingyikeji.backend.domain.entities;

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
  @Reference private List<Disease> diseaseList = new LinkedList<>();

  public static Department create(String name, List<Disease> diseaseList) {
    return new Department(null, name, diseaseList);
  }

  private Department(String id, String name, List<Disease> diseaseList) {
    super();
    this.id = id;
    this.name = name;
    this.diseaseList = diseaseList;
  }
}
