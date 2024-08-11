package com.lingyikeji.backend.domain.entities;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/8/7. */
@Getter
@Setter
public class BaseEntity {
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
