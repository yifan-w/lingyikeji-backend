package com.lingyikeji.backend.facade.handler;

import com.lingyikeji.backend.application.MainApplicationService;
import com.lingyikeji.backend.domain.entities.User;
import com.lingyikeji.backend.utils.GsonUtils;
import com.lingyikeji.backend.utils.HashUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** Created by Yifan Wang on 2024/8/15. */
@Component
@RequiredArgsConstructor
public class HttpHandlerInterceptor implements HandlerInterceptor {
  private static final Logger logger = LogManager.getLogger(HttpHandlerInterceptor.class);

  private final MainApplicationService mainApplicationService;

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String userName =
        Arrays.stream(request.getCookies() == null ? new Cookie[0] : request.getCookies())
            .filter(cookie -> Objects.equals(cookie.getName(), "userName"))
            .findFirst()
            .map(Cookie::getValue)
            .orElse("");
    String token =
        Arrays.stream(request.getCookies() == null ? new Cookie[0] : request.getCookies())
            .filter(cookie -> Objects.equals(cookie.getName(), "token"))
            .findFirst()
            .map(Cookie::getValue)
            .orElse("");
    logger.info(
        "request uri: {}, parameters: {}, userName: {}",
        request.getRequestURI(),
        GsonUtils.GSON.toJson(request.getParameterMap()),
        userName);

    if (Objects.equals("/authUser", request.getRequestURI())) {
      return true;
    }

    if (!Objects.equals(token, HashUtils.doHash(userName))) {
      return false;
    }

    User user = mainApplicationService.getUserByUserName(userName);
    request.getSession().setAttribute("user", user);
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
