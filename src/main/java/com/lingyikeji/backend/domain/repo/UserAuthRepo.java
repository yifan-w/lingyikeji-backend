package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.UserHashedPwd;
import java.util.Optional;

/** Created by Yifan Wang on 2024/7/30. */
public interface UserAuthRepo {
  boolean save(UserHashedPwd userHashedPwd);

  Optional<UserHashedPwd> findUserHashedPwdByUserName(String userName);

  Optional<String> findHashedPwdByUserName(String userName);

  Optional<String> updateHashedPwdByUserName(UserHashedPwd userHashedPwd);
}
