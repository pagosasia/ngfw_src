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

package com.metavize.tran.nat;

import com.metavize.mvvm.logging.LogEvent;

class NatAttachment
{    
    /* True if this session uses a port that must be released */
    /* Port to release, 0, if a port should not be released */
    private int releasePort = 0;

    private LogEvent eventToLog = null;

    /* True if this session has created a session that must be removed from the session
     * manager.  (Presently the session manager only manages ftp sessions) */
    private boolean isManagedSession = false;
    
    NatAttachment()
    {
    }

    boolean isManagedSession()
    {
        return this.isManagedSession;
    }

    void isManagedSession( boolean isManagedSession )
    {
        this.isManagedSession = isManagedSession;
    }

    int releasePort()
    {
        return this.releasePort;
    }

    void releasePort( int releasePort )
    {
        this.releasePort = releasePort;
    }

    LogEvent eventToLog()
    {
        return this.eventToLog;
    }

    void eventToLog(LogEvent eventToLog)
    {
        this.eventToLog = eventToLog;
    }
}
