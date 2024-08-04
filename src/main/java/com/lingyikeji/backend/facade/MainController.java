package com.lingyikeji.backend.facade;

import com.lingyikeji.backend.application.MainApplicationService;
import com.lingyikeji.backend.application.vo.Resp;
import com.lingyikeji.backend.utils.HashUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Created by Yifan Wang on 2023/10/16. */
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class MainController {
  private static final Logger logger = LogManager.getLogger(MainController.class);
  private static final AtomicInteger counter = new AtomicInteger(1);
  private static final String STATIC_M3U8 =
      """
          #EXTM3U
          #EXT-X-PLAYLIST-TYPE:VOD
          #EXT-X-TARGETDURATION:8
          #EXT-X-MEDIA-SEQUENCE:0
          """;
  private static final String LOOP_M3U8 =
      """
          #EXTINF:8,
          https://www.pixelgeom.com/livestream/lingyi/idle.ts
          #EXT-X-ENDLIST
          """;
  private final MainApplicationService applicationService;

  @PostMapping("/createUserAuth")
  public Resp createUserAuth(String userName, String pwd) {
    return Resp.success(Map.of("result", applicationService.createUserAuth(userName, pwd)));
  }

  @PostMapping("/authUser")
  public Resp authUser(String userName, String pwd, HttpServletResponse response) {
    boolean authResult = applicationService.authUser(userName, pwd);
    if (authResult) {
      Cookie cookie = new Cookie("token", HashUtils.doHash(userName));
      cookie.setMaxAge(60 * 60 * 24 * 30);
      response.addCookie(cookie);
    }
    return Resp.success(Map.of("result", authResult));
  }

  @PostMapping("/createDept")
  public Resp createDept(String name) {
    return Resp.success(Map.of("id", applicationService.createDepartment(name)));
  }

  @GetMapping("/getAllDepts")
  public Resp getAllDept() {
    return Resp.success(Map.of("depts", applicationService.getAllDepartments()));
  }

  @PostMapping("/createDisease")
  public Resp createDisease(String name, String desc) {
    return Resp.success(Map.of("id", applicationService.createDisease(name, desc)));
  }

  @PostMapping("/createConversation")
  public Resp createConversation(String diseaseId) {
    return Resp.success(Map.of("id", applicationService.createConversation(diseaseId)));
  }

  @GetMapping("/getDisease")
  public Resp getDisease(String id) {
    return Resp.success(applicationService.getDisease(id));
  }

  @GetMapping("/getConversation")
  public Resp getConversation(String id) {
    return Resp.success(applicationService.getConversation(id));
  }

  @GetMapping("/getLivestream")
  public ResponseEntity<Resource> getLivestream(String id) {
    StringBuilder sb = new StringBuilder(STATIC_M3U8);
    sb.append(LOOP_M3U8.repeat(counter.get()));
    String m3u8Content = sb.toString();
    try {
      return ResponseEntity.ok()
          .contentLength(m3u8Content.getBytes("UTF-8").length)
          .contentType(MediaType.parseMediaType("application/x-mpegURL"))
          // .header("Content-Disposition", "attachment; filename=demo.m3u8")
          .body(
              new InputStreamResource(
                  new ByteArrayInputStream(m3u8Content.getBytes(StandardCharsets.UTF_8))));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/testLLM")
  public Resp testLLM(String message) {
    return Resp.success(Map.of("msg", applicationService.testLLM(message)));
  }
}
