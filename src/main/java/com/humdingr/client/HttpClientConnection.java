package com.humdingr.client;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
public class HttpClientConnection {
  private String host;
  private int port;
  private String path;
  private QueryData queryData;
  private HeaderData headerData;
  private HttpURLConnection connection;
  private boolean useSSL;

  private HttpClientConnection() {

  }

  /**
   * Builds a connection with default port and no SSL
   * @param host [host name of server (do not include http://)]
   */
  public HttpClientConnection(String host) {
    setupConnection(host, -1, false);
  }

  /**
   * Builds a connection with default port
   * @param host [host name of server (do not include http://)]
   * @param useSSL [Whether to use ssl connection or not]
   */
  public HttpClientConnection(String host, boolean useSSL) {
    setupConnection(host, -1, useSSL);
  }

  /**
   * Builds a connection without ssl
   * @param host [host name of server (do not include http://)]
   * @param port [port of server]
   */
  public HttpClientConnection(String host, int port) {
    if (port < 0) {
      throw new IllegalArgumentException("Port can not be a negative value");
    }
    setupConnection(host, port, false);
  }

  /**
   * Builds a connection with given settings
   * @param host [host name of server (do not include http://)]
   * @param port [port of server]
   * @param useSSL [Whether to use ssl or not]
   */
  public HttpClientConnection(String host, int port, boolean useSSL) {
    if (port < 0) {
      throw new IllegalArgumentException("Port can not be a negative value");
    }
    setupConnection(host, port, useSSL);
  }

  private void setupConnection(String host, int port, boolean useSSL) {
    this.host = host;
    this.port = port;
    this.path = "/";
    this.useSSL = useSSL;
    queryData = new QueryData();
    headerData = new HeaderData();
  }

  /**
   * Sets the path of the url to the given string
   * @param path [full path of file]
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Adds the string to the current path
   * @param path
   */
  public void addPath(String path) {
    if (!path.startsWith("/"))
      path = "/" + path;
    this.path = this.path + path;
  }

  /**
   * Adds the name value pair as a addQuery parameter
   * Queries will be preserved in the order you add them
   * @param name [name of query]
   * @param value [value of query]
   */
  public void addQuery(String name, String value) {
    queryData.addQuery(name, value);
  }

  /**
   * Adds the name value pair to the header of the http call
   * @param name [name of header]
   * @param value [value of header]
   */
  public void addHeader(String name, String value) {
    headerData.addHeader(name, value);
  }

  /**
   * Sends the given body to the server as a POST and returns the response
   * @param body [Full string of what to send as post-body]
   * @return [body of response]
   * @throws MalformedURLException [url is not valid]
   * @throws ServerException [server did not return a 200 OK]
   */
  public String doPost(String body) throws MalformedURLException, ServerException {
    return send(body, "POST", true, true);
  }

  /**
   * Converts Object to XML, sends it as the body, and returns reponse as given class
   * @param o [Object to be converted to XML
   * @param c [Class which response will be converted too
   * @return [Object matching given class type
   */
  public <T> T doPostWithXML(Object o, Class<T> c) throws JAXBException, MalformedURLException, ServerException {
    String xml = marshalToXml(o);
    String response = doPost(xml);
    return unmarshalXml(c, response);
  }

  /**
   * Sends the URL to the server as a GET and returns the response
   * @return [body of response]
   * @throws MalformedURLException [url is not valid]
   * @throws ServerException [server did not return a 200 OK]
   */
  public String doGet() throws MalformedURLException, ServerException {
    return send("", "GET", true, false);
  }

  /**
   * Sends the URL to the server as a GET and converts response to given class
   * @param <T> [class of expected object]
   * @return [object returned from xml conversion]
   */
  public <T> T doGetConvertFromXML(Class<T> c) throws MalformedURLException, ServerException, JAXBException {
    String response = doGet();
    return unmarshalXml(c, response);

  }

  /**
   * Sends the URL to the server as a PUT and return the reponse body
   * @param body [request body to send to server]
   * @return [body of response]
   * @throws MalformedURLException [if url is invalid]
   * @throws ServerException [if server does not return a 200]
   */
  public String doPut(String body) throws MalformedURLException, ServerException {
    return send(body, "PUT", true, true);
  }

