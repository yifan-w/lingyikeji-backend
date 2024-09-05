package com.lingyikeji.backend.facade;

import com.lingyikeji.backend.application.MainApplicationService;
import com.lingyikeji.backend.application.vo.Resp;
import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.entities.Department;
import com.lingyikeji.backend.domain.entities.User;
import com.lingyikeji.backend.facade.vo.PatientVO;
import com.lingyikeji.backend.utils.GsonUtils;
import com.lingyikeji.backend.utils.HashUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Created by Yifan Wang on 2023/10/16. */
@RestController
@RequiredArgsConstructor
public class MainController {
  private static final Logger logger = LogManager.getLogger(MainController.class);
  private static final AtomicInteger counter = new AtomicInteger(1);
  private static final String STATIC_M3U8 =
      """
          #EXTM3U
          #EXT-X-PLAYLIST-TYPE:VOD
          #EXT-X-TARGETDURATION:8
          #EXT-X-MEDIA-SEQUENCE:0
          #EXTINF:8,
          https://www.pixelgeom.com/livestream/lingyi/{0}.ts
          #EXT-X-ENDLIST
          """;
  private final MainApplicationService applicationService;

  @PostMapping("/createUserAuth")
  public Resp createUserAuth(String userName, String pwd) {
    return Resp.success(Map.of("result", applicationService.createUserAuth(userName, pwd)));
  }

  @PostMapping("/deleteUserAuth")
  public Resp deleteUserAuth(@CookieValue("token") String token, String id) {
    if (this.isNotAuthAdmin(token)) {
      return Resp.success(Map.of("result", false));
    }
    applicationService.deleteUserAuth(id);
    return Resp.success(Map.of("result", true));
  }

  @GetMapping("/getAllUserAuth")
  public Resp getAllUserAuth(@CookieValue("token") String token) {
    if (this.isNotAuthAdmin(token)) {
      return Resp.success(Map.of("result", false));
    }
    return Resp.success(applicationService.getAllUserAuth());
  }

  @PostMapping("/authUser")
  public Resp authUser(String userName, String pwd, HttpServletResponse response) {
    boolean authResult = applicationService.authUser(userName, pwd);
    if (authResult) {
      Cookie tokenCookie = new Cookie("token", HashUtils.doHash(userName));
      tokenCookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(tokenCookie);
      Cookie userNameCookie = new Cookie("userName", userName);
      userNameCookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(userNameCookie);
    }
    return Resp.success(Map.of("result", authResult));
  }

  @PostMapping("/createDept")
  public Resp createDept(String name, String subDeptIds, String patientIds) {
    List<String> subDeptIdList =
        StringUtils.isEmpty(subDeptIds)
            ? Collections.emptyList()
            : Arrays.stream(subDeptIds.split(",")).toList();
    List<String> patientIdList =
        StringUtils.isEmpty(patientIds)
            ? Collections.emptyList()
            : Arrays.stream(patientIds.split(",")).toList();
    return Resp.success(
        Map.of("id", applicationService.createDepartment(name, subDeptIdList, patientIdList)));
  }

  @GetMapping("/getAllDepts")
  public Resp getAllDept(String deptId) {
    return Resp.success(
        Map.of(
            "depts",
            applicationService.getAllDepartments(deptId).stream()
                .map(dept -> GsonUtils.GSON.fromJson(GsonUtils.GSON.toJson(dept), Department.class))
                .toList()));
  }

  @PostMapping("/createDisease")
  public Resp createDisease(String name, String desc) {
    return Resp.success(Map.of("id", applicationService.createDisease(name, desc)));
  }

  @PostMapping("/createConversation")
  public Resp createConversation(
      @CookieValue("userName") String userName, String deptId, String patientId, String msg) {
    return Resp.success(
        Map.of("id", applicationService.createConversation(userName, deptId, patientId, msg)));
  }

  @PostMapping("/createConversationV0")
  public Resp createConversationV0(
      @CookieValue("userName") String userName, String patientId, String msg) {
    String deptId = "66b8719ecfe54b4ce9c69074";
    return Resp.success(
        Map.of("id", applicationService.createConversation(userName, deptId, patientId, msg)));
  }

  @GetMapping("/getAllPatientsV0")
  public Resp getAllPatientsV0(String id) {
    return Resp.success(
        applicationService.getAllPatients().stream().map(PatientVO::fromPatient).toList());
  }

  @GetMapping("/getDisease")
  public Resp getDisease(String id) {
    return Resp.success(applicationService.getDisease(id));
  }

  @GetMapping("/getConversation")
  public Resp getConversation(String id) {
    return Resp.success(
        GsonUtils.GSON.fromJson(
            GsonUtils.GSON.toJson(applicationService.getConversation(id)), Conversation.class));
  }

  @PostMapping("/createPatient")
  public Resp createPatient(String patientJson) {
    return Resp.success(Map.of("id", applicationService.createPatient(patientJson)));
  }

