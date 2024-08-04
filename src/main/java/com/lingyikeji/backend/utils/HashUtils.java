package com.lingyikeji.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** Created by Yifan Wang on 2024/8/1. */
public class HashUtils {
  private static final Base64.Encoder ENCODER = Base64.getEncoder();

  public static String doHash(String unHashed) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hash = digest.digest(unHashed.getBytes(StandardCharsets.UTF_8));
    return ENCODER.encodeToString(hash);
  }
}
