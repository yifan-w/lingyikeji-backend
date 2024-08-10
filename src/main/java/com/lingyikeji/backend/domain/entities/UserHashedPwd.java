package com.lingyikeji.backend.domain.entities;

import com.lingyikeji.backend.utils.HashUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/7/30. */
@Entity("user_auth")
@Getter
@Setter
@NoArgsConstructor
public class UserHashedPwd extends BaseEntity {
  @Id private String id;
  private String userName;
  private String hashedPwd;

  public static UserHashedPwd create(String userName, String unHashedPwd) {
    return new UserHashedPwd(null, userName, HashUtils.doHash(unHashedPwd));
  }

  private UserHashedPwd(String id, String userName, String hashedPwd) {
    super();
    this.id = id;
    this.userName = userName;
    this.hashedPwd = hashedPwd;
  }
}
