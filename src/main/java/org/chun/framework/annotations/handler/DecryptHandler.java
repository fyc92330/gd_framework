package org.chun.framework.annotations.handler;

import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.util.StringCryptoUtil;
import org.springframework.stereotype.Service;

@Service
public class DecryptHandler implements IParamHandler {
  @Override
  public String transform(Object value, ProcessorAPI annotation) {
    return StringCryptoUtil.getDecrypt((String) value);
  }
}
