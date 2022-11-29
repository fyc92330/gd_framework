package org.chun.framework.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.boot.json.JacksonJsonParser;

import java.util.List;
import java.util.Map;

public class JsonUtil {

  private static final ObjectMapper objectMapper = JsonMapper.builder()
      .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
      .enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build()
      .registerModule(new JavaTimeModule())
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  private static final JacksonJsonParser jsonParser = new JacksonJsonParser(objectMapper);

  @SneakyThrows
  public static Map<String, Object> toMap(Object jsonObject) {
    return jsonParser.parseMap(convert2JsonStr(jsonObject));
  }

  @SneakyThrows
  public static List<Object> toList(Object jsonObject) {
    return jsonParser.parseList(convert2JsonStr(jsonObject));
  }

  public static <T> T convert(Object jsonObject, Class<T> clazz) {
    return objectMapper.convertValue(jsonObject, clazz);
  }

  /** =================================================== private ================================================== */

  private static String convert2JsonStr(Object jsonObject) throws JsonProcessingException {
    return objectMapper.writeValueAsString(jsonObject);
  }
}
