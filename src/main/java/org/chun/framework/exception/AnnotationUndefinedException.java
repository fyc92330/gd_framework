package org.chun.framework.exception;

public class AnnotationUndefinedException extends FrameworkBaseException {

  public AnnotationUndefinedException(Class<?> annotation) {
    super(String.format("Specified annotation is undefined. Require annotation: @%s", annotation.getSimpleName()));
  }
}
