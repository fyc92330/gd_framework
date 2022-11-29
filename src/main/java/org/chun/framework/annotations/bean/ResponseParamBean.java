package org.chun.framework.annotations.bean;

import org.apache.logging.log4j.util.Strings;
import org.chun.framework.annotations.enums.ProcessorApiColumn;

import java.util.Arrays;

public class ResponseParamBean implements BaseParamBean {

  int count;
  Node[] nodes;

  public ResponseParamBean() {
    count = 0;
  }

  public ResponseParamBean(String key, String value) {
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

  public static ResponseParamBean.ResponseParamBuilder builder() {
    return new ResponseParamBean.ResponseParamBuilder();
  }

  public static class ResponseParamBuilder {

    ResponseParamBean bean;
    ProcessorApiColumn.Response column;

    public ResponseParamBean.ResponseParamBuilder classify(ProcessorApiColumn.Response responseColumn) {
      this.column = responseColumn;
      return this;
    }

    public ResponseParamBean.ResponseParamBuilder storage(String fieldName) {
      assert column != null;
      bean.add(fieldName, column.name());
      return this;
    }

    public ResponseParamBean build() {
      return this.bean;
    }
  }
}
