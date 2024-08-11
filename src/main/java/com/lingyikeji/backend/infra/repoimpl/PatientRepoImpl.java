package com.lingyikeji.backend.infra.repoimpl;

import static dev.morphia.query.experimental.filters.Filters.eq;

import com.lingyikeji.backend.domain.entities.Patient;
import com.lingyikeji.backend.domain.repo.PatientRepo;
import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/** Created by Yifan Wang on 2024/7/18. */
@Repository
@RequiredArgsConstructor
public class PatientRepoImpl implements PatientRepo {
  private final Datastore datastore;

  @Override
  public String save(Patient patient) {
    patient.setUpdatedAt(LocalDateTime.now());
    if (StringUtils.isEmpty(patient.getId())) {
      patient.setCreatedAt(LocalDateTime.now());
    }
    return datastore.save(patient).getId();
  }

  @Override
  public Optional<Patient> findById(String id) {
    return Optional.ofNullable(datastore.find(Patient.class).filter(eq("id", id)).first());
  }

  @Override
  public List<Patient> findAll() {
    return datastore
        .find(Patient.class)
        .iterator(new FindOptions().sort(Sort.descending("createdAt")))
        .toList();
  }

  @Override
  public void deleteById(String id) {
    Optional<Patient> patientOptional = this.findById(id);
    if (patientOptional.isEmpty()) {
      throw new RuntimeException("Disease " + id + " not found");
    }

    datastore.delete(patientOptional.get());
  }
}
