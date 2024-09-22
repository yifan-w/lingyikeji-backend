package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.CategoryStats;
import java.util.LinkedList;
import java.util.List;
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
  private String questionHitRatio = "0%";
  private List<String> hintQuestions = new LinkedList<>();
  private List<String> historyQuestions = new LinkedList<>();
  private List<String> msgList = new LinkedList<>();

  public static CategoryStatsVO fromCategoryStats(CategoryStats stats) {
    return new CategoryStatsVO(
        stats.getL1Category(),
        stats.getTotalChatCount(),
        stats.getUniqueChatCount(),
        stats.getDuplicateChatCount(),
        stats.getUniquePatientAnswerCount(),
        new LinkedList<>(stats.getUniquePatientQuestions()),
        new LinkedList<>(stats.getUniqueQuestions()),
        new LinkedList<>(stats.getUniqueAnswers()));
  }

  private CategoryStatsVO(
      String l1Category,
      int totalCount,
      int uniqueCount,
      int duplicateCount,
      int uniquePatientAnswerCount,
      List<String> hintQuestions,
      List<String> historyQuestions,
      List<String> msgList) {
    this.l1Category = l1Category;
    this.totalCount = totalCount;
    this.uniqueCount = uniqueCount;
    this.duplicateCount = duplicateCount;
    this.hintQuestions = hintQuestions;
    this.historyQuestions = historyQuestions;
    this.msgList = msgList;
    if (uniqueCount > 0) {
      this.questionHitRatio =
          String.format("%.2f", 100.0 * uniqueCount / uniquePatientAnswerCount) + "%";
    }
  }
}
