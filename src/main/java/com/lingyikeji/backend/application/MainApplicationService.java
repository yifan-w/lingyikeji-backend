package com.lingyikeji.backend.application;

import com.google.gson.JsonParser;
import com.lingyikeji.backend.application.vo.ChatAnswerVO;
import com.lingyikeji.backend.application.vo.ConversationStatsVO;
import com.lingyikeji.backend.application.vo.ConversationVO;
import com.lingyikeji.backend.application.vo.DiseaseOptionVO;
import com.lingyikeji.backend.application.vo.UserVO;
import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Department;
import com.lingyikeji.backend.domain.entities.Disease;
import com.lingyikeji.backend.domain.entities.Feedback;
import com.lingyikeji.backend.domain.entities.Message;
import com.lingyikeji.backend.domain.entities.Patient;
import com.lingyikeji.backend.domain.entities.PatientQA;
import com.lingyikeji.backend.domain.entities.User;
import com.lingyikeji.backend.domain.repo.ConversationRepo;
import com.lingyikeji.backend.domain.repo.DepartmentRepo;
import com.lingyikeji.backend.domain.repo.DiseaseRepo;
import com.lingyikeji.backend.domain.repo.FeedbackRepo;
import com.lingyikeji.backend.domain.repo.PatientRepo;
import com.lingyikeji.backend.domain.repo.UserAuthRepo;
import com.lingyikeji.backend.infra.gateway.HttpService;
import com.lingyikeji.backend.infra.gateway.LLMService;
import com.lingyikeji.backend.infra.gateway.dto.TTSDTO;
import com.lingyikeji.backend.utils.GsonUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
  private final HttpService httpService;
  private final LLMService llmService;
  private final UserAuthRepo userAuthRepo;
  private final DepartmentRepo departmentRepo;
  private final PatientRepo patientRepo;
  private final FeedbackRepo feedbackRepo;

  public MainApplicationService(
      DiseaseRepo diseaseRepo,
      ConversationRepo conversationRepo,
      HttpService httpService,
      @Qualifier("openAILLMService") LLMService llmService,
      UserAuthRepo userAuthRepo,
      DepartmentRepo departmentRepo,
      PatientRepo patientRepo,
      FeedbackRepo feedbackRepo) {
    this.diseaseRepo = diseaseRepo;
    this.conversationRepo = conversationRepo;
    this.httpService = httpService;
    this.llmService = llmService;
    this.userAuthRepo = userAuthRepo;
    this.departmentRepo = departmentRepo;
    this.patientRepo = patientRepo;
    this.feedbackRepo = feedbackRepo;
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

  public boolean createUserAuth(String userName, String pwd, String userType) {
    return userAuthRepo.save(User.create(userName, pwd, userType));
  }

  public void deleteUserAuth(String id) {
    userAuthRepo.deleteById(id);
  }

  public Optional<User> getUserByUserName(String userName) {
    return userAuthRepo.findUserHashedPwdByUserName(userName);
  }

  public List<User> getAllUserAuth() {
    return userAuthRepo.findAll();
  }

  public Optional<UserVO> authUser(String userName, String pwd) {
    return userAuthRepo
        .findUserHashedPwdByUserName(userName)
        .filter(user -> user.testPwd(pwd))
        .map(UserVO::fromUser);
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

  public boolean exceedsConversationLimit(User user, int limit) {
    return false;
    /*
    if (user.isTest()) {
      return false;
    }
    return conversationRepo.findByUserName(user.getUserName()).stream()
        .anyMatch(conversation -> CollectionUtils.size(conversation.getMsgList()) < limit);

     */
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

  public ConversationVO getConversation(String id) {
    return conversationRepo.findById(id).map(ConversationVO::fromConversation).orElseThrow();
  }

  public List<ConversationVO> getConversationsByUserName(User user) {
    return conversationRepo.findByUserName(user.getUserName()).stream()
        .map(ConversationVO::fromConversation)
        .toList();
  }

  public String createPatient(String patientJson) {
    Patient patient = GsonUtils.GSON.fromJson(patientJson, Patient.class);
    return patientRepo.save(patient);
  }

  public ChatAnswerVO chat(String conversationId, String question) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    Patient patient = conversation.getPatient();
    logger.info("patient id for conversation {}: {}", conversationId, patient.getId());

    String genAudio =
        StringUtils.isNotEmpty(conversation.getNoMatchAudioUrl())
            ? conversation.getNoMatchAudioUrl()
            : (patient.getSex() == Patient.Sex.MALE
                ? "https://www.pixelgeom.com/livestream/lingyi/tmp/default_male.mp3"
                : "https://www.pixelgeom.com/livestream/lingyi/tmp/default_female.mp3");

    // test results
    if (question.contains("胸片") || question.contains("听诊")) {
      conversation.addUserMsg(question);
      conversationRepo.save(conversation);

      if (MapUtils.isEmpty(patient.getTestResults())) {
        return ChatAnswerVO.create("请询问病情相关信息", patient.getSilentVideoUrl(), null, null, genAudio);
      }

      return question.contains("胸片")
          ? ChatAnswerVO.createExamResults(
              "chestXrayImg", patient.getTestResults().get("chestXrayImg"))
          : ChatAnswerVO.createExamResults(
              "chestSoundAudio", patient.getTestResults().get("chestSoundAudio"));
    }
    String answer = llmService.askPatientQuestion(patient, question);

    conversation.addUserMsg(question);
    conversation.addPatientMsg(answer);
    conversationRepo.save(conversation);

    Optional<PatientQA> patientQAOptional =
        patient.getPatientQAList().stream()
            .filter(
                patientQA ->
                    patientQA.getA().contains(answer)
                        && Math.abs(patientQA.getA().length() - answer.length()) < 3)
            .findFirst();

    if (patientQAOptional.isPresent()) {
      PatientQA patientQA = patientQAOptional.get();
      return ChatAnswerVO.create(
          answer, patientQA.getVideoUrl(), patientQA.getVrJsonUrl(), patientQA.getVrWavUrl(), null);
    }

    // VR Patient
    if (Objects.equals(patient.getId(), "66b4d61f1f01cc37dcc02327")) {
      return ChatAnswerVO.create(
          "请询问病情相关信息",
          patient.getSilentVideoUrl(),
          "https://www.pixelgeom.com/livestream/lingyi/vr/json/LZH_BL1_default.json",
          "https://www.pixelgeom.com/livestream/lingyi/vr/wav/LZH_BL1_default.wav",
          "https://www.pixelgeom.com/livestream/lingyi/tmp/default_female.mp3");
    }

    if (StringUtils.isNotEmpty(patient.getDesc())) {
      String prompt =
          "#角色\n"
              + "你是一位专业的医疗领域标准问诊病人，下面提供了你的“原始病历”和“当前医生提问”，请理解你自己的“原始病历”，并遵循“任务”逻辑，合理回复“当前医生提问”\n"
              + "#原始病历\n"
              + patient.getDesc()
              + "\n#任务\n"
              + "首先理解你自己的“原始病历”，深刻熟悉自己的基本信息和病情等病历信息；然后针对“当前医生提问”，从“原始病历”中寻找相关信息，若在“原始病历”中能找到“当前医生提问”的相关信息，那么一步步思考做出合理的回复。"
              + "若是找不到相关信息，那么进一步判断医生的问题如果属于和人的身体相关、症状、主诉、既往病史、就诊经历、家庭情况、生活环境、家庭病史等合理医学范畴则如实回答没有对应的问题。例如“原始病历”并不包括“嗓子不疼”，但医生询问“嗓子疼不疼”，则回复“不疼”；或者”原始病历“并不包括”没有掉头发“，但医生询问”最近有没有掉头发“，则回复”没有掉头发“。"
              + "若医生提问超出和人的身体相关、症状、主诉、既往病史、就诊经历、家庭情况、生活环境、家庭病史等范畴，请回复“请询问病情相关信息”。\n"
              + "切记要么输出病人回复内容，要么输出请询问病情相关信息，不要输出其他任何无关内容。\n"
              + "#当前医生提问\n"
              + question
              + "\n#病人回复";

      String generatedAnswer = llmService.sendPrompt(prompt);

      if (!generatedAnswer.contains("请询问病情相关信息")) {
        String ttsId = conversationId + "-" + System.currentTimeMillis();
        TTSDTO ttsdto = new TTSDTO(ttsId, generatedAnswer, patient);
        logger.info("ttsdto: {}", GsonUtils.GSON.toJson(ttsdto));
        String resp =
            httpService.doPost(
                "https://openspeech.bytedance.com/api/v1/tts",
                Map.of("Authorization", "Bearer;m1REPwhn__BL6EIM96iXqbmnTzSxnYTU"),
                ttsdto);
        String base64String =
            JsonParser.parseString(resp).getAsJsonObject().get("data").getAsString();
        String fileName = ttsId + ".mp3";

        String filePath = // "/Users/wangyifan/Downloads/" + fileName;
            "/usr/share/nginx/livestream/tmp/" + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
          byte[] decoder = Base64.getDecoder().decode(base64String);
          fos.write(decoder);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        genAudio = "https://www.pixelgeom.com/livestream/lingyi/tmp/" + fileName;
      }

      return ChatAnswerVO.create(
          generatedAnswer, patient.getSilentVideoUrl(), null, null, genAudio);
    }

    return ChatAnswerVO.create(answer, patient.getSilentVideoUrl(), null, null, genAudio);
  }

  public String testLLM(String message) {
    return llmService.sendPrompt(message);
  }

  public String feedback(User user, String conversationId, int score, String message) {
    logger.info(
        "feedback userName: {}, conversationId: {}, score: {}, message: {}",
        user.getUserName(),
        conversationId,
        score,
        message);
    Conversation conversation =
        StringUtils.isEmpty(conversationId)
            ? null
            : conversationRepo.findById(conversationId).get();
    return feedbackRepo.save(Feedback.create(user.getUserName(), score, conversation, message));
  }

  public boolean updatePatient(String id, String patientJson) {
    Optional<Patient> patientOptional = patientRepo.findById(id);
    if (patientOptional.isEmpty()) {
      return false;
    }

    Patient patient = patientOptional.get();
    Patient inputPatient = GsonUtils.GSON.fromJson(patientJson, Patient.class);
    patient.updateWith(inputPatient);
    patientRepo.save(patient);
    return true;
  }

  public ConversationStatsVO getConversationStats(String conversationId) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    return ConversationStatsVO.fromConversationStats(conversation.generateStats());
  }

  public MedicineVO getMedicines(String conversationId) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    return new MedicineVO(
        conversation.getPatient().getCorrectMedicines(),
        conversation.getPatient().getIncorrectMedicines());
  }

  public void prescribeMedicines(String conversationId, List<String> medicines) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    conversation.setMedicines(medicines);
    conversationRepo.save(conversation);
  }

  public DiseaseOptionVO getConversationDiseaseOptions(String conversationId) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    return new DiseaseOptionVO(
        conversation.getPatient().getCorrectDiseaseOptions(),
        conversation.getPatient().getIncorrectDiseaseOptions());
  }

  public void diagnoseDisease(String conversationId, String disease) {
    Conversation conversation = conversationRepo.findById(conversationId).get();
    conversation.setDiagnosedDisease(disease);
    conversationRepo.save(conversation);
  }
}
