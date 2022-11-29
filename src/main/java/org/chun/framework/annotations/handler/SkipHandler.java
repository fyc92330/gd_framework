package org.chun.framework.annotations.handler;

import org.chun.framework.annotations.ProcessorAPI;
import org.springframework.stereotype.Service;

@Service
public class SkipHandler implements IParamHandler{
  @Override
  public String transform(Object value, ProcessorAPI annotation) {
    return null;
  }
}
