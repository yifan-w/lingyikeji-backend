package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.CategoryStats;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/14. */
@Getter
@Setter
public class CategoryStatsVO {
  private String l1Category;
  private int totalCount;
  private int uniqueCount;
  private int duplicateCount;

  public static CategoryStatsVO fromCategoryStats(CategoryStats stats) {
    return new CategoryStatsVO(
        stats.getL1Category(),
        stats.getTotalChatCount(),
        stats.getUniqueChatCount(),
        stats.getDuplicateChatCount());
  }

  private CategoryStatsVO(String l1Category, int totalCount, int uniqueCount, int duplicateCount) {
    this.l1Category = l1Category;
    this.totalCount = totalCount;
    this.uniqueCount = uniqueCount;
    this.duplicateCount = duplicateCount;
  }
}
