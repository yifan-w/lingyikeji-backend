package com.lingyikeji.backend.facade;

import com.lingyikeji.backend.application.MainApplicationService;
import com.lingyikeji.backend.application.vo.Resp;
import com.lingyikeji.backend.utils.HashUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Created by Yifan Wang on 2023/10/16. */
@CrossOrigin("*")
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

  @PostMapping("/authUser")
  public Resp authUser(String userName, String pwd, HttpServletResponse response) {
    boolean authResult = applicationService.authUser(userName, pwd);
    if (authResult) {
      Cookie cookie = new Cookie("token", HashUtils.doHash(userName));
      cookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(cookie);
    }
    return Resp.success(Map.of("result", authResult));
  }

  @PostMapping("/createDept")
  public Resp createDept(String name) {
    return Resp.success(Map.of("id", applicationService.createDepartment(name)));
  }

  @GetMapping("/getAllDepts")
  public Resp getAllDept() {
    return Resp.success(Map.of("depts", applicationService.getAllDepartments()));
  }

  @PostMapping("/createDisease")
  public Resp createDisease(String name, String desc) {
    return Resp.success(Map.of("id", applicationService.createDisease(name, desc)));
  }

  @PostMapping("/createConversation")
  public Resp createConversation(String diseaseId) {
    return Resp.success(Map.of("id", applicationService.createConversation(diseaseId)));
  }

  @GetMapping("/getDisease")
  public Resp getDisease(String id) {
    return Resp.success(applicationService.getDisease(id));
  }

  @GetMapping("/getConversation")
  public Resp getConversation(String id) {
    return Resp.success(applicationService.getConversation(id));
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
    /*
    String prompt =
        "[{\"q\":\"您怎么不舒服？\",\"a\":\"我最近几天老在咳嗽，喉咙也疼的不行，还流鼻涕\"},{\"q\":\"注意到与咳嗽相关的其他症状了吗?\",\"a\":\"还有喉咙痛和流鼻涕\"},{\"q\":\"这种症状持续多久了？\",\"a\":\"流鼻涕一周\"},{\"q\":\"告诉我咳嗽开始的时间及咳嗽是怎样开始的\",\"a\":\"一个星期前受凉了就开始咳嗽了\"},{\"q\":\"咳嗽与气喘相关吗?\",\"a\":\"我没有气喘的症状啊\"},{\"q\":\"起病前有没有什么诱因？\",\"a\":\"一周前淋了雨，可能受凉了，之后就这样了\"},{\"q\":\"你感觉最近症状加重了吗？\",\"a\":\"差不多的，但是晚上咳嗽会严重些\"},{\"q\":\"什么情况下感觉症状加重了？\",\"a\":\"没有更严重\"},{\"q\":\"起病以后去医院看过吗？\",\"a\":\"昨天去过社区医院\"},{\"q\":\"医生考虑诊断是什么？\",\"a\":\"医生说可能是感冒了\"},{\"q\":\"做过什么检查和治疗？\",\"a\":\"吃了一些阿莫西林\"},{\"q\":\"效果怎么样？\",\"a\":\"效果一般，所以又来你这边了\"},{\"q\":\"除了这些症状以外，是否还伴有其它异常？\",\"a\":\"就是咳嗽和喉咙痛，还有些鼻涕，别的还好，没发烧、头也不疼\"},{\"q\":\"精神、饮食、睡眠怎么样？\",\"a\":\"都没有受影响，挺好的\"},{\"q\":\"什么时候开始出现这种症状的？\",\"a\":\"差不多一个星期之前开始的\"},{\"q\":\"现在情况怎么样？\",\"a\":\"现在还是差不多的情况\"},{\"q\":\"每次持续多长时间？\",\"a\":\"最近一个星期都有这些不舒服\"},{\"q\":\"你感觉最近症状减轻了吗？\",\"a\":\"没有减轻\"},{\"q\":\"症状发作有什么规律吗，比如与气候、环境、情绪、体位变化、咳嗽，或进食、呼吸、运动、劳累等因素有关吗？\",\"a\":\"我感觉没有什么规律\"},{\"q\":\"发病后大小便有没有变化？\",\"a\":\"大小便都还好，没有什么变化\"},{\"q\":\"起病以后，体重、体力怎么样？\",\"a\":\"体重没有变化，体力挺好的\"},{\"q\":\"咽痛的程度如何？\",\"a\":\"挺严重的，我说话都痛\"},{\"q\":\"是否伴有异物感？\",\"a\":\"没有这种感觉\"},{\"q\":\"具体是什么样的疼痛？\",\"a\":\"咳嗽的时候感觉喉咙里撕裂的痛\"},{\"q\":\"吞咽东西的时候疼痛会更严重吗？\",\"a\":\"会更痛一些\"},{\"q\":\"发烧了吗？\",\"a\":\"没有发烧\"},{\"q\":\"疼痛有没有扯到其他部位？\",\"a\":\"没有\"},{\"q\":\"呼吸或者咽东西的时候觉得困难吗？\",\"a\":\"还好，对呼吸影响\"},{\"q\":\"鼻涕是什么颜色的？\",\"a\":\"就是清鼻涕\"},{\"q\":\"除了流鼻涕，还有没有打喷嚏、鼻塞？\",\"a\":\"没有\"},{\"q\":\"除了流鼻涕，喉咙痛不痛？\",\"a\":\"痛啊，说话都有点痛，尤其咳嗽的时候特别痛\"},{\"q\":\"过去身体状况怎么样？\",\"a\":\"身体还可以\"},{\"q\":\"以前患过什么慢性病吗？比如高血压、糖尿病、心脏病等\",\"a\":\"以前什么病都没有\"},{\"q\":\"以前患过什么传染病吗？比如病毒性肝炎、结核病、血吸虫病、伤寒、痢疾等\",\"a\":\"没有得过传染病\"},{\"q\":\"以前有没有做过什么手术？\",\"a\":\"没有做过手术\"},{\"q\":\"以前有没有受过外伤？\",\"a\":\"没有受过外伤\"},{\"q\":\"是否按国家要求接种了疫苗？\",\"a\":\"是的，疫苗都接种了\"},{\"q\":\"有没有输过血？\",\"a\":\"没有输过血\"},{\"q\":\"有没有对什么食物或者药物过敏的？\",\"a\":\"对芒果过敏，药物过敏没有发现\"},{\"q\":\"当时有哪些过敏症状，是否出现皮疹或者呼吸困难？\",\"a\":\"皮肤发红肿起来，还有疹子，没有呼吸困难\"},{\"q\":\"您有抽烟的嗜好吗?\",\"a\":\"我抽烟\"},{\"q\":\"您有饮酒的嗜好吗?\",\"a\":\"我不喝酒\"},{\"q\":\"抽烟多长时间了？\",\"a\":\"20年了\"},{\"q\":\"抽烟每天的量是多少？\",\"a\":\"一天半包吧\"},{\"q\":\"家属身体健康状况怎么样？\",\"a\":\"家里人身体都还可以\"},{\"q\":\"家里人有得过肿瘤或者精神方面的疾病吗？\",\"a\":\"没有呢\"},{\"q\":\"您什么时候结婚的？\",\"a\":\"我适龄结婚的\"},{\"q\":\"结婚多少年了？\",\"a\":\"结婚三十多年了\"},{\"q\":\"您爱人身体健康状况怎么样？\",\"a\":\"身体挺好的\"}]"
            + "以上是一个json对象数组，每一个对象的q属性代表一个问题，a属性代表一个回复。接下来我会提一个问题，请你从json对象数组中选择一个与我提的问题最为相似的问题并回答我对应的回复，回答内容不要包含问题本身，回答末尾不要自行添加标点符号。如果没有任何相似的问题，请回答“请按照问诊标准进行提问”。\n"
            + "问："
            + question;
    return Resp.success(
        Map.of(
            "answer",
            applicationService.testLLM(prompt),
            "video",
            "https://www.pixelgeom.com/api/lingyi/getLivestream?id=y_demo"));
     */
    return Resp.success(applicationService.chat(conversationId, question));
  }
}
