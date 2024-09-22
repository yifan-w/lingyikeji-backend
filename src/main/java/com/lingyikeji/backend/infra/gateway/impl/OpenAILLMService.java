package com.lingyikeji.backend.infra.gateway.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lingyikeji.backend.domain.entities.Patient;
import com.lingyikeji.backend.infra.gateway.HttpService;
import com.lingyikeji.backend.infra.gateway.LLMService;
import com.lingyikeji.backend.infra.gateway.OpenAIChatBody;
import com.lingyikeji.backend.infra.gateway.dto.PatientQADTO;
import com.lingyikeji.backend.utils.GsonUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/** Created by Yifan Wang on 2024/8/5. */
@Service("openAILLMService")
@RequiredArgsConstructor
public class OpenAILLMService implements LLMService {
  private static final Logger logger = LogManager.getLogger(OpenAILLMService.class);
  private static final String URL = "https://chat.aawang.bio/v1/chat/completions";
  private static final String TOKEN = System.getenv("openai_token");
  private static final Map<String, String> HEADERS =
      Map.of("Authorization", "Bearer " + TOKEN, "Content-Type", "application/json");
  private final HttpService httpService;

  @Override
  public String askPatientQuestion(Patient patient, String question) {
    String prompt =
        GsonUtils.GSON.toJson(
                patient.getPatientQAList().stream().map(PatientQADTO::fromPatientQA).toList())
            + "以上是一个json对象数组，记录了一个医生和一个病人的对话，每一个对象的q属性代表一个问题，a属性代表一个回复。接下来我会作为医生提一个问题，请你作为病人考虑从json对象数组中选择一个与我提的问题最为相似的问题并回答我对应的回复，回答内容不要包含问题本身，回答末尾不要自行添加标点符号。如果没有任何相似的问题，请回答“请询问病情相关信息”。\n"
            + "问："
            + question;
    return this.sendPrompt(prompt);
  }

  @Override
  public String sendPrompt(String prompt) {
    logger.info("Prompt to Openai: {}", prompt);
    OpenAIChatBody body = OpenAIChatBody.create(prompt);
    String response = httpService.doPost(URL, HEADERS, body);
    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    String answer =
        jsonObject
            .get("choices")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject()
            .get("message")
            .getAsJsonObject()
            .get("content")
            .getAsString();
    logger.info("OpenAI response: {}", answer);
    return answer;
  }
}
