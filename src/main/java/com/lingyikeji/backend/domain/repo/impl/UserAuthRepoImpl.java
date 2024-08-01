package com.lingyikeji.backend.domain.repo.impl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.UserHashedPwd;
import com.lingyikeji.backend.domain.repo.UserAuthRepo;
import dev.morphia.Datastore;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Created by Yifan Wang on 2024/7/30. */
@Repository
@RequiredArgsConstructor
public class UserAuthRepoImpl implements UserAuthRepo {
  private final Datastore datastore;

  @Override
  public boolean save(UserHashedPwd userHashedPwd) {
    if (this.findUserHashedPwdByUserName(userHashedPwd.getUserName()).isPresent()) {
      return false;
    }

    datastore.save(userHashedPwd);
    return true;
  }

  @Override
  public Optional<UserHashedPwd> findUserHashedPwdByUserName(String userName) {
    return Optional.ofNullable(
        datastore.find(UserHashedPwd.class).filter(eq("userName", userName)).first());
  }

  @Override
  public Optional<String> findHashedPwdByUserName(String userName) {
    return Optional.ofNullable(
            datastore.find(UserHashedPwd.class).filter(eq("userName", userName)).first())
        .map(UserHashedPwd::getHashedPwd);
  }

  @Override
  public Optional<String> updateHashedPwdByUserName(UserHashedPwd userHashedPwd) {
    Optional<UserHashedPwd> userHashedPwdOptional =
        this.findUserHashedPwdByUserName(userHashedPwd.getUserName());
    if (userHashedPwdOptional.isEmpty()) {
      return Optional.empty();
    }

    userHashedPwdOptional.get().setHashedPwd(userHashedPwd.getHashedPwd());
    datastore.save(userHashedPwdOptional);
    return Optional.of(userHashedPwdOptional.get().getUserName());
  }
}
