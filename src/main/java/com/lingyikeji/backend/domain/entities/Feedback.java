package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/18. */
@Entity("feedback")
@Getter
@Setter
@NoArgsConstructor
public class Feedback extends BaseEntity {
  @Id private String id;
  private String userName;
  private int score;
  @Reference private Conversation conversation;
  private String message;

  public static Feedback create(
      String userName, int score, Conversation conversation, String message) {
    return new Feedback(null, userName, score, conversation, message);
  }

  private Feedback(
      String id, String userName, int score, Conversation conversation, String message) {
    super();
    this.id = id;
    this.userName = userName;
    this.score = score;
    this.conversation = conversation;
    this.message = message;
  }
}
