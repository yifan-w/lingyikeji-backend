package com.lingyikeji.backend.infra;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lingyikeji.backend.infra.gateway.HttpService;
import com.lingyikeji.backend.infra.gateway.LLMService;
import com.lingyikeji.backend.infra.gateway.OpenAIChatBody;
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
  public String sendPrompt(String prompt) {
    logger.info("Prompt to Openai: {}", prompt);
    OpenAIChatBody body = OpenAIChatBody.create(prompt);
    String response = httpService.doPost(URL, HEADERS, body);
    logger.info("OpenAI response: {}", response);
    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    return jsonObject
        .get("choices")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("message")
        .getAsJsonObject()
        .get("content")
        .getAsString();
  }
}
