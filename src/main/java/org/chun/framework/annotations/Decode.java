package org.chun.framework.annotations;

import org.chun.framework.annotations.enums.EncodingType;

public @interface Decode {

  /**
   * 解碼欄位名稱
   */
  String name();

  /**
   * 解碼欄位類型
   */
  EncodingType type();
}
