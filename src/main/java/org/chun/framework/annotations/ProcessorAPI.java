package org.chun.framework.annotations;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = {GET, POST, PUT, DELETE})
public @interface ProcessorAPI {

  /**
   * RequestMapping 的路徑
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] value();

  /**
   * RequestMapping 的方法類型
   */
  @AliasFor(annotation = RequestMapping.class)
  RequestMethod[] method();

  /**
   * 傳入參數需要進行解密的欄位
   */
  String[] decrypt() default {};

  /**
   * 傳入參數需要進行解碼的欄位
   */
  Decode[] decode() default {};

  /**
   * 回傳參數需要進行加密的欄位
   */
  String[] encrypt() default {};

  /**
   * 進行後端分頁, 單頁所顯示的資料筆數
   */
  String page() default Strings.EMPTY;

  /**
   * 回傳參數需要被過濾掉的欄位
   */
  String[] skip() default {};

  /**
   * 由Header 進行回傳的參數
   */
  String[] header() default {};

  /**
   * 需要被遮罩的回傳參數
   */
  Mock[] mocks() default {};
}
