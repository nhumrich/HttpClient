package com.humdingr.client;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @autor: Nick Humrich
 * @date: 8/27/13
 */
public class QueryDataTest {
  @Test
  public void testGetQueryString() throws Exception {
    QueryData data = new QueryData();
    data.addQuery("name", "bob");
    data.addQuery("friend", "fred");
    data.addQuery("title", "awesome");

    String query = data.getQueryString();
    Assert.assertEquals("?name=bob&friend=fred&title=awesome", query);
  }

  @Test
  public void testGetQueryEmpty() throws Exception {
    QueryData data = new QueryData();

    String query = data.getQueryString();
    Assert.assertEquals("", query);
  }


}
