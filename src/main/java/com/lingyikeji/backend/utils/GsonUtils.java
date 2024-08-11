package com.lingyikeji.backend.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Created by Yifan Wang on 2024/8/8. */
public class GsonUtils {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
          .addSerializationExclusionStrategy(
              new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                  final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                  return expose != null && !expose.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                  return false;
                }
              })
          .addDeserializationExclusionStrategy(
              new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                  final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                  return expose != null && !expose.deserialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                  return false;
                }
              })
          .create();

  private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return LocalDateTime.parse(
          json.getAsString(),
          DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss").withLocale(Locale.ENGLISH));
    }
  }

  private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    public JsonElement serialize(
        LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(
          localDateTime.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));
    }
  }
}
