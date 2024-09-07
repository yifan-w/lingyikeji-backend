package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.Conversation;
import java.util.List;
import java.util.Optional;

/** Created by Yifan Wang on 2024/7/18. */
public interface ConversationRepo {
  String save(Conversation conversation);

  Optional<Conversation> findById(String id);

  List<Conversation> findByUserName(String userName);

  List<Conversation> findAll();

  void deleteById(String id);
}
