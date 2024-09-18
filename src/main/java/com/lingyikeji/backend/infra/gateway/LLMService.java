package com.lingyikeji.backend.infra.gateway;

import com.lingyikeji.backend.domain.entities.Patient;

public interface LLMService {
  String askPatientQuestion(Patient patient, String question);

  String sendPrompt(String prompt);
}
