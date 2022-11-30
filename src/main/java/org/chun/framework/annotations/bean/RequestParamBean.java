package org.chun.framework.annotations.bean;

import org.apache.logging.log4j.util.Strings;
import org.chun.framework.annotations.enums.ProcessorApiColumn;

import java.util.Arrays;

public class RequestParamBean implements BaseParamBean {

  int count;
  Node[] nodes;

  public RequestParamBean() {
    count = 0;
    nodes = new Node[0];
  }

  public RequestParamBean(String key, String value) {
    count = 1;
    nodes = new Node[]{new Node(key, value)};
  }

  @Override
  public String[] get(String key) {
    return Arrays.stream(nodes)
        .filter(node -> key.hashCode() == node.id)
        .findAny()
        .map(node -> node.value)
        .orElse(Strings.EMPTY_ARRAY);
  }

  @Override
  public void add(String key, String value) {
    if (containKey(key)) {
      int code = key.hashCode();
      for (Node node : nodes) {
        if (code == node.id) {
          node.add(value);
          break;
        }
      }
    } else {
      Node[] nodes = new Node[++count];
      for (int i = 0; i < count; i++) {
        nodes[i] = i == count - 1
            ? new Node(key, value)
            : this.nodes[i];
      }
      this.nodes = nodes;
    }
  }

  @Override
  public boolean isEmpty() {
    return count != 0;
  }

  @Override
  public boolean containKey(String key) {
    return !Arrays.equals(Strings.EMPTY_ARRAY, get(key));
  }

  public static RequestParamBuilder builder() {
    return new RequestParamBuilder();
  }

  public static class RequestParamBuilder {

    RequestParamBean bean;
    ProcessorApiColumn.Request column;

    RequestParamBuilder(){
      this.bean = new RequestParamBean();
    }

    public RequestParamBuilder classify(ProcessorApiColumn.Request requestColumn) {
      this.column = requestColumn;
      return this;
    }

    public RequestParamBuilder storage(String fieldName) {
      assert column != null;
      bean.add(fieldName, column.name());
      return this;
    }

    public RequestParamBean build() {
      return this.bean;
    }
  }
}
