package org.chun.framework.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.demo.model.ApiResponse;
import org.chun.framework.demo.model.base.BaseModel;
import org.chun.framework.util.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<ApiResponse<?>> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    final ProcessorAPI annotation = returnType.getMethodAnnotation(ProcessorAPI.class);
    return annotation != null && Arrays.equals(Strings.EMPTY_ARRAY, annotation.header());
  }

  @Override
  public ApiResponse<?> beforeBodyWrite(ApiResponse<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
    assert body != null;
    BaseModel<?> data = body.getData();
    Class<?> dataClass = data.getClass();
    Map<String, Object> responseBody = JsonUtil.toMap(data);
    String[] param2Header = Objects.requireNonNull(returnType.getMethodAnnotation(ProcessorAPI.class)).header();

    for (String paramKey : param2Header) {
      Object value = responseBody.get(paramKey);
      if (value instanceof String) {
        response.getHeaders().add(paramKey, (String) value);
        responseBody.remove(paramKey);
      }
    }

    return new ApiResponse<>((BaseModel<?>) JsonUtil.convert(responseBody, dataClass), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
  }
}
