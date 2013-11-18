package com.humdingr.client;

import java.util.ArrayList;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
class QueryData {
  private ArrayList<NameValuePair> queryList;

  protected QueryData() {
    queryList = new ArrayList<NameValuePair>();
  }

  protected void addQuery(String name, String value) {
    queryList.add(new NameValuePair(name, value));
  }

  protected String getQueryString() {
    if (queryList.size() == 0)
      return "";

    StringBuilder builder = new StringBuilder();
    for (NameValuePair mvp : queryList) {
      builder.append("&");
      builder.append(mvp.getName());
      builder.append("=");
      builder.append(mvp.getValue());
    }

    builder.replace(0,1,"?");

    return builder.toString();
  }

}
