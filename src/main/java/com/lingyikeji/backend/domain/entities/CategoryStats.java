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
  private Set<String> uniqueMsgSet = new HashSet<>();

  public CategoryStats(String l1Category) {
    this.l1Category = l1Category;
  }

  public void recordPatientChat(String content) {
    ++totalChatCount;
    if (uniqueMsgSet.contains(content)) {
      ++duplicateChatCount;
    } else {
      uniqueMsgSet.add(content);
      ++uniqueChatCount;
    }
  }
}
