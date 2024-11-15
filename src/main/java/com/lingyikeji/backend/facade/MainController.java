package com.lingyikeji.backend.facade;

import com.lingyikeji.backend.application.MainApplicationService;
import com.lingyikeji.backend.application.vo.Resp;
import com.lingyikeji.backend.application.vo.UserVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  @GetMapping(value = "/mockGetAllDepartments", produces = "application/json")
  public String mockGetAllDepartments(HttpServletResponse response) {
    return "[{\"id\":\"1\",\"parentId\":\"0\",\"name\":\"内科\",\"children\":[{\"id\":\"10\",\"parentId\":\"1\",\"name\":\"心血管内科\",\"children\":[]},{\"id\":\"11\",\"parentId\":\"1\",\"name\":\"呼吸内科\",\"children\":[]},{\"id\":\"12\",\"parentId\":\"1\",\"name\":\"消化内科\",\"children\":[]},{\"id\":\"13\",\"parentId\":\"1\",\"name\":\"肾内科\",\"children\":[]},{\"id\":\"14\",\"parentId\":\"1\",\"name\":\"血液内科\",\"children\":[]},{\"id\":\"15\",\"parentId\":\"1\",\"name\":\"内分泌科\",\"children\":[]},{\"id\":\"16\",\"parentId\":\"1\",\"name\":\"神经内科\",\"children\":[]},{\"id\":\"17\",\"parentId\":\"1\",\"name\":\"风湿免疫科\",\"children\":[]},{\"id\":\"18\",\"parentId\":\"1\",\"name\":\"感染科\",\"children\":[]}]},{\"id\":\"2\",\"parentId\":\"0\",\"name\":\"外科\",\"children\":[{\"id\":\"19\",\"parentId\":\"2\",\"name\":\"普通外科\",\"children\":[]},{\"id\":\"20\",\"parentId\":\"2\",\"name\":\"神经外科\",\"children\":[]},{\"id\":\"21\",\"parentId\":\"2\",\"name\":\"心胸外科\",\"children\":[]},{\"id\":\"22\",\"parentId\":\"2\",\"name\":\"泌尿外科\",\"children\":[]},{\"id\":\"23\",\"parentId\":\"2\",\"name\":\"甲乳外科\",\"children\":[]},{\"id\":\"24\",\"parentId\":\"2\",\"name\":\"肝胆胰外科\",\"children\":[]},{\"id\":\"25\",\"parentId\":\"2\",\"name\":\"胃肠外科\",\"children\":[]},{\"id\":\"26\",\"parentId\":\"2\",\"name\":\"骨外科\",\"children\":[]},{\"id\":\"27\",\"parentId\":\"2\",\"name\":\"肛肠外科\",\"children\":[]}]},{\"id\":\"3\",\"parentId\":\"0\",\"name\":\"妇产科\",\"children\":[{\"id\":\"28\",\"parentId\":\"3\",\"name\":\"妇科\",\"children\":[]},{\"id\":\"29\",\"parentId\":\"3\",\"name\":\"产科\",\"children\":[]}]},{\"id\":\"4\",\"parentId\":\"0\",\"name\":\"儿科\",\"children\":[{\"id\":\"30\",\"parentId\":\"4\",\"name\":\"小儿外科\",\"children\":[]},{\"id\":\"31\",\"parentId\":\"4\",\"name\":\"小儿内科\",\"children\":[]},{\"id\":\"32\",\"parentId\":\"4\",\"name\":\"新生儿科\",\"children\":[]}]},{\"id\":\"5\",\"parentId\":\"0\",\"name\":\"肿瘤科\",\"children\":[{\"id\":\"33\",\"parentId\":\"5\",\"name\":\"肿瘤内科\",\"children\":[]},{\"id\":\"34\",\"parentId\":\"5\",\"name\":\"肿瘤外科\",\"children\":[]},{\"id\":\"35\",\"parentId\":\"5\",\"name\":\"肿瘤妇科\",\"children\":[]},{\"id\":\"36\",\"parentId\":\"5\",\"name\":\"放射肿瘤科\",\"children\":[]}]},{\"id\":\"6\",\"parentId\":\"0\",\"name\":\"皮肤性病科\",\"children\":[{\"id\":\"37\",\"parentId\":\"6\",\"name\":\"皮肤科\",\"children\":[]},{\"id\":\"38\",\"parentId\":\"6\",\"name\":\"性病科\",\"children\":[]}]},{\"id\":\"7\",\"parentId\":\"0\",\"name\":\"五官科\",\"children\":[{\"id\":\"39\",\"parentId\":\"7\",\"name\":\"眼科\",\"children\":[]},{\"id\":\"40\",\"parentId\":\"7\",\"name\":\"耳鼻咽喉科\",\"children\":[]},{\"id\":\"44\",\"parentId\":\"7\",\"name\":\"口腔科\",\"children\":[]}]},{\"id\":\"8\",\"parentId\":\"0\",\"name\":\"急诊科\",\"children\":[{\"id\":\"41\",\"parentId\":\"8\",\"name\":\"急诊内科\",\"children\":[]},{\"id\":\"42\",\"parentId\":\"8\",\"name\":\"急诊外科\",\"children\":[]}]},{\"id\":\"9\",\"parentId\":\"0\",\"name\":\"全科\",\"children\":[{\"id\":\"43\",\"parentId\":\"9\",\"name\":\"全科医学科\",\"children\":[]}]}]";
  }

  @GetMapping(value = "/mockGetAllPatients", produces = "application/json")
  public String mockGetAllPatients(HttpServletResponse response) {
    return "{\"code\":\"200\",\"msg\":\"ok\",\"data\":[{\"patientId\":\"9992023027793\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴喘息9年，加重伴阵发性胸闷、气短1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027577\",\"name\":\"***\",\"age\":\"61岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽、咳痰14天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027260\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断性发热咳嗽、咳痰5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027139\",\"name\":\"***\",\"age\":\"92岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短、发热伴头晕、烧心、反酸7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027365\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴气短20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027411\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰，活动后气短30余年，加重伴发热、双下肢水肿6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027301\",\"name\":\"***\",\"age\":\"51岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027089\",\"name\":\"***\",\"age\":\"88岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热，周身乏力7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027479\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027151\",\"name\":\"***\",\"age\":\"86岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"恶心、呕吐、头晕伴咳嗽、气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027480\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，痰中带血伴间断发热10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027167\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰，痰中带血伴活动后气短1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027311\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发烧、咳嗽10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027573\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰，活动后气短3个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027010\",\"name\":\"***\",\"age\":\"74岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027467\",\"name\":\"***\",\"age\":\"82岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴间断发热20天，加重伴尿急、尿频、尿痛、排尿困难5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027186\",\"name\":\"***\",\"age\":\"75岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴平卧位憋气15天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027588\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发作性晕厥4天，咳嗽、咳痰1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027258\",\"name\":\"***\",\"age\":\"75岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、13天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027766\",\"name\":\"***\",\"age\":\"55岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷、气短1个月，加重6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027423\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"确诊食管鳞癌6个月，再次化疗入院。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027192\",\"name\":\"***\",\"age\":\"79岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰5-6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027138\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰，活动后气短20年，加重伴发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027144\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳黄痰20余年，加重伴喘息10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027435\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴喘息10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027691\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰20余年，加重伴喘息7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027278\",\"name\":\"***\",\"age\":\"77岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热12天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027802\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴喘息2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027188\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热15天，咳嗽、胸闷、恶心、呕吐3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027443\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热20天，头晕3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027575\",\"name\":\"***\",\"age\":\"37岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热5天，咯血3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027589\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、气短伴间断发热1个月，加重伴尿频、尿痛2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027525\",\"name\":\"***\",\"age\":\"31岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热半月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027830\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、呼吸困难伴腹胀、心悸4-5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027765\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"呼吸困难2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027537\",\"name\":\"***\",\"age\":\"46岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热7天，加重伴走路不稳3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027606\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴腹部不适、双侧腹股沟疼痛、走路不稳1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027314\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热，咳嗽、咳痰10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027774\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴活动后气短2月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027832\",\"name\":\"***\",\"age\":\"63岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"反复咳嗽、咳痰10余年，加重伴咯血4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027257\",\"name\":\"***\",\"age\":\"54岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽半月，吸气时右侧胸痛1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027591\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰20天，痰中带血、双下肢浮肿3天，发热伴喘息1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027825\",\"name\":\"***\",\"age\":\"72岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴喘息4-5年，加重伴阵发性胸闷、气短、呼吸困难4-5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027092\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027597\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷7年，加重5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027741\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027233\",\"name\":\"***\",\"age\":\"45岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴活动后气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027851\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴头晕、胸闷、腰痛15天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027268\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027441\",\"name\":\"***\",\"age\":\"104岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰，活动后气短20余年，加重7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027858\",\"name\":\"***\",\"age\":\"49岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"胸闷气短2月，加重伴周身乏力6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027333\",\"name\":\"***\",\"age\":\"74岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴活动后气短7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027389\",\"name\":\"***\",\"age\":\"78岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027840\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰2周。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027180\",\"name\":\"***\",\"age\":\"79岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027252\",\"name\":\"***\",\"age\":\"72岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰、胸痛1年，加重伴喘息20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027171\",\"name\":\"***\",\"age\":\"51岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、全身酸痛、咳嗽、咳痰10天，心慌气短5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027810\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热伴咳嗽、咳痰6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027903\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴喘息、痰中带血10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027795\",\"name\":\"***\",\"age\":\"42岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴头部不适及胸闷、气短1年。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027231\",\"name\":\"***\",\"age\":\"89岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰半月，阵发性胸闷喘息1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027066\",\"name\":\"***\",\"age\":\"51岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027468\",\"name\":\"***\",\"age\":\"57岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、胸闷、右手麻木10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027380\",\"name\":\"***\",\"age\":\"75岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热，伴乏力12天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027187\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027300\",\"name\":\"***\",\"age\":\"52岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027206\",\"name\":\"***\",\"age\":\"42岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰、发热、气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027005\",\"name\":\"***\",\"age\":\"83岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰，活动后气短40余年，加重伴发热2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027437\",\"name\":\"***\",\"age\":\"49岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴左侧胸痛3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027926\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"反复咳嗽、咳痰、咯血20余年，发热4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027868\",\"name\":\"***\",\"age\":\"49岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息、发热1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027185\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热伴咳嗽、咳痰2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027176\",\"name\":\"***\",\"age\":\"77岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、全身酸痛10天，尿频、尿痛、尿血、上腹部疼痛3天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027304\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴活动后气短、发热、不思饮食10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027897\",\"name\":\"***\",\"age\":\"88岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷、气短1年余，加重伴咳嗽、咳痰、发热、呼吸困难7天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027178\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027228\",\"name\":\"***\",\"age\":\"82岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰、喘息50年，加重伴夜间不能平卧5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027660\",\"name\":\"***\",\"age\":\"52岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断胸闷、气短20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027384\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027516\",\"name\":\"***\",\"age\":\"30岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热伴牙痛2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027340\",\"name\":\"***\",\"age\":\"53岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热伴胸闷胸痛10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027320\",\"name\":\"***\",\"age\":\"18岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"主因发热、咳嗽、咳痰15天，喘憋2天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027542\",\"name\":\"***\",\"age\":\"36岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴左侧胸痛10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027306\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽，咽干10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027899\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰伴喘息10余年，加重伴间断发热、心悸5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027169\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰10天，加重伴阵发性胸闷1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027564\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴气短、发热20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027211\",\"name\":\"***\",\"age\":\"78岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽、咳痰10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027865\",\"name\":\"***\",\"age\":\"36岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、腰痛4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027006\",\"name\":\"***\",\"age\":\"34岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴呼吸困难20余天，加重伴发热5-6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027491\",\"name\":\"***\",\"age\":\"47岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027019\",\"name\":\"***\",\"age\":\"78岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰5年，加重伴气短、发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027018\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴痰中带血，呼吸困难10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027455\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽、咳痰10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027197\",\"name\":\"***\",\"age\":\"82岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰12天，活动后气短胸闷2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027463\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热，咳嗽、咳痰7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027873\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短40年，加重伴阵发性胸闷、气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027229\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断恶心呕吐15天，咳嗽咳痰6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027395\",\"name\":\"***\",\"age\":\"81岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、伴胸闷4天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027570\",\"name\":\"***\",\"age\":\"43岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽、咳痰23天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027248\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、气短10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027848\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰30余年，活动后气短10余年，加重1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027478\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027560\",\"name\":\"***\",\"age\":\"81岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴烧心、反酸20天，再次发热1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027369\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、四肢乏力10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027859\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴咯血3个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027238\",\"name\":\"***\",\"age\":\"46岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、喘息、心悸20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027753\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴活动后气短2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027182\",\"name\":\"***\",\"age\":\"42岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热8天，气短3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027532\",\"name\":\"***\",\"age\":\"41岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、痰中带血伴发热20余天，加重5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027386\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027900\",\"name\":\"***\",\"age\":\"63岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热、喘息5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027183\",\"name\":\"***\",\"age\":\"63岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热伴咳嗽、咳痰9天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027303\",\"name\":\"***\",\"age\":\"55岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰、发热5天，左侧胸痛1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027902\",\"name\":\"***\",\"age\":\"81岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息10余天，间断发热3天，咳血1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027635\",\"name\":\"***\",\"age\":\"63岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短伴周身乏力3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027574\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027214\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰10天，活动后胸闷气短1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027239\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽11天，气短4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027811\",\"name\":\"***\",\"age\":\"58岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027202\",\"name\":\"***\",\"age\":\"54岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热11天，咳嗽咳痰8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027207\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳喘20年，加重伴咳嗽、咳痰10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027308\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴气短12天入院\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027557\",\"name\":\"***\",\"age\":\"57岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴气短、间断咯血1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027155\",\"name\":\"***\",\"age\":\"52岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热、气短9天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027466\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰10余天，加重伴发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027869\",\"name\":\"***\",\"age\":\"74岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，活动后气短3年，加重7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027448\",\"name\":\"***\",\"age\":\"78岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰8年，加重伴发热、喘息9天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027208\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热伴咳嗽咳痰6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027432\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽、咳痰10余天，胸闷、气短7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027277\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027882\",\"name\":\"***\",\"age\":\"51岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"头晕伴腹部不适、心悸、胸闷2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027075\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027153\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴头晕、烧心、反酸7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027593\",\"name\":\"***\",\"age\":\"39岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰1个月，加重伴胸闷、气短10余天、\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027056\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热、气短伴食欲减退10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027813\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴喘息5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027870\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰3年，加重伴气短、阵发性胸闷2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027861\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴胸闷、气短，左下肢麻木20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027505\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴胸闷、气短、心悸、头晕9天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027227\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027409\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷、气短7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027190\",\"name\":\"***\",\"age\":\"50岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"反复发热10余天伴咳嗽、胸闷、气短5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027323\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、周身乏力13天，咳嗽加重1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027555\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴发热、心悸、尿少20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027177\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咽痛5天，加重伴恶心2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027419\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热，间断咳嗽、咳痰、5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027265\",\"name\":\"***\",\"age\":\"72岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"胸闷、气短、咳嗽20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027595\",\"name\":\"***\",\"age\":\"31岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰10余天，加重伴发热1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027582\",\"name\":\"***\",\"age\":\"58岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴胸闷、气短1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027526\",\"name\":\"***\",\"age\":\"47岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后胸闷气短12天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027264\",\"name\":\"***\",\"age\":\"81岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽咳痰咽痛伴头晕10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027172\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咽痛，发热2天后伴持续性咳痰两周。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027230\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰8年，加重伴发热、气短7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027020\",\"name\":\"***\",\"age\":\"56岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、喘息3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027473\",\"name\":\"***\",\"age\":\"62岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、乏力20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027396\",\"name\":\"***\",\"age\":\"77岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰15天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027269\",\"name\":\"***\",\"age\":\"84岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，活动后气短，间断发热，恶心6天入院。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027112\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热、活动后气短10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027173\",\"name\":\"***\",\"age\":\"53岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027077\",\"name\":\"***\",\"age\":\"72岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热、气短3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027234\",\"name\":\"***\",\"age\":\"69岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、喘息1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027307\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027382\",\"name\":\"***\",\"age\":\"34岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴腹部不适20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027212\",\"name\":\"***\",\"age\":\"36岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、鼻塞4天伴乏力胸闷头皮发麻1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027383\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴活动后加重10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027507\",\"name\":\"***\",\"age\":\"58岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、喘息7天，伴发热3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027279\",\"name\":\"***\",\"age\":\"77岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断左侧胸痛1个月，发热、咳嗽、咳痰10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027769\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴呼吸困难1年余，加重伴双下肢浮肿7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027894\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断胸闷、心悸2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027611\",\"name\":\"***\",\"age\":\"41岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热1个月余。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027309\",\"name\":\"***\",\"age\":\"86岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027281\",\"name\":\"***\",\"age\":\"74岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热3天，加重伴咳嗽、咽痛乏力2天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027276\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热3天，咳嗽咳痰7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027146\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咽痛、咳嗽、咳痰5-6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027225\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027021\",\"name\":\"***\",\"age\":\"41岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027004\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"\\u0016间断咳嗽、咳痰伴喘息4-5年，加重半月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027746\",\"name\":\"***\",\"age\":\"45岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027867\",\"name\":\"***\",\"age\":\"52岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027887\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷、气短、头晕伴尿急、尿痛3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027014\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴呼吸困难20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027359\",\"name\":\"***\",\"age\":\"49岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、头痛9天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027841\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息2个月，加重2周。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027835\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰伴喘息20年，加重伴心悸、发热1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027866\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，胸闷、气短20天，加重伴心悸、头痛10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027193\",\"name\":\"***\",\"age\":\"84岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热10天，恶心、呕吐6小时。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027565\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，间断发热伴活动后气短1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027817\",\"name\":\"***\",\"age\":\"65岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴阵发性胸闷、气短、呼吸困难7-8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027145\",\"name\":\"***\",\"age\":\"61岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热8天，气短3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027520\",\"name\":\"***\",\"age\":\"84岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰，发热伴胸闷、气短20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027583\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热、气短、尿急、尿频、尿痛、恶心9天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027256\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰1周，胸闷气短3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027215\",\"name\":\"***\",\"age\":\"66岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰10天、乏力2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027224\",\"name\":\"***\",\"age\":\"82岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027390\",\"name\":\"***\",\"age\":\"72岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热6天，咳嗽咳痰2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027271\",\"name\":\"***\",\"age\":\"88岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027181\",\"name\":\"***\",\"age\":\"77岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热7天，咳嗽、咳痰3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027013\",\"name\":\"***\",\"age\":\"60岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热4天，咳嗽、咯血2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027908\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴胸闷、气短4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027898\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴心悸14天，喘息2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027027\",\"name\":\"***\",\"age\":\"52岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴发热8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027280\",\"name\":\"***\",\"age\":\"43岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热4天，咳嗽、咳痰8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027779\",\"name\":\"***\",\"age\":\"48岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027842\",\"name\":\"***\",\"age\":\"79岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴喘息10余天，加重伴发热1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027299\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热2天，咳嗽咳痰13天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027656\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"左测肢体麻木、乏力1天余。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027594\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、胸闷、气短1个月余。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027236\",\"name\":\"***\",\"age\":\"82岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热、咳嗽、咳痰10天，加重伴胸闷、气短5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027325\",\"name\":\"***\",\"age\":\"79岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热、咳嗽7天\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027199\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、喘息8天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027550\",\"name\":\"***\",\"age\":\"35岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热20余天，加重伴胸闷、心悸7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027906\",\"name\":\"***\",\"age\":\"57岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰7天，加重伴咯血2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027453\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰、喘息20余年，加重伴阵发性胸闷、气短、呼吸困难10天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027801\",\"name\":\"***\",\"age\":\"58岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽伴胸痛2个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027785\",\"name\":\"***\",\"age\":\"67岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性胸闷、气短10余年，加重伴头晕、无力、烧心2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027907\",\"name\":\"***\",\"age\":\"78岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10年，加重伴发热2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027315\",\"name\":\"***\",\"age\":\"74岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴周身乏力10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027872\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰，活动后气短6年，加重5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027584\",\"name\":\"***\",\"age\":\"76岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰伴胸闷、气短2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027529\",\"name\":\"***\",\"age\":\"71岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热14天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027418\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰10余年，加重伴心悸、头痛、腹部不适3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027521\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热，咳嗽、咳痰，活动后气短伴烧心、反酸20天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027500\",\"name\":\"***\",\"age\":\"59岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"阵发性心前区不适6个月，加重伴头晕3天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027549\",\"name\":\"***\",\"age\":\"80岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"劳累后胸闷气短5年，加重伴咳嗽、咳痰、呼吸困难4-5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027368\",\"name\":\"***\",\"age\":\"81岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热、四肢乏力10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027598\",\"name\":\"***\",\"age\":\"56岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、上腹部不适伴发热1个月。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027274\",\"name\":\"***\",\"age\":\"79岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、气短伴周身乏力6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027189\",\"name\":\"***\",\"age\":\"86岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"发热5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027198\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热6天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027393\",\"name\":\"***\",\"age\":\"56岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咽痒、咳嗽、咳痰20余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027221\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、胸闷气短7天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027357\",\"name\":\"***\",\"age\":\"83岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、发热11天伴喘息4天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027585\",\"name\":\"***\",\"age\":\"64岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、痰中带血40天，阵发性胸痛2天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027009\",\"name\":\"***\",\"age\":\"85岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰5年，活动后气短2年，加重伴发热5天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027392\",\"name\":\"***\",\"age\":\"92岁\",\"gender\":\"2\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断发热，咳嗽、咳痰6天，意识不清、喘息1天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027174\",\"name\":\"***\",\"age\":\"70岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰2周。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027243\",\"name\":\"***\",\"age\":\"68岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"咳嗽、咳痰、发热18天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027399\",\"name\":\"***\",\"age\":\"73岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"慢性咳嗽、咳痰40余年，活动后气短4年，加重伴10余天。\",\"deptName\":\"呼吸内科\"},{\"patientId\":\"9992023027388\",\"name\":\"***\",\"age\":\"87岁\",\"gender\":\"1\",\"departmentId\":\"11\",\"chiefComplaint\":\"间断咳嗽、咳痰伴发热3天。\",\"deptName\":\"呼吸内科\"}]}";
  }

  @PostMapping("/createUserAuth")
  public Resp createUserAuth(String userName, String pwd, String userType) {
    if (StringUtils.isEmpty(userType)) {
      return Resp.success(Map.of("result", false));
    }

    return Resp.success(
        Map.of("result", applicationService.createUserAuth(userName, pwd, userType)));
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
    Optional<UserVO> userOptional = applicationService.authUser(userName, pwd);
    boolean authResult = userOptional.isPresent();
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("result", authResult);
    if (authResult) {
      Cookie tokenCookie = new Cookie("token", HashUtils.doHash(userName));
      tokenCookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(tokenCookie);
      Cookie userNameCookie = new Cookie("userName", userName);
      userNameCookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(userNameCookie);

      resultMap.put("user", userOptional.get());
    }
    return Resp.success(resultMap);
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
      HttpServletRequest request, String deptId, String patientId, String msg) {
    User user = (User) request.getSession().getAttribute("user");
    if (applicationService.exceedsConversationLimit(user, 1)) {
      return Resp.error("您已超出可创建问诊次数上限");
    }

    return Resp.success(
        Map.of(
            "id",
            applicationService.createConversation(user.getUserName(), deptId, patientId, msg)));
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
    return Resp.success(Map.of("conversation", applicationService.getConversation(id)));
  }

  @GetMapping("/getConversationsByUserName")
  public Resp getConversationsByUserName(HttpServletRequest request, String id) {
    User user = (User) request.getSession().getAttribute("user");
    return Resp.success(
        Map.of("conversations", applicationService.getConversationsByUserName(user)));
  }

  @PostMapping("/createPatient")
  public Resp createPatient(String patientJson) {
    return Resp.success(Map.of("id", applicationService.createPatient(patientJson)));
  }

  @PostMapping("/updatePatient")
  public Resp updatePatient(String id, String patientJson) {
    return Resp.success(Map.of("result", applicationService.updatePatient(id, patientJson)));
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
    if (StringUtils.isEmpty(question)) {
      return Resp.success(null);
    }
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
    return Resp.success(
        Map.of("id", applicationService.feedback(user, conversationId, score, message)));
  }

  @GetMapping("/getConversationStats")
  public Resp getConversationStats(String conversationId) {
    return Resp.success(Map.of("stats", applicationService.getConversationStats(conversationId)));
  }

  @GetMapping("/getMedicines")
  public Resp getMedicines(String conversationId) {
    return Resp.success(applicationService.getMedicines(conversationId));
  }

  @PostMapping("/prescribeMedicines")
  public Resp prescribeMedicines(String conversationId, String medicines) {
    applicationService.prescribeMedicines(conversationId, List.of(medicines.split(",")));
    return Resp.success(Map.of("result", true));
  }

  @GetMapping("/getDiseaseOptions")
  public Resp getDiseaseOptions(String conversationId) {
    return Resp.success(applicationService.getConversationDiseaseOptions(conversationId));
  }

  @PostMapping("/diagnoseDisease")
  public Resp diagnoseDisease(String conversationId, String disease) {
    applicationService.diagnoseDisease(conversationId, disease);
    return Resp.success(Map.of("result", true));
  }

  @ExceptionHandler
  public Resp exceptionHandler(Exception e) {
    logger.error("Exception: ", e);
    return Resp.error(e.getMessage());
  }

  private boolean isNotAuthAdmin(String token) {
    return !Objects.equals(token, "9js52LsQxPyA078CqasXdtmJQTCCwe5wVDrUo3xEgB8=");
  }
}
