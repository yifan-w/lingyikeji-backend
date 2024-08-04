package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.Department;
import java.util.List;
import java.util.Optional;

/** Created by Yifan Wang on 2024/8/1. */
public interface DepartmentRepo {
  String save(Department department);

  List<Department> findAll();

  Optional<Department> findById(String id);
}
