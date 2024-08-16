package com.lingyikeji.backend.facade.handler;

import com.lingyikeji.backend.utils.GsonUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** Created by Yifan Wang on 2024/8/15. */
@Component
public class HttpHandlerInterceptor implements HandlerInterceptor {
  private static final Logger logger = LogManager.getLogger(HttpHandlerInterceptor.class);

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    logger.info(
        "request uri: {}, parameters: {}, userName: {}",
        request.getRequestURI(),
        GsonUtils.GSON.toJson(request.getParameterMap()),
        Arrays.stream(request.getCookies() == null ? new Cookie[0] : request.getCookies())
            .filter(cookie -> Objects.equals(cookie.getName(), "userName"))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(""));
    return true;
  }

  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable ModelAndView modelAndView)
      throws Exception {}

  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable Exception ex)
      throws Exception {}
}
