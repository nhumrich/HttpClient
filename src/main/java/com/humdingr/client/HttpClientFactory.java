package com.humdingr.client;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
public class HttpClientFactory {
  private String host;
  private int port;
  private boolean useSSL;
  private String defaultPath = "";

  /**
   * Builds a factory for connections with default port and no SSL
   * @param host [host name of server (do not include http://)]
   */
  public HttpClientFactory(String host) {
    setupFactory(host, 80, false);
  }

  /**
   * Builds a factory for connections without ssl
   * @param host [host name of server (do not include http://)]
   * @param port [port of server]
   */
  public HttpClientFactory(String host, int port) {
    if (port < 0) {
      throw new IllegalArgumentException("Port can not be a negative value");
    }
    setupFactory(host, port, false);
  }

  /**
   * Builds a factory for connections with default port
   * @param host [host name of server (do not include http://)]
   * @param useSSL [Whether to use ssl connection or not]
   */
  public HttpClientFactory(String host, boolean useSSL) {
    setupFactory(host, 80, useSSL);
  }

  /**
   * Builds a factory for connections with given settings
   * @param host [host name of server (do not include http://)]
   * @param port [port of server]
   * @param useSSL [Whether to use ssl or not]
   */
  public HttpClientFactory(String host, int port, boolean useSSL) {
    if (port < 0) {
      throw new IllegalArgumentException("Port can not be a negative value");
    }
    setupFactory(host, port, useSSL);
  }

  /**
   * Adds the path automatically to all new connections
   * @param path [path to be used as default]
   */
  public void addDefaultPathContext(String path) {
    this.defaultPath = path;
  }

  private void setupFactory(String host, int port, boolean useSSL) {
    this.host = host;
    this.port = port;
    this.useSSL = useSSL;
  }

  /**
   * Returns a new connection for sending http requests
   * @return HttpClientConnection
   */
  public HttpClientConnection getNewConnection() {
    HttpClientConnection connection = new HttpClientConnection(host, port, useSSL);
    connection.setPath(defaultPath);
    return connection;
  }

}
