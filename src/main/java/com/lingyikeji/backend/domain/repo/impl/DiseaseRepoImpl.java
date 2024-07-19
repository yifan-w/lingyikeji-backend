package com.lingyikeji.backend.domain.repo.impl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.Disease;
import com.lingyikeji.backend.domain.repo.DiseaseRepo;
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
public class DiseaseRepoImpl implements DiseaseRepo {
  private final Datastore datastore;

  @Override
  public String save(Disease disease) {
    return datastore.save(disease).getId();
  }

  @Override
  public Optional<Disease> findById(String id) {
    return Optional.ofNullable(datastore.find(Disease.class).filter(eq("id", id)).first());
  }

  @Override
  public List<Disease> findAll() {
    return datastore
        .find(Disease.class)
        .iterator(new FindOptions().sort(Sort.descending("createdAt")))
        .toList();
  }

  @Override
  public void deleteById(String id) {
    Optional<Disease> diseaseOptional = this.findById(id);
    if (diseaseOptional.isEmpty()) {
      throw new RuntimeException("Disease " + id + " not found");
    }

    datastore.delete(diseaseOptional.get());
  }
}
