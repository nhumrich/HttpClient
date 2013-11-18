package com.humdingr.client;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
public class ServerException extends Exception {
  private int statusCode;

  public ServerException(String message) {
    super(message);
  }

  public ServerException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

}
