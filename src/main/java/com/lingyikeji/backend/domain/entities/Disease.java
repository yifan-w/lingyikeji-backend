package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/18. */
@Entity("disease")
@Getter
@Setter
@NoArgsConstructor
public class Disease extends BaseEntity {
  @Id private String id;
  private String name;
  private String desc;

  public static Disease create(String name, String desc) {
    return new Disease(null, name, desc);
  }

  private Disease(String id, String name, String desc) {
    super();
    this.id = id;
    this.name = name;
    this.desc = desc;
  }
}
