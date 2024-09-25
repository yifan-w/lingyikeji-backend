package com.lingyikeji.backend.application.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.ConversationStats;
import com.lingyikeji.backend.domain.entities.Patient;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

/** Created by Yifan Wang on 2024/9/14. */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationStatsVO {
  private int totalCount;
  private int hitCount;
  private int missedCount;
  private List<CategoryStatsVO> categoryStatsList = new LinkedList<>();
  private List<TestStatsVO> diagnosticTestStatsList = new LinkedList<>();
  private List<TestStatsVO> physicalTestStatsList = new LinkedList<>();
  private DiagnosisStatsVO diseaseStats;
  private DiagnosisStatsVO medicineStats;

  public static ConversationStatsVO fromConversationStats(ConversationStats conversationStats) {
    List<CategoryStatsVO> statsList =
        conversationStats.getStatsByCategory().values().stream()
            .map(CategoryStatsVO::fromCategoryStats)
            .toList();
    TestStatsVO chestXRayStats =
        TestStatsVO.create("胸片", List.of("请出示胸片结果"), Collections.emptyList());
    if (conversationStats.hitChestXRayQuestion()) {
      chestXRayStats =
          TestStatsVO.create(
              "胸片", List.of("请出示胸片结果"), List.of(conversationStats.getChestXRayQuestion()));
    }

    TestStatsVO chestSoundStats =
        TestStatsVO.create("肺部听诊", List.of("请让我来为你听诊"), Collections.emptyList());
    if (conversationStats.hitChestSoundQuestion()) {
      chestSoundStats =
          TestStatsVO.create(
              "肺部听诊", List.of("请让我来为你听诊"), List.of(conversationStats.getChestSoundQuestion()));
    }

    Conversation conversation = conversationStats.getConversation();
    Patient patient = conversation.getPatient();
    DiagnosisStatsVO diseaseStats = null;
    if (CollectionUtils.isNotEmpty(patient.getCorrectDiseaseOptions())) {
      if (conversation.getDiagnosedDisease() == null) {
        diseaseStats = new DiagnosisStatsVO(patient.getCorrectDiseaseOptions().get(0), null);
      } else if (patient.getIncorrectMedicines().contains(conversation.getDiagnosedDisease())) {
        diseaseStats =
            new DiagnosisStatsVO(
                conversation.getDiagnosedDisease(), conversation.getDiagnosedDisease());
      } else {
        diseaseStats =
            new DiagnosisStatsVO(
                patient.getCorrectDiseaseOptions().get(0), conversation.getDiagnosedDisease());
      }
    }

    DiagnosisStatsVO medicineStatsVO = null;
    if (CollectionUtils.isNotEmpty(patient.getCorrectMedicines())) {
      medicineStatsVO = new DiagnosisStatsVO(patient.getCorrectMedicines().get(0), null);
      if (CollectionUtils.isNotEmpty(conversation.getMedicines())) {
        for (String prescribedMedicine : conversation.getMedicines()) {
          if (patient.getCorrectMedicines().contains(prescribedMedicine)) {
            medicineStatsVO = new DiagnosisStatsVO(prescribedMedicine, prescribedMedicine);
            break;
          }
        }
        if (medicineStatsVO.getChosenOption() == null) {
          medicineStatsVO =
              new DiagnosisStatsVO(
                  patient.getCorrectMedicines().get(0), conversation.getMedicines().get(0));
        }
      }
    }

    return new ConversationStatsVO(
        conversationStats.getTotalChatCount(),
        conversationStats.getHitChatCount(),
        conversationStats.getMissedChatCount(),
        statsList,
        chestXRayStats,
        chestSoundStats,
        diseaseStats,
        medicineStatsVO);
  }

  private ConversationStatsVO(
      int totalCount,
      int hitCount,
      int missedCount,
      List<CategoryStatsVO> categoryStatsList,
      TestStatsVO chestXRayStats,
      TestStatsVO chestSoundStats,
      DiagnosisStatsVO diseaseStats,
      DiagnosisStatsVO medicineStats) {
    this.totalCount = totalCount;
    this.hitCount = hitCount;
    this.missedCount = missedCount;
    this.categoryStatsList = categoryStatsList;
    this.diagnosticTestStatsList.add(chestXRayStats);
    this.physicalTestStatsList.add(chestSoundStats);
    this.diseaseStats = diseaseStats;
    this.medicineStats = medicineStats;
  }
}
