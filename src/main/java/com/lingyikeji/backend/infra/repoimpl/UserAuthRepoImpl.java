package com.lingyikeji.backend.infra.repoimpl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.User;
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
  public boolean save(User user) {
    if (this.findUserHashedPwdByUserName(user.getUserName()).isPresent()) {
      return false;
    }

    user.setUpdatedAt(LocalDateTime.now());
    if (StringUtils.isEmpty(user.getId())) {
      user.setCreatedAt(LocalDateTime.now());
    }
    datastore.save(user);
    return true;
  }

  @Override
  public void deleteById(String id) {
    Optional<User> userHashedPwdOptional =
        Optional.ofNullable(datastore.find(User.class).filter(eq("id", id)).first());
    if (userHashedPwdOptional.isEmpty()) {
      throw new RuntimeException("UserHashPwd " + id + " not found");
    }

    datastore.delete(userHashedPwdOptional.get());
  }

  @Override
  public List<User> findAll() {
    return datastore.find(User.class).iterator(new FindOptions()).toList();
  }

  @Override
  public Optional<User> findUserHashedPwdByUserName(String userName) {
    return Optional.ofNullable(datastore.find(User.class).filter(eq("userName", userName)).first());
  }

  @Override
  public Optional<String> findHashedPwdByUserName(String userName) {
    return Optional.ofNullable(datastore.find(User.class).filter(eq("userName", userName)).first())
        .map(User::getHashedPwd);
  }

  @Override
  public Optional<String> updateHashedPwdByUserName(User user) {
    Optional<User> userHashedPwdOptional = this.findUserHashedPwdByUserName(user.getUserName());
    if (userHashedPwdOptional.isEmpty()) {
      return Optional.empty();
    }

    userHashedPwdOptional.get().setHashedPwd(user.getHashedPwd());
    datastore.save(userHashedPwdOptional);
    return Optional.of(userHashedPwdOptional.get().getUserName());
  }
}
