package com.lingyikeji.backend.domain.entities;

import com.lingyikeji.backend.utils.HashUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/30. */
@Entity("user")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
  @Id private String id;
  private String userName;
  private String unHashedPwd;
  private String hashedPwd;
  private UserType userType = UserType.PERSONAL;

  private static enum UserType {
    PERSONAL,
    STUDENT,
    TEACHER
  }

  public static User create(String userName, String unHashedPwd, String userType) {
    return new User(
        null, userName, unHashedPwd, HashUtils.doHash(unHashedPwd), UserType.valueOf(userType));
  }

  private User(
      String id, String userName, String unHashedPwd, String hashedPwd, UserType userType) {
    super();
    this.id = id;
    this.userName = userName;
    this.unHashedPwd = unHashedPwd;
    this.hashedPwd = hashedPwd;
    this.userType = userType;
  }

  public String getUserType() {
    return this.userType.name();
  }

  public boolean testPwd(String inputPwd) {
    return Objects.equals(HashUtils.doHash(inputPwd), this.hashedPwd);
  }
}
