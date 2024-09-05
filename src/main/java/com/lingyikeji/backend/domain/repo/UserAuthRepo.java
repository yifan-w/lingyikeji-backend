package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.User;
import java.util.List;
import java.util.Optional;

/** Created by Yifan Wang on 2024/7/30. */
public interface UserAuthRepo {
  boolean save(User user);

  void deleteById(String id);

  List<User> findAll();

  Optional<User> findUserHashedPwdByUserName(String userName);

  Optional<String> findHashedPwdByUserName(String userName);

  Optional<String> updateHashedPwdByUserName(User user);
}
