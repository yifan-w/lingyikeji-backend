package com.lingyikeji.backend.application.vo;

import com.lingyikeji.backend.domain.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by Yifan Wang on 2024/9/6. */
@Setter
@Getter
@NoArgsConstructor
public class UserVO {
  String name;
  String type;

  public static UserVO fromUser(User user) {
    return new UserVO(user.getUserName(), user.getUserType());
  }

  private UserVO(String name, String type) {
    this.name = name;
    this.type = type;
  }
}
