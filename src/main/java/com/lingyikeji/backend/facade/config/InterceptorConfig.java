package com.lingyikeji.backend.facade.config;

import com.lingyikeji.backend.facade.handler.HttpHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Created by Yifan Wang on 2024/8/15. */
@Component
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
  private final HttpHandlerInterceptor interceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor);
  }
}
