package org.chun.framework.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.chun.framework.annotations.ApiFor;
import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.annotations.bean.RequestParamBean;
import org.chun.framework.annotations.bean.ResponseParamBean;
import org.chun.framework.annotations.enums.ProcessorApiColumn;
import org.chun.framework.annotations.handler.DecodeHandler;
import org.chun.framework.annotations.handler.DecryptHandler;
import org.chun.framework.annotations.handler.EncryptHandler;
import org.chun.framework.annotations.handler.HeaderHandler;
import org.chun.framework.annotations.handler.IParamHandler;
import org.chun.framework.annotations.handler.MockHandler;
import org.chun.framework.annotations.handler.PageHandler;
import org.chun.framework.annotations.handler.SkipHandler;
import org.chun.framework.demo.model.ApiResponse;
import org.chun.framework.demo.model.base.BaseModel;
import org.chun.framework.demo.model.rq.DemoControllerRequest;
import org.chun.framework.util.AnnotationUtil;
import org.chun.framework.util.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.chun.framework.annotations.enums.ProcessorApiColumn.Request.DECODE;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Request.DECRYPT;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Response.ENCRYPT;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Response.HEADER;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Response.MOCK;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Response.PAGE;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Response.SKIP;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class ControllerAroundAspect {
  private final DecodeHandler decodeHandler;
  private final DecryptHandler decryptHandler;
  private final HeaderHandler headerHandler;
  private final SkipHandler skipHandler;
  private final EncryptHandler encryptHandler;
  private final MockHandler mockHandler;
  private final PageHandler pageHandler;

  @Pointcut("execution(@(org.chun.framework.annotations.ProcessorAPI) * *(..))")
  void appProgrammingInterface() {

  }

  @Before(value = "appProgrammingInterface()")
  protected void beforeApi(JoinPoint joinPoint) throws Exception {
    log.info(" >>> before api <<< ");
    // 取得物件與參數
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    String methodName = method.getName();
    Class<?> baseClass = method.getDeclaringClass();
    Parameter[] params = method.getParameters();
    Object[] args = joinPoint.getArgs();

    // 取得 ProcessorAPI 參數
    final ProcessorAPI annotation = method.getAnnotation(ProcessorAPI.class);
    RequestParamBean bean =
        (RequestParamBean) AnnotationUtil.apiParamCollector(annotation, DECODE, DECRYPT);

    // 取得 RequestBody
    for (int i = 0; i < params.length; i++) {
      if (params[i].getAnnotation(RequestBody.class) != null) {
        Class<?> requestBodyClass = args[i].getClass();
        Map<String, Object> requestBody = JsonUtil.toMap(args[i]);
        log.error(" >>> data:{}", requestBody);

        // 取得 ApiFor 對請求操作的值 針對傳入的參數做處理
        List<String> fieldList = this.apiFieldList(methodName, requestBodyClass);
        Map<String, Object> body = new HashMap<>();
        for (String key : requestBody.keySet()) {
          if(fieldList.contains(key)){
            body.put(key, this.requestHandler(requestBody.get(key),bean.get(key), annotation));
          }
        }

        body.put("baseClass", baseClass);
        args[i] = JsonUtil.convert(body, requestBodyClass);
        break;
      }
    }
  }

  @After(value = "appProgrammingInterface()")
  protected Object afterApi(JoinPoint joinPoint) throws Exception {
    log.info(" >>> after api <<< ");
    // 取得物件與參數
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    String methodName = method.getName();

    // 取得 ProcessorAPI 參數
    final ProcessorAPI annotation = method.getAnnotation(ProcessorAPI.class);
    ResponseParamBean bean =
        (ResponseParamBean) AnnotationUtil.apiParamCollector(annotation, HEADER, SKIP, ENCRYPT, MOCK, PAGE);

    Object target = joinPoint.getTarget();
    Class<?> responseBodyClass = target.getClass();
    Map<String, Object> responseBody = JsonUtil.toMap(target);

    // 取得 ApiFor 對請求操作的值 針對傳入的參數做處理
    List<String> fieldList = this.apiFieldList(methodName, responseBodyClass);
    for (String key : responseBody.keySet()) {
      if (fieldList.contains(key)) {
        responseBody.put(key, this.responseHandler(responseBody.get(key), bean.get(key), annotation));
      } else {
        responseBody.remove(key);
      }
    }

    return JsonUtil.convert(responseBody, responseBodyClass);
  }

  @AfterReturning(value = "appProgrammingInterface()", returning = "model")
  ApiResponse<?> afterReturn(JoinPoint joinPoint, Object model) {
    log.info(" >>> after return <<< ");
    return new ApiResponse<>((BaseModel<?>) model, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
  }


  /** =================================================== private ================================================== */

  /**
   * 處理Request參數
   *
   * @param fieldValue
   * @param rules
   * @param annotation
   * @return
   */
  private Object requestHandler(Object fieldValue, String[] rules, ProcessorAPI annotation) {
    boolean isNotString = !(fieldValue instanceof String);
    boolean isNoNeedTransform = rules.length == 0 || Objects.isNull(fieldValue);
    if (isNotString || isNoNeedTransform) {
      return fieldValue;
    }

    String value = (String) fieldValue;
    for (ProcessorApiColumn.Request ruleEnum : Arrays.stream(rules).map(ProcessorApiColumn.Request::getEnum).toList()) {
      if (value == null || Strings.isBlank(value)) {
        break;
      }

      IParamHandler handler = switch (ruleEnum) {
        case DECODE -> decodeHandler;
        case DECRYPT -> decryptHandler;
      };
      value = handler.transform(value, annotation);
    }
    return value;
  }

  /**
   * 處理Response參數
   *
   * @param fieldValue
   * @param rules
   * @param annotation
   * @return
   */
  private Object responseHandler(Object fieldValue, String[] rules, ProcessorAPI annotation) {
    if (rules.length == 0 || Objects.isNull(fieldValue)) {
      return fieldValue;
    }

    Object value = fieldValue;
    for (ProcessorApiColumn.Response ruleEnum : Arrays.stream(rules).map(ProcessorApiColumn.Response::getEnum).toList()) {
      if (value == null || Strings.isBlank((String) value) || HEADER == ruleEnum) {
        break;
      }

      IParamHandler handler = switch (ruleEnum) {
        case SKIP -> skipHandler;
        case ENCRYPT -> encryptHandler;
        case MOCK -> mockHandler;
        case PAGE -> pageHandler;
        default -> throw new IllegalStateException("Unexpected value: " + ruleEnum);
      };
      value = handler.transform(value, annotation);
    }
    return value;
  }

  /**
   * 集中ApiFor指定的參數
   *
   * @param methodName
   * @param clazz
   * @return
   */
  private List<String> apiFieldList(String methodName, Class<?> clazz) {
    Predicate<Field> apiForMethodFilter = field -> {
      ApiFor apiFor = field.getAnnotation(ApiFor.class);
      return apiFor != null && Arrays.asList(apiFor.name()).contains(methodName);
    };
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(apiForMethodFilter)
        .map(Field::getName)
        .toList();
  }

}
