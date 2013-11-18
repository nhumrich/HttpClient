package com.humdingr.client;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
class HeaderData {
  private ArrayList<NameValuePair> headers;

  protected HeaderData() {
    headers = new ArrayList<NameValuePair>();
  }

  protected void addHeader(String name, String value) {
    headers.add(new NameValuePair(name, value));
  }

  protected void setHeaders(HttpURLConnection connection) {
    for (NameValuePair nvp: headers) {
      connection.setRequestProperty(nvp.getName(), nvp.getValue());
    }
  }
}
