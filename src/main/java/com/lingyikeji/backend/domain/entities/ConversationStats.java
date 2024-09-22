package com.lingyikeji.backend.domain.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/** Created by Yifan Wang on 2024/9/14. */
@Getter
@Setter
@NoArgsConstructor
public class ConversationStats {
  private int totalChatCount = 0;
  private int hitChatCount = 0;
  private int missedChatCount = 0;
  private Patient patient;
  private Set<String> totalCategories = new HashSet<>();
  private Map<String, CategoryStats> statsByCategory = new HashMap<>();
  // helper variables
  private Message previousMsgWhenRecording;
  private String chestXRayQuestion;
  private String chestSoundQuestion;

  public ConversationStats(Conversation conversation) {
    this.patient = conversation.getPatient();
    this.totalCategories = conversation.getPatient().getAllL1Categories();
    this.totalCategories.forEach(
        category ->
            statsByCategory.put(
                category,
                new CategoryStats(
                    patient.getPatientQAList().stream()
                        .filter(patientQA -> Objects.equals(patientQA.getL1Category(), category))
                        .map(PatientQA::getQ)
                        .collect(Collectors.toSet()),
                    (int)
                        patient.getPatientQAList().stream()
                            .filter(
                                patientQA -> Objects.equals(patientQA.getL1Category(), category))
                            .map(PatientQA::getA)
                            .distinct()
                            .count(),
                    category)));
  }

  public void recordChat(Message msg) {
    if (msg.fromUser()) {
      if (msg.getContent().contains("胸片")) {
        this.askChestXRayQuestion(msg.getContent());
      }
      if (msg.getContent().contains("听诊")) {
        this.askChestSoundQuestion(msg.getContent());
      }
      this.previousMsgWhenRecording = msg;
      return;
    }

    String content = msg.getContent();
    ++totalChatCount;
    List<PatientQA> patientQAList = this.patient.findQAByAnswer(content);
    if (patientQAList.isEmpty()) {
      ++missedChatCount;
      return;
    }

    ++hitChatCount;
    for (PatientQA patientQA : patientQAList) {
      statsByCategory.get(patientQA.getL1Category()).recordChat(this.previousMsgWhenRecording);
      statsByCategory.get(patientQA.getL1Category()).recordChat(msg);
    }
  }

  public boolean hitChestXRayQuestion() {
    return StringUtils.isNotEmpty(this.getChestXRayQuestion());
  }

  private void askChestXRayQuestion(String question) {
    this.setChestXRayQuestion(question);
  }

  public boolean hitChestSoundQuestion() {
    return StringUtils.isNotEmpty(this.getChestSoundQuestion());
  }

  private void askChestSoundQuestion(String question) {
    this.setChestSoundQuestion(question);
  }
}
