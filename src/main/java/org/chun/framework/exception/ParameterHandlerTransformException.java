package org.chun.framework.exception;

public class ParameterHandlerTransformException extends FrameworkBaseException {

  ParameterHandlerTransformException(Class<?> rootClass) {
    super(errorMsg(rootClass.getSimpleName()));
  }

  private static String errorMsg(String className) {
    return switch (className) {
      case "DecodeHandler" -> "1";
      case "DecryptHandler" -> "2";
      case "HeaderHandler" -> "3";
      case "SkipHandler" -> "4";
      case "EncryptHandler" -> "5";
      case "MockHandler" -> "6";
      case "PageHandler" -> "7";
      default -> "9";
    };
  }
}
