package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Embedded;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/18. */
@Embedded
@Getter
@Setter
@NoArgsConstructor
public class Message {
  private static enum Type {
    USER,
    PATIENT
  }

  private Type type;
  private String content;
  private LocalDateTime createdAt;

  public static Message fromUser(String content) {
    return new Message(Type.USER, content);
  }

  public static Message fromPatient(String content) {
    return new Message(Type.PATIENT, content);
  }

  private Message(Type type, String content) {
    this.type = type;
    this.content = content;
    this.createdAt = LocalDateTime.now();
  }

  public boolean fromUser() {
    return this.type == Type.USER;
  }

  public boolean fromPatient() {
    return this.type == Type.PATIENT;
  }
}
