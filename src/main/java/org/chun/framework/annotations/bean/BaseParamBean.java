package org.chun.framework.annotations.bean;

public interface BaseParamBean {

  String[] get(String key);

  void add(String key, String value);

  boolean isEmpty();

  boolean containKey(String key);

  class Node {
    int id;
    String key;
    String[] value;
    int size;

    Node(String key, String value) {
      assert key != null;
      assert value != null;

      this.key = key;
      this.id = key.hashCode();
      this.value = new String[]{value};
      this.size = 1;
    }

    protected void add(String value) {
      assert value != null;

      String[] handlers = new String[++size];
      for (int i = 0; i < size; i++) {
        handlers[i] = i == size - 1
            ? value
            : this.value[i];
      }
      this.value = handlers;
    }
  }
}
