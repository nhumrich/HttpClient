package com.humdingr.client;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
class NameValuePair {
  private String name;
  private String value;

  protected NameValuePair(String name, String value) {
    this.name = name;
    this.value = value;
  }

  protected String getName() {
    return name;
  }

  protected String getValue() {
    return value;
  }
}
