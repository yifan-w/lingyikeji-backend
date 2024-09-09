package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/** Created by Yifan Wang on 2024/9/9. */
@Getter
@Setter
@NoArgsConstructor
public class ConversationVO {
  private String id;
  private PatientVO patient;
  private String userName;
  private List<Message> msgList = new LinkedList<>();

  public static ConversationVO fromConversation(Conversation conversation) {
    return new ConversationVO(
        conversation.getId(),
        PatientVO.fromPatient(conversation.getPatient()),
        conversation.getUserName(),
        conversation.getMsgList());
  }

  private ConversationVO(String id, PatientVO patient, String userName, List<Message> msgList) {
    this.id = id;
    this.patient = patient;
    this.userName = userName;
    this.msgList = msgList;
  }
}
