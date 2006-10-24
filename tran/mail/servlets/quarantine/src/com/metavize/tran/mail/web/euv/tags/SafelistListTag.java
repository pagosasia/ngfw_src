/*
 * Copyright (c) 2003-2006 Untangle Networks, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */
package com.metavize.tran.mail.web.euv.tags;

import javax.servlet.ServletRequest;
import java.util.Iterator;
import java.util.Arrays;


/**
 * Tag which is used to iterate over the contents of a Safelist
 * <br><br>
 * It then sets-up an OddEvenTag and a MessageTag for each iteration.
 */
public final class SafelistListTag
  extends IteratingTag<String> {

  private static final String SL_KEY = "metavize.safelist.contents.";
  
  @Override
  protected Iterator<String> createIterator() {
    String[] list = getCurrentList(pageContext.getRequest());

//    System.out.println("***DEBUG*** Creating iterator, array: " +
//      (list==null?"null":Integer.toString(list.length)) + "");
    
    if(list == null || list.length == 0) {
      return null;
    }
    return Arrays.asList(list).iterator();
  }
  @Override
  protected void setCurrent(String s) {
    SafelistEntryTag.setCurrent(pageContext, s);
  }

  public static final void setCurrentList(ServletRequest request,
    String[] list) {
    request.setAttribute(SL_KEY, list);
  }
  public static final void clearCurrentList(ServletRequest request) {
    request.removeAttribute(SL_KEY);
  }

  /**
   * Returns null if there are no safelist entries
   * - sort string entries, within list, according to natural, ascending order
   */
  static String[] getCurrentList(ServletRequest request) {
      Object allSLEntries = request.getAttribute(SL_KEY);
      if (null != allSLEntries) {
          Arrays.sort((String[]) allSLEntries);
      }
      return (String[]) allSLEntries;
  }

  static boolean hasCurrentList(ServletRequest request) {
    String[] list = getCurrentList(request);
    return list != null && list.length > 0;
  }
}
