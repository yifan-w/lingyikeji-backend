package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.Disease;

import java.util.List;
import java.util.Optional;

/** Created by Yifan Wang on 2024/7/18. */
public interface DiseaseRepo {
  String save(Disease disease);

  Optional<Disease> findById(String id);

  List<Disease> findAll();

  void deleteById(String id);
}
