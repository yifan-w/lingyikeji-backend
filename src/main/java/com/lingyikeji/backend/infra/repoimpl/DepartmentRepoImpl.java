package com.lingyikeji.backend.infra.repoimpl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.Department;
import com.lingyikeji.backend.domain.repo.DepartmentRepo;
import dev.morphia.Datastore;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Created by Yifan Wang on 2024/8/1. */
@Repository
@RequiredArgsConstructor
public class DepartmentRepoImpl implements DepartmentRepo {
  private final Datastore datastore;

  @Override
  public String save(Department department) {
    return datastore.save(department).getId();
  }

  @Override
  public List<Department> findAll() {
    return datastore.find(Department.class).iterator().toList();
  }

  @Override
  public Optional<Department> findById(String id) {
    return Optional.ofNullable(datastore.find(Department.class).filter(eq("id", id)).first());
  }
}
