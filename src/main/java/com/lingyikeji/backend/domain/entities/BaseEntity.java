package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/7. */
@Getter
@Setter
public class BaseEntity {
  @Expose(deserialize = false)
  private LocalDateTime createdAt;

  @Expose(deserialize = false)
  private LocalDateTime updatedAt;

  protected BaseEntity() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }
}
