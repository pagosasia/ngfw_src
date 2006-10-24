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

package com.metavize.mvvm.portal;

import java.util.List;

public interface RemotePortalManager
{
    RemoteApplicationManager applicationManager();

    PortalSettings getPortalSettings();

    void setPortalSettings(PortalSettings settings);

    List<PortalLogin> getActiveLogins();

    void forceLogout(PortalLogin login);
}
