package com.lingyikeji.backend.application.vo;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

/** Created by Yifan Wang on 2024/9/20. */
@Getter
@Setter
public class TestStatsVO {
  private String l1Category;
  private String hitRatio = "0%";
  private List<String> hintQuestions = new LinkedList<>();
  private List<String> historyQuestions = new LinkedList<>();

  public static TestStatsVO create(
      String l1Category, List<String> hintQuestions, List<String> historyQuestions) {
    return new TestStatsVO(l1Category, hintQuestions, historyQuestions);
  }

  private TestStatsVO(
      String l1Category, List<String> hintQuestions, List<String> historyQuestions) {
    this.l1Category = l1Category;
    if (CollectionUtils.isNotEmpty(historyQuestions)) {
      this.hitRatio =
          String.format("%.2f", 100.0 * hintQuestions.size() / hintQuestions.size()) + "%";
    }
    this.hintQuestions = hintQuestions;
    this.historyQuestions = historyQuestions;
  }

  private TestStatsVO(String l1Category) {
    this.l1Category = l1Category;
  }
}
