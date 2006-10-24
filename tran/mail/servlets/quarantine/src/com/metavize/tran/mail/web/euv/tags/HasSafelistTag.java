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


/**
 * Includes/excludes body chunks if there
 * is (or could be) a Safelist for the current user
 */
public final class HasSafelistTag
  extends IfElseTag {

  private static final String HAS_ENTRY_KEY = "metavize.safelist.exists";  
  
  @Override
  protected boolean isConditionTrue() {
    Boolean ret = (Boolean) pageContext.getRequest().getAttribute(HAS_ENTRY_KEY);
    return ret==null?
      false:
      ret.booleanValue();
  }

  public static void setCurrent(ServletRequest req, boolean hasEntry) {
    req.setAttribute(HAS_ENTRY_KEY,
      hasEntry?Boolean.TRUE:Boolean.FALSE);
  }  
}
