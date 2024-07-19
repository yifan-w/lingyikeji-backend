package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/18. */
@Entity("conversation")
@Getter
@Setter
@NoArgsConstructor
public class Conversation {
  @Id private String id;
  @Reference private Disease disease;
  private List<Message> msgList = new LinkedList<>();
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Conversation create(Disease disease, Message message) {
    List<Message> msgList = new LinkedList<>();
    msgList.add(message);
    return new Conversation(null, disease, msgList);
  }

  private Conversation(String id, Disease disease, List<Message> msgList) {
    this.id = id;
    this.disease = disease;
    this.msgList = msgList;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }
}
