package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
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
public class Conversation extends BaseEntity {
  @Id private String id;
  @Reference private Disease disease;
  private List<Message> msgList = new LinkedList<>();

  public static Conversation create(Disease disease, Message message) {
    List<Message> msgList = new LinkedList<>();
    msgList.add(message);
    return new Conversation(null, disease, msgList);
  }

  private Conversation(String id, Disease disease, List<Message> msgList) {
    super();
    this.id = id;
    this.disease = disease;
    this.msgList = msgList;
  }
}
