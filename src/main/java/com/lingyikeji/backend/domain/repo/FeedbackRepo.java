package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.Feedback;
import java.util.List;

/** Created by Yifan Wang on 2024/9/9. */
public interface FeedbackRepo {
  String save(Feedback feedback);

  List<Feedback> findAll();
}
