package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.ConversationStats;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/14. */
@Getter
@Setter
public class ConversationStatsVO {
  private int totalCount;
  private int hitCount;
  private int missedCount;
  private List<CategoryStatsVO> categoryStatsList = new LinkedList<>();

  public static ConversationStatsVO fromConversationStats(ConversationStats conversationStats) {
    List<CategoryStatsVO> statsList =
        conversationStats.getStatsByCategory().values().stream()
            .map(CategoryStatsVO::fromCategoryStats)
            .toList();
    return new ConversationStatsVO(
        conversationStats.getTotalChatCount(),
        conversationStats.getHitChatCount(),
        conversationStats.getMissedChatCount(),
        statsList);
  }

  private ConversationStatsVO(
      int totalCount, int hitCount, int missedCount, List<CategoryStatsVO> categoryStatsList) {
    this.totalCount = totalCount;
    this.hitCount = hitCount;
    this.missedCount = missedCount;
    this.categoryStatsList = categoryStatsList;
  }
}
