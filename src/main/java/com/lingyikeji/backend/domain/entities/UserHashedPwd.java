package com.lingyikeji.backend.domain.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/30. */
@Entity("user_auth")
@Getter
@Setter
@NoArgsConstructor
public class UserHashedPwd {
  @Id private String id;
  private String userName;
  private String hashedPwd;

  public static UserHashedPwd create(String userName, String unHashedPwd) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hash = digest.digest(unHashedPwd.getBytes(StandardCharsets.UTF_8));
    String hashedPwd = new String(hash, StandardCharsets.UTF_8);
    return new UserHashedPwd(null, userName, hashedPwd);
  }

  private UserHashedPwd(String id, String userName, String hashedPwd) {
    this.id = id;
    this.userName = userName;
    this.hashedPwd = hashedPwd;
  }
}
