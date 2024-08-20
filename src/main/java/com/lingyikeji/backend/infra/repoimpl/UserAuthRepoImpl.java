package com.lingyikeji.backend.infra.repoimpl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.UserHashedPwd;
import com.lingyikeji.backend.domain.repo.UserAuthRepo;
import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    userHashedPwd.setUpdatedAt(LocalDateTime.now());
    if (StringUtils.isEmpty(userHashedPwd.getId())) {
      userHashedPwd.setCreatedAt(LocalDateTime.now());
    }
    datastore.save(userHashedPwd);
    return true;
  }

  @Override
  public void deleteById(String id) {
    Optional<UserHashedPwd> userHashedPwdOptional =
        Optional.ofNullable(datastore.find(UserHashedPwd.class).filter(eq("id", id)).first());
    if (userHashedPwdOptional.isEmpty()) {
      throw new RuntimeException("UserHashPwd " + id + " not found");
    }

    datastore.delete(userHashedPwdOptional.get());
  }

  @Override
  public List<UserHashedPwd> findAll() {
    return datastore.find(UserHashedPwd.class).iterator(new FindOptions()).toList();
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
