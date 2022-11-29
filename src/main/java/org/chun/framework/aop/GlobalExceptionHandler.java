package org.chun.framework.aop;

import lombok.extern.slf4j.Slf4j;
import org.chun.framework.demo.model.ApiResponse;
import org.chun.framework.exception.FrameworkBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(FrameworkBaseException.class)
  private ApiResponse<?> errorHandler(FrameworkBaseException e) {
    log.warn(">>> framework feedback: ", e);
    ApiResponse<?> apiResponse = new ApiResponse<>();
    apiResponse.setHttpCode(HttpStatus.OK.value());
    apiResponse.setErrorMsg(e.getMessage());
    return apiResponse;
  }

  @ExceptionHandler(Exception.class)
  private ApiResponse<?> errorHandler(HandlerMethod handlerMethod, Exception e) {
    log.error(">>> exception: ", e);
    ApiResponse<?> apiResponse = new ApiResponse<>();
    apiResponse.setHttpCode(HttpStatus.BAD_REQUEST.value());
    return apiResponse;
  }
}
