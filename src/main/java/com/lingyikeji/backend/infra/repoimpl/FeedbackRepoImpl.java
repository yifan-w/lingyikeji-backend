package com.lingyikeji.backend.infra.repoimpl;

import com.lingyikeji.backend.domain.entities.Feedback;
import com.lingyikeji.backend.domain.repo.FeedbackRepo;
import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/** Created by Yifan Wang on 2024/9/9. */
@Repository
@RequiredArgsConstructor
public class FeedbackRepoImpl implements FeedbackRepo {
  private final Datastore datastore;

  @Override
  public String save(Feedback feedback) {
    feedback.setUpdatedAt(LocalDateTime.now());
    if (StringUtils.isEmpty(feedback.getId())) {
      feedback.setCreatedAt(LocalDateTime.now());
    }
    return datastore.save(feedback).getId();
  }

  @Override
  public List<Feedback> findAll() {
    return datastore
        .find(Feedback.class)
        .iterator(new FindOptions().sort(Sort.descending("createdAt")))
        .toList();
  }
}
