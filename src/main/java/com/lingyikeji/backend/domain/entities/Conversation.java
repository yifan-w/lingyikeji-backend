package com.lingyikeji.backend.domain.entities;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

/** Created by Yifan Wang on 2024/7/18. */
@Entity("conversation")
@Getter
@Setter
@NoArgsConstructor
public class Conversation extends BaseEntity {
  @Id private String id;

  @Expose(serialize = false)
  @Reference
  private Department department;

  @Reference private Patient patient;

  private String deptName;
  private String userName;
  private List<Message> msgList = new LinkedList<>();

  public static Conversation create(
      String userName, Department department, Patient patient, List<Message> msgList) {
    return new Conversation(null, userName, department, patient, msgList);
  }

  private Conversation(
      String id, String userName, Department department, Patient patient, List<Message> msgList) {
    super();
    this.id = id;
    this.userName = userName;
    this.department = department;
    this.patient = patient;
    this.deptName = department.getName();
    if (CollectionUtils.isNotEmpty(msgList)) {
      this.msgList = msgList;
    }
  }

  public void addUserMsg(String msg) {
    msgList.add(Message.fromUser(msg));
  }

  public void addPatientMsg(String msg) {
    msgList.add(Message.fromPatient(msg));
  }

  public ConversationStats generateStats() {
    ConversationStats conversationStats = new ConversationStats(this);
    msgList.stream()
        .filter(Message::fromPatient)
        .map(Message::getContent)
        .forEach(conversationStats::recordPatientChat);
    return conversationStats;
  }
}
