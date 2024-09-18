package com.lingyikeji.backend.domain.entities;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/14. */
@Getter
@Setter
public class CategoryStats {
  private String l1Category;
  private int totalChatCount = 0;
  private int uniqueChatCount = 0;
  private int duplicateChatCount = 0;
  private Set<String> uniquePatientQuestions = new HashSet<>();
  private Set<String> uniqueQuestions = new HashSet<>();
  private Set<String> uniqueAnswers = new HashSet<>();

  public CategoryStats(Set<String> uniquePatientQuestions, String l1Category) {
    this.uniquePatientQuestions = uniquePatientQuestions;
    this.l1Category = l1Category;
  }

  public void recordChat(Message msg) {
    String content = msg.getContent();
    if (msg.fromPatient()) {
      ++totalChatCount;
      if (uniqueAnswers.contains(content)) {
        ++duplicateChatCount;
      } else {
        uniqueAnswers.add(content);
        ++uniqueChatCount;
      }
    } else {
      uniqueQuestions.add(content);
    }
  }
}
