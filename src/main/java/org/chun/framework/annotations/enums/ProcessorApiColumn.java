package org.chun.framework.annotations.enums;

import java.util.Arrays;

public class ProcessorApiColumn {

  public enum Request {
    DECRYPT,
    DECODE;

    public static Request getEnum(String title) {
      return Arrays.stream(values())
          .filter(e -> e.name().equals(title))
          .findAny()
          .orElseThrow(() -> new EnumConstantNotPresentException(Request.class, title));
    }
  }

  public enum Response {
    ENCRYPT,
    PAGE,
    SKIP,
    HEADER,
    MOCK;

    public static Response getEnum(String title) {
      return Arrays.stream(values())
          .filter(e -> e.name().equals(title))
          .findAny()
          .orElseThrow(() -> new EnumConstantNotPresentException(Response.class, title));
    }
  }
}
