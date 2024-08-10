package com.lingyikeji.backend.domain.repo;

import com.lingyikeji.backend.domain.entities.Patient;
import java.util.List;
import java.util.Optional;

/** Created by Yifan Wang on 2024/7/18. */
public interface PatientRepo {
  String save(Patient patient);

  Optional<Patient> findById(String id);

  List<Patient> findAll();

  void deleteById(String id);
}