  @GetMapping("/getLivestream")
  public ResponseEntity<Resource> getLivestream(String id) {
    String m3u8Content = MessageFormat.format(STATIC_M3U8, id);
    try {
      return ResponseEntity.ok()
          .contentLength(m3u8Content.getBytes("UTF-8").length)
          .contentType(MediaType.parseMediaType("application/x-mpegURL"))
          // .header("Content-Disposition", "attachment; filename=demo.m3u8")
          .body(
              new InputStreamResource(
                  new ByteArrayInputStream(m3u8Content.getBytes(StandardCharsets.UTF_8))));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/testLLM")
  public Resp testLLM(String message) {
    return Resp.success(Map.of("msg", applicationService.testLLM(message)));
  }

  @PostMapping("/testQuestion")
  public Resp testQuestion(String question) {
    String template =
        "### 病历\\n\\n#### 基本信息\\n- **姓名**：王小明\\n- **性别**：男\\n- **年龄**：32岁\\n- **职业**：软件工程师\\n- **婚姻状况**：已婚\\n- **住址**：北京市朝阳区\\n\\n#### 主诉\\n患者因鼻塞、流涕、咽喉痛、低热3天前来就诊。\\n\\n#### 现病史\\n患者3天前出现鼻塞、流清水样鼻涕，伴有喷嚏、咽喉干痒和轻度灼热感。随后出现咽喉痛，吞咽时疼痛加剧，伴有低热（37.8℃），无明显畏寒。患者自述有轻度头痛和全身不适，无明显咳嗽或呼吸困难。患者无既往类似病史，未接触过有类似症状的人员。\\n\\n#### 既往史\\n- **疾病史**：无慢性病史，无重大疾病史。\\n- **手术史**：无。\\n- **过敏史**：无药物、食物过敏史。\\n\\n#### 家族史\\n家族中无遗传性疾病史，父母健康，无兄弟姐妹。\\n\\n#### 个人史\\n无吸烟史，偶尔饮酒，无其他不良嗜好。居住环境良好，近期无长途旅行史。\\n\\n#### 体格检查\\n- **体温**：37.8℃\\n- **脉搏**：82次/分\\n- **呼吸频率**：18次/分\\n- **血压**：120/80 mmHg\\n\\n- **鼻腔**：鼻腔黏膜充血、水肿，有清水样分泌物。\\n- **咽喉**：咽部充血明显，扁桃体轻度肿大，无脓性分泌物。\\n- **颌下淋巴结**：肿大且触痛。\\n- **肺部**：无明显异常体征，未闻及干湿啰音及喘鸣音。\\n\\n#### 辅助检查\\n- **外周血常规**：\\n  - 白细胞计数：8.2 x 10^9/L（正常范围）\\n  - 淋巴细胞比例：45%（正常范围）\\n  - 中性粒细胞比例：50%（正常范围）\\n  - 血红蛋白：140 g/L（正常范围）\\n  - 血小板：220 x 10^9/L（正常范围）\\n\\n- **X线胸片**：未见明显异常。\\n\\n- **病原学检查**：未进行。\\n\\n#### 诊断\\n- **初步诊断**：急性上呼吸道感染（普通感冒）\\n\\n#### 处理及建议\\n1. **一般治疗**：\\n   - 休息，避免劳累。\\n   - 保持室内空气湿润，避免受凉。\\n\\n2. **药物治疗**：\\n   - 退热药：对乙酰氨基酚，每次500mg，每4-6小时一次，必要时服用。\\n   - 抗病毒药物：可考虑使用奥司他韦，每次75mg，每日两次，连续5天。\\n\\n3. **饮食建议**：\\n   - 多饮水，保持水分摄入。\\n   - 进食清淡易消化食物，避免辛辣刺激性食物。\\n\\n4. **复查**：\\n   - 如症状未见好转或出现高热、呼吸困难等情况，建议及时复诊。\\n\\n5. **预防措施**：\\n   - 注意个人卫生，勤洗手，避免接触传染源。\\n   - 增强体质，适当锻炼，避免过度疲劳。\\n\\n#### 随访计划\\n一周后电话随访，了解病情进展及治疗效果，如有异常及时调整治疗方案。\n"
            + "请根据该病人的病历，进行模拟真实就诊，我作为医生进行问诊，你只需回答该问题，不用称呼医生开头。\n"
            + "问：";
    String prompt = template + question;
    logger.info("Prompt: {}", prompt);
    return Resp.success(Map.of("answer", applicationService.testLLM(prompt)));
  }

  @PostMapping("/chat")
  public Resp chat(String conversationId, String question) {
    return Resp.success(applicationService.chat(conversationId, question));
  }

  @PostMapping("/patientAddVrData")
  public Resp patientAddVrData(String patientId) {
    applicationService.patientAddVrData(patientId);
    return Resp.success(true);
  }

  @PostMapping("/feedback")
  public Resp feedback(
      String conversationId, int score, String message, HttpServletRequest request) {
    User user = (User) request.getSession().getAttribute("user");
    applicationService.feedback(user, conversationId, score, message);
    return Resp.success(true);
  }

  private boolean isNotAuthAdmin(String token) {
    return !Objects.equals(token, "9js52LsQxPyA078CqasXdtmJQTCCwe5wVDrUo3xEgB8=");
  }
}
