package com.humdingr.client;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
public class HttpClientConnectionTest {
  @Test
  public void testDoPost() throws Exception {
    HttpClientConnection connection = new HttpClientConnection("posttestserver.com");
    connection.setPath("/post.php");
    connection.addHeader("Content-Type", "text/html");
    connection.addHeader("Content-Language", "en-US");
    connection.addHeader("Accept", "text/*");

    String response = connection.doPost("Hello!");

    Assert.assertTrue(response.endsWith("Post body was 6 chars long.\r"));
  }

  @Test
  public void testDoGet() throws Exception {
    HttpClientConnection connection = new HttpClientConnection("bing.com", false);
    connection.setPath("/search");
    connection.addQuery("q", "test");
    connection.addHeader("Content-Type", "text/html");
    connection.addHeader("Content-Language", "en-US");
    connection.addHeader("Accept", "text/*");

    String response = connection.doGet();
    Assert.assertTrue(response.contains("<!DOCTYPE html"));
  }

  @Test
  public void test404() throws Exception {
    //http://www.google.com/test?hello=true
    HttpClientConnection connection = new HttpClientConnection("google.com", 80, false);
    connection.setPath("/test");
    connection.addQuery("hello", "true");
    connection.addHeader("Content-Type", "text/html");
    connection.addHeader("Content-Language", "en-US");
    connection.addHeader("Accept", "text/*");

    try {
      connection.doGet();
      Assert.fail("Should have thrown 404");
    } catch (ServerException e) {
      Assert.assertEquals("Connection was unsuccessful. Server threw status code: 404", e.getMessage());
    }
  }

  @Test
  public void testSSL() throws Exception {
    HttpClientConnection connection = new HttpClientConnection("google.com", true);
    connection.addHeader("Content-Type", "text/html");
    connection.addHeader("Content-Language", "en-US");
    connection.addHeader("Accept", "text/*");

    String response = connection.doGet();
    Assert.assertTrue(response.contains("<!doctype html>"));
  }
}
