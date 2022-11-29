package org.chun.framework.annotations.handler;

import org.chun.framework.annotations.Decode;
import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.annotations.enums.EncodingType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Service
public class DecodeHandler implements IParamHandler {
  @Override
  public String transform(Object value, ProcessorAPI annotation) {
    String valueString = (String) value;
    return Arrays.stream(annotation.decode())
        .filter(de -> valueString.equals(de.name()))
        .findAny()
        .map(Decode::type)
        .map(type -> this.decode(type, valueString))
        .orElse(valueString);
  }

  private String decode(EncodingType decodeType, String value){
    return switch (decodeType){
      case BASE64 -> base64Decoder(value);
      case UNICODE -> value;
    };
  }

  private String base64Decoder(String value){
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] textBytes = value.getBytes(StandardCharsets.UTF_8);
    return new String(decoder.decode(textBytes), StandardCharsets.UTF_8);
  }
}
