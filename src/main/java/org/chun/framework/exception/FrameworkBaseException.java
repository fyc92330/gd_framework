package org.chun.framework.exception;

public class FrameworkBaseException extends RuntimeException {

  public FrameworkBaseException() {
    super();
  }

  public FrameworkBaseException(String errorMsg) {
    super(errorMsg);
  }
}
