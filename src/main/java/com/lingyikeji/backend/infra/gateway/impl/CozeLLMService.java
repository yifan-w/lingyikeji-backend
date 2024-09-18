package com.lingyikeji.backend.infra.gateway.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lingyikeji.backend.domain.entities.Patient;
import com.lingyikeji.backend.infra.gateway.CozeChatBody;
import com.lingyikeji.backend.infra.gateway.CozeChatMessage;
import com.lingyikeji.backend.infra.gateway.LLMService;
import com.lingyikeji.backend.infra.gateway.dto.CozeAskQuestionDTO;
import com.lingyikeji.backend.utils.GsonUtils;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service("cozeLLMService")
public class CozeLLMService implements LLMService {
  private static final Logger logger = LogManager.getLogger(CozeLLMService.class);
  private static final String BOT_ID = "7393595533455736839";

  private final WebClient client =
      WebClient.builder()
          .baseUrl("https://api.coze.com/v3/chat")
          .defaultHeaders(
              httpHeaders -> {
                httpHeaders.set(
                    "Authorization",
                    "Bearer pat_aLoW3ZA8z6KqkUJhd8ykxUTjY8LnokXp0clqw12ggG4pFFOXti6b7RbNBiRfzA75");
                httpHeaders.set("Content-Type", "application/json");
              })
          .build();

  @Override
  public String askPatientQuestion(Patient patient, String question) {
    return this.sendPrompt(GsonUtils.GSON.toJson(CozeAskQuestionDTO.create(patient, question)));
  }

  @Override
  public String sendPrompt(String prompt) {
    ParameterizedTypeReference<ServerSentEvent<String>> type =
        new ParameterizedTypeReference<ServerSentEvent<String>>() {};

    CozeChatBody body =
        CozeChatBody.create(BOT_ID, "12345", List.of(CozeChatMessage.create(prompt)));

    Flux<ServerSentEvent<String>> eventStream =
        client.post().bodyValue(body).retrieve().bodyToFlux(type);

    StringBuilder stringBuilder = new StringBuilder();
    Object lock = new Object();

    logger.info("Prompt to Coze: {}", prompt);
    eventStream.subscribe(
        content -> {
          if (StringUtils.equals(content.event(), "conversation.message.completed")) {
            JsonObject jsonObject = JsonParser.parseString(content.data()).getAsJsonObject();
            if (StringUtils.equals(jsonObject.get("type").getAsString(), "answer")) {
              logger.info("Coze content: {}", content.data());
              stringBuilder.append(jsonObject.get("content").getAsString());
              synchronized (lock) {
                lock.notifyAll();
              }
            }
          }
        },
        error -> logger.error("Error receiving SSE: {}", error, error),
        () -> {});

    synchronized (lock) {
      try {
        lock.wait();
      } catch (InterruptedException e) {
        logger.error(e);
      }

      String response = stringBuilder.toString();
      logger.info("Coze response: {}", response);
      return response;
    }
  }
}
