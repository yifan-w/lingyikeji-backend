package com.lingyikeji.backend.application;

import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Department;
import com.lingyikeji.backend.domain.entities.Disease;
import com.lingyikeji.backend.domain.entities.Message;
import com.lingyikeji.backend.domain.entities.UserHashedPwd;
import com.lingyikeji.backend.domain.repo.ConversationRepo;
import com.lingyikeji.backend.domain.repo.DepartmentRepo;
import com.lingyikeji.backend.domain.repo.DiseaseRepo;
import com.lingyikeji.backend.domain.repo.UserAuthRepo;
import com.lingyikeji.backend.infra.gateway.LLMService;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Created by Yifan Wang on 2023/10/1. */
@Service
public class MainApplicationService {
  private final DiseaseRepo diseaseRepo;
  private final ConversationRepo conversationRepo;
  private final LLMService llmService;
  private final UserAuthRepo userAuthRepo;
  private final DepartmentRepo departmentRepo;

  public MainApplicationService(
      DiseaseRepo diseaseRepo,
      ConversationRepo conversationRepo,
      @Qualifier("openAILLMService") LLMService llmService,
      UserAuthRepo userAuthRepo,
      DepartmentRepo departmentRepo) {
    this.diseaseRepo = diseaseRepo;
    this.conversationRepo = conversationRepo;
    this.llmService = llmService;
    this.userAuthRepo = userAuthRepo;
    this.departmentRepo = departmentRepo;
  }

  public boolean createUserAuth(String userName, String pwd) {
    return userAuthRepo.save(UserHashedPwd.create(userName, pwd));
  }

  public boolean authUser(String userName, String pwd) {
    Optional<UserHashedPwd> userHashedPwdOptional =
        userAuthRepo.findUserHashedPwdByUserName(userName);
    if (userHashedPwdOptional.isEmpty()) {
      return false;
    }

    UserHashedPwd inputUserHashedPwd = UserHashedPwd.create(userName, pwd);
    return StringUtils.equals(
        userHashedPwdOptional.get().getHashedPwd(), inputUserHashedPwd.getHashedPwd());
  }

  public String createDepartment(String name) {
    return departmentRepo.save(Department.create(name, new LinkedList<>()));
  }

  public List<Department> getAllDepartments() {
    return departmentRepo.findAll();
  }

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

  public String testLLM(String message) {
    return llmService.sendPrompt(message);
  }
}
