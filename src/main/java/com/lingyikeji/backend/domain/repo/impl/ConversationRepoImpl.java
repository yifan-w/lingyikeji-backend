package com.lingyikeji.backend.domain.repo.impl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.Conversation;
import com.lingyikeji.backend.domain.repo.ConversationRepo;
import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Created by Yifan Wang on 2024/7/18. */
@Repository
@RequiredArgsConstructor
public class ConversationRepoImpl implements ConversationRepo {
  private final Datastore datastore;

  @Override
  public String save(Conversation conversation) {
    return datastore.save(conversation).getId();
  }

  @Override
  public Optional<Conversation> findById(String id) {
    return Optional.ofNullable(datastore.find(Conversation.class).filter(eq("id", id)).first());
  }

  @Override
  public List<Conversation> findAll() {
    return datastore
        .find(Conversation.class)
        .iterator(new FindOptions().sort(Sort.descending("createdAt")))
        .toList();
  }

  @Override
  public void deleteById(String id) {
    Optional<Conversation> conversationOptional = this.findById(id);
    if (conversationOptional.isEmpty()) {
      throw new RuntimeException("Conversation " + id + " not found");
    }

    datastore.delete(conversationOptional.get());
  }
}
