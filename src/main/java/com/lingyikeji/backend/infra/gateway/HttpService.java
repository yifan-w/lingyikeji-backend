package com.lingyikeji.backend.infra.gateway;

import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
  private static final Logger logger = LogManager.getLogger(HttpService.class);
  private final RestTemplate restTemplate = new RestTemplate();

  public String doGet(String url, Map<String, String> headers, Map<String, String> params) {
    return doRequest(HttpMethod.GET, url, headers, params);
  }

  public <T> String doPost(String url, Map<String, String> headers, T params) {
    return doRequest(HttpMethod.POST, url, headers, params);
  }

  private <T> String doRequest(
      HttpMethod method, String url, Map<String, String> headers, T params) {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (MapUtils.isNotEmpty(headers)) {
      headers.forEach(httpHeaders::set);
    }

    HttpEntity<T> entity = new HttpEntity<>(params, httpHeaders);

    ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    }

    throw new RuntimeException(
        "HTTP get error. Code: "
            + response.getStatusCode().toString()
            + ". Message: "
            + response.getBody());
  }
}
