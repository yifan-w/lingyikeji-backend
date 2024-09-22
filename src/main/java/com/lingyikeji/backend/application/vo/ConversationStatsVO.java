package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.ConversationStats;
import java.util.Collections;
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
  private List<TestStatsVO> diagnosticTestStatsList = new LinkedList<>();
  private List<TestStatsVO> physicalTestStatsList = new LinkedList<>();

  public static ConversationStatsVO fromConversationStats(ConversationStats conversationStats) {
    List<CategoryStatsVO> statsList =
        conversationStats.getStatsByCategory().values().stream()
            .map(CategoryStatsVO::fromCategoryStats)
            .toList();
    TestStatsVO chestXRayStats =
        TestStatsVO.create("胸片", List.of("请出示胸片结果"), Collections.emptyList());
    if (conversationStats.hitChestXRayQuestion()) {
      chestXRayStats =
          TestStatsVO.create(
              "胸片", List.of("请出示胸片结果"), List.of(conversationStats.getChestXRayQuestion()));
    }

    TestStatsVO chestSoundStats =
        TestStatsVO.create("肺部听诊", List.of("请让我来为你听诊"), Collections.emptyList());
    if (conversationStats.hitChestSoundQuestion()) {
      chestSoundStats =
          TestStatsVO.create(
              "肺部听诊", List.of("请让我来为你听诊"), List.of(conversationStats.getChestSoundQuestion()));
    }

    return new ConversationStatsVO(
        conversationStats.getTotalChatCount(),
        conversationStats.getHitChatCount(),
        conversationStats.getMissedChatCount(),
        statsList,
        chestXRayStats,
        chestSoundStats);
  }

  private ConversationStatsVO(
      int totalCount,
      int hitCount,
      int missedCount,
      List<CategoryStatsVO> categoryStatsList,
      TestStatsVO chestXRayStats,
      TestStatsVO chestSoundStats) {
    this.totalCount = totalCount;
    this.hitCount = hitCount;
    this.missedCount = missedCount;
    this.categoryStatsList = categoryStatsList;
    this.diagnosticTestStatsList.add(chestXRayStats);
    this.physicalTestStatsList.add(chestSoundStats);
  }
}
