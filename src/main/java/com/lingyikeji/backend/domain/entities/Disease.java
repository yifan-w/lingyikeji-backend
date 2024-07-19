package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/18. */
@Entity("disease")
@Getter
@Setter
@NoArgsConstructor
public class Disease {
  @Id private String id;
  private String name;
  private String desc;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Disease create(String name, String desc) {
    return new Disease(null, name, desc);
  }

  private Disease(String id, String name, String desc) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }
}
