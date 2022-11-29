package org.chun.framework.annotations.handler;

import org.chun.framework.annotations.ProcessorAPI;
import org.springframework.stereotype.Service;

@Service
public class HeaderHandler implements IParamHandler{
  @Override
  public String transform(Object value, ProcessorAPI annotation) {
    return null;
  }
}
