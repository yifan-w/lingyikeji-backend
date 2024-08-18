package com.lingyikeji.backend.application;

import com.lingyikeji.backend.application.vo.ChatAnswerVO;
import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Department;
import com.lingyikeji.backend.domain.entities.Disease;
import com.lingyikeji.backend.domain.entities.Message;
import com.lingyikeji.backend.domain.entities.Patient;
import com.lingyikeji.backend.domain.entities.PatientQA;
import com.lingyikeji.backend.domain.entities.UserHashedPwd;
import com.lingyikeji.backend.domain.repo.ConversationRepo;
import com.lingyikeji.backend.domain.repo.DepartmentRepo;
import com.lingyikeji.backend.domain.repo.DiseaseRepo;
import com.lingyikeji.backend.domain.repo.PatientRepo;
import com.lingyikeji.backend.domain.repo.UserAuthRepo;
import com.lingyikeji.backend.infra.gateway.LLMService;
import com.lingyikeji.backend.utils.GsonUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Created by Yifan Wang on 2023/10/1. */
@Service
public class MainApplicationService {
  private static final Logger logger = LogManager.getLogger(MainApplicationService.class);
  private final DiseaseRepo diseaseRepo;
  private final ConversationRepo conversationRepo;
  private final LLMService llmService;
  private final UserAuthRepo userAuthRepo;
  private final DepartmentRepo departmentRepo;
  private final PatientRepo patientRepo;

  public MainApplicationService(
      DiseaseRepo diseaseRepo,
      ConversationRepo conversationRepo,
      @Qualifier("cozeLLMService") LLMService llmService,
      UserAuthRepo userAuthRepo,
      DepartmentRepo departmentRepo,
      PatientRepo patientRepo) {
    this.diseaseRepo = diseaseRepo;
    this.conversationRepo = conversationRepo;
    this.llmService = llmService;
    this.userAuthRepo = userAuthRepo;
    this.departmentRepo = departmentRepo;
    this.patientRepo = patientRepo;
  }

  public void patientAddVrData(String patientId) {
    Patient patient = patientRepo.findById(patientId).get();
    patient
        .getPatientQAList()
        .forEach(
            patientQA -> {
              String prefix =
                  patientQA
                      .getVideoUrl()
                      .substring(
                          patientQA.getVideoUrl().lastIndexOf("/") + 1,
                          patientQA.getVideoUrl().lastIndexOf("."));
              patientQA.setVrJsonUrl(prefix + ".json");
              patientQA.setVrWavUrl(prefix + ".wav");
            });
    patientRepo.save(patient);
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

  public String createDepartment(String name, List<String> subDeptIds, List<String> patientIds) {
    List<Department> subDepartments =
        CollectionUtils.isEmpty(subDeptIds)
            ? Collections.emptyList()
            : subDeptIds.stream().map(deptId -> departmentRepo.findById(deptId).get()).toList();
    List<Patient> patients =
        CollectionUtils.isEmpty(patientIds)
            ? Collections.emptyList()
            : patientIds.stream().map(patientId -> patientRepo.findById(patientId).get()).toList();
    return departmentRepo.save(Department.create(name, subDepartments, patients));
  }

  public List<Department> getAllDepartments(String deptId) {
    if (deptId == null) {
      return departmentRepo.findAll().stream()
          .filter(dept -> CollectionUtils.isNotEmpty(dept.getSubDepartments()))
          .toList();
    }

    Department department = departmentRepo.findById(deptId).get();
    return department.getSubDepartments();
  }

  public List<Patient> getAllPatients() {
    return patientRepo.findAll();
  }

  public String createDisease(String name, String desc) {
    return diseaseRepo.save(Disease.create(name, desc));
  }

  public String createConversation(String userName, String deptId, String patientId, String msg) {
    Department department = departmentRepo.findById(deptId).get();
    Patient patient = patientRepo.findById(patientId).get();
    List<Message> msgList =
        StringUtils.isEmpty(msg) ? Collections.emptyList() : List.of(Message.fromUser(msg));
    return conversationRepo.save(Conversation.create(userName, department, patient, msgList));
  }

  public Disease getDisease(String id) {
    return diseaseRepo.findById(id).orElseThrow();
  }

  public Conversation getConversation(String id) {
    return conversationRepo.findById(id).orElseThrow();
  }

  public String createPatient(String patientJson) {
    Patient patient = GsonUtils.GSON.fromJson(patientJson, Patient.class);
    return patientRepo.save(patient);
  }

  public ChatAnswerVO chat(String conversationId, String question) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    Patient patient = conversation.getPatient();
    logger.info("patient id for conversation {}: {}", conversationId, patient.getId());
    String prompt =
        GsonUtils.GSON.toJson(patient.getPatientQAList())
            + "以上是一个json对象数组，记录了一个医生和一个病人的对话，每一个对象的q属性代表一个问题，a属性代表一个回复。接下来我会作为医生提一个问题，请你作为病人考虑从json对象数组中选择一个与我提的问题最为相似的问题并回答我对应的回复，回答内容不要包含问题本身，回答末尾不要自行添加标点符号。如果没有任何相似的问题，请回答“请按照问诊标准进行提问”。\n"
            + "问："
            + question;
    String answer = this.testLLM(prompt);

    conversation.addUserMsg(question);
    conversation.addPatientMsg(answer);
    conversationRepo.save(conversation);

    Optional<PatientQA> patientQAOptional =
        patient.getPatientQAList().stream()
            .filter(patientQA -> patientQA.getA().contains(answer))
            .findFirst();
    String video = patientQAOptional.map(PatientQA::getVideoUrl).orElse("");
    String vrJson = patientQAOptional.map(PatientQA::getVrJsonUrl).orElse("");
    String vrWav = patientQAOptional.map(PatientQA::getVrWavUrl).orElse("");
    return ChatAnswerVO.create(answer, video, vrJson, vrWav);
  }

  public String testLLM(String message) {
    return llmService.sendPrompt(message);
  }
}