  public String doPut() throws MalformedURLException, ServerException {
    return send("", "PUT", true, false);
  }

  public String doDelete() throws MalformedURLException, ServerException {
    return send("", "DELETE", true, false);
  }

  /**
   * Sends the given method to the server. This allows for any HTTP method
   * @param body [body to be sent to server. Does not matter if no output]
   * @param method [HTTP Method to be used i.e. POST, DELETE, PUT etc.]
   * @param input [whether data will be expected from the server]
   * @param output [whether data will be sent (in the body) to the server]
   * @return [body of response]
   * @throws MalformedURLException [URL is not valid]
   * @throws ServerException [Server did not send a 200 OK]
   */
  public String send(String body, String method, boolean input, boolean output) throws MalformedURLException, ServerException {
    int retryCount = 5;
    while (retryCount > 0) {
      try {
        return internalSend(body, method, input, output);
      } catch (ServerException e) {

        if (e.getStatusCode() == 503) {
          retryCount--;
          try {
            Thread.sleep(1000);
          }
          catch (InterruptedException e1) {
            continue;
          }

        } else {
          throw e;
        }
      }
    }
    throw new ServerException("Retry attempts are failing. Call is unsuccessful", 503);
  }

  private String internalSend(String body, String method, boolean input, boolean output) throws ServerException, MalformedURLException {
    URL url = getURL();
    if (useSSL) {
      HttpURLConnection connection = null;
      String response = "";
      int status = 0;

      try {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        int b = body.getBytes().length;
        connection.setRequestProperty("Content-Length", "" + b);
        headerData.setHeaders(connection);

        connection.setDoInput(input);
        connection.setDoOutput(output);

        if (output) {
          sendRequest(connection, body);
        }

        status = connection.getResponseCode();

        if (input) {
          response = getResponse(connection);
        }

        if (status != 200) {
          throw new ServerException("Server threw a status code " + status + " with response: " + response, status);
        }
      }
      catch (IOException e) {
        throw new ServerException("Connection was unsuccessful. Server threw status code: " + status, status);
      } finally {
        connection.disconnect();
      }

      return response;
    } else {
      HttpsURLConnection connection = null;
      String response = "";
      int status = 0;

      try {
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        int b = body.getBytes().length;
        connection.setRequestProperty("Content-Length", "" + b);
        headerData.setHeaders(connection);

        connection.setDoInput(input);
        connection.setDoOutput(output);

        if (output) {
          sendRequest(connection, body);
        }

        status = connection.getResponseCode();

        if (input) {
          response = getResponse(connection);
        }

        if (status != 200) {
          throw new ServerException("Server threw a status code " + status + " with response: " + response, status);
        }
      }
      catch (IOException e) {
        throw new ServerException("Connection was unsuccessful. Server threw status code: " + status, status);
      } finally {
        connection.disconnect();
      }

      return response;
    }
  }

  public <T> T unmarshalXml(Class<T> c, String xml) throws JAXBException {
    T t = null;

    JAXBContext context = JAXBContext.newInstance(c);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    t = (T) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

    return t;
  }

  public String marshalToXml(Object o) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(o.getClass());
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    StringWriter writer = new StringWriter();
    marshaller.marshal(o, writer);

    return writer.toString();
  }

  private URL getURL() throws MalformedURLException {
    StringBuilder builder = new StringBuilder();
    builder.append("http");
    if (useSSL) builder.append("s");
    builder.append("://");
    builder.append(host);
    if (port >= 0) builder.append(":" + port);
    builder.append(path);
    builder.append(queryData.getQueryString());

    return new URL(builder.toString());
  }

  private void sendRequest(HttpURLConnection connection, String body) throws IOException {
    DataOutputStream wr = new DataOutputStream (connection.getOutputStream());

    wr.writeBytes(body);
    wr.flush();
    wr.close();
  }

  private String getResponse(HttpURLConnection connection) throws IOException {
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuffer response = new StringBuffer();
    while((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    return response.toString();
  }

  //Todo: make all calls return a "Response" and then all responses can convert to string/object/etc
      //todo: exceptions have status code directly in them, so they dont have to be parsed.
}
