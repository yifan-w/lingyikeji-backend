package com.lingyikeji.backend.application;

import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Disease;
import com.lingyikeji.backend.domain.entities.Message;
import com.lingyikeji.backend.domain.repo.ConversationRepo;
import com.lingyikeji.backend.domain.repo.DiseaseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Created by Yifan Wang on 2023/10/1. */
@Service
@RequiredArgsConstructor
public class MainApplicationService {
  private final DiseaseRepo diseaseRepo;
  private final ConversationRepo conversationRepo;

  public String createDisease(String name, String desc) {
    return diseaseRepo.save(Disease.create(name, desc));
  }

  public String createConversation(String diseaseId) {
    Disease disease = diseaseRepo.findById(diseaseId).orElseThrow();
    return conversationRepo.save(Conversation.create(disease, Message.fromPatient("医生我不舒服")));
  }

  public Disease getDisease(String id) {
    return diseaseRepo.findById(id).orElseThrow();
  }

  public Conversation getConversation(String id) {
    return conversationRepo.findById(id).orElseThrow();
  }
}
