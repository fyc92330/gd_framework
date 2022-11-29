package org.chun.framework.annotations.handler;

import org.chun.framework.annotations.ProcessorAPI;

public interface IParamHandler {

  String transform(Object value, ProcessorAPI annotation);
}
