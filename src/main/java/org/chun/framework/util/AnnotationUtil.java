package org.chun.framework.util;

import lombok.AllArgsConstructor;
import org.chun.framework.annotations.Decode;
import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.annotations.bean.BaseParamBean;
import org.chun.framework.annotations.bean.RequestParamBean;
import org.chun.framework.annotations.bean.ResponseParamBean;
import org.chun.framework.annotations.enums.ProcessorApiColumn;
import org.chun.framework.exception.AnnotationUndefinedException;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static org.chun.framework.annotations.enums.ProcessorApiColumn.Request.DECODE;
import static org.chun.framework.annotations.enums.ProcessorApiColumn.Request.DECRYPT;

public class AnnotationUtil {

  /**
   * 標示要操作的資料
   *
   * @param annotation
   * @param enums
   * @return
   */
  public static BaseParamBean apiParamCollector(Annotation annotation, Enum<?>... enums) {
    if (annotation instanceof ProcessorAPI) {
      return switch (AnnotationUtil.ApiRoot.getEnum(enums[0])) {
        case REQUEST -> requestParam((ProcessorAPI) annotation);
        case RESPONSE -> responseParam((ProcessorAPI) annotation);
      };
    } else {
      throw new AnnotationUndefinedException(ProcessorAPI.class);
    }
  }

  @AllArgsConstructor
  enum ApiRoot {
    REQUEST(ProcessorApiColumn.Request.class),
    RESPONSE(ProcessorApiColumn.Response.class);
    final Class<?> apiRootClass;

    public static ApiRoot getEnum(Enum<?> column) {
      final Class<?> columnClass = column.getClass();
      return Arrays.stream(values())
          .filter(e -> columnClass == e.apiRootClass)
          .findAny()
          .orElseThrow(() -> new EnumConstantNotPresentException(ApiRoot.class, columnClass.getSimpleName()));
    }
  }

  /** =================================================== private ================================================== */

  /**
   * 請求物件操作標示
   *
   * @param apiOption
   * @return
   */
  private static RequestParamBean requestParam(ProcessorAPI apiOption) {
    RequestParamBean.RequestParamBuilder builder = RequestParamBean.builder();

    // 解編碼
    Decode[] decodes = apiOption.decode();
    if(decodes.length != 0){
      builder = builder.classify(DECODE);
      for (Decode decode : decodes) {
        builder = builder.storage(decode.name());
      }
    }

    // 解密
    String[] decrypts = apiOption.decrypt();
    if(decrypts.length != 0){
      builder = builder.classify(DECRYPT);
      for (String decrypt : decrypts) {
        builder = builder.storage(decrypt);
      }
    }

    return builder.build();
  }

  /**
   * 回應物件操作標示
   *
   * @param apiOption
   * @return
   */
  private static ResponseParamBean responseParam(ProcessorAPI apiOption) {
    ResponseParamBean.ResponseParamBuilder builder = ResponseParamBean.builder();

    return builder.build();
  }
}
