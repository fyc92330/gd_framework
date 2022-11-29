package org.chun.framework.demo.model.base;

import java.io.Serializable;

public class BaseModel<T> implements Serializable, Cloneable {

  private Class<?> baseClass;

  @Override
  public BaseModel<T> clone() throws CloneNotSupportedException {
    BaseModel clone = (BaseModel) super.clone();
    return clone;
  }

  public Class<?> getBaseClass() {
    return baseClass;
  }

  public void setBaseClass(Class<?> baseClass) {
    this.baseClass = baseClass;
  }
}
