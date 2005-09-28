/*
 * Copyright (c) 2005 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.mvvm.tran.firewall;

import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.metavize.mvvm.tapi.Protocol;
import com.metavize.mvvm.tapi.IPNewSessionRequest;
import com.metavize.mvvm.tapi.IPSessionDesc;

/**
 * A class for matching Traffic
 *   This is cannot be squashed into a RedirectRule because all of its elements are final. 
 *   This is a property which is not possible in hibernate objects.
 */
abstract class TrafficMatcher {
    private static final Logger logger = Logger.getLogger( TrafficMatcher.class );
        
    private final boolean isEnabled;
        
    /* Package protected so the InterfaceRedirect has access to these variables */
    final ProtocolMatcher protocol;

    final IPMatcher   srcAddress;
    final IPMatcher   dstAddress;

    final PortMatcher srcPort;
    final PortMatcher dstPort;

    protected final boolean isPingMatcher;

    // XXX For the future
    // TimeMatcher time;
    protected TrafficMatcher( boolean     isEnabled,  ProtocolMatcher protocol, 
                              IPMatcher   srcAddress, IPMatcher       dstAddress,
                              PortMatcher srcPort,    PortMatcher     dstPort )
    {
        this.isEnabled  = isEnabled;
        this.protocol   = protocol;
        this.srcAddress = srcAddress;
        this.dstAddress = dstAddress;

        /* Ports are ignored for PING sessions */
        if ( this.protocol.equals( ProtocolMatcher.MATCHER_PING )) {
            this.srcPort       = PortMatcher.MATCHER_PING;
            this.dstPort       = PortMatcher.MATCHER_PING;
            this.isPingMatcher = true;
        } else {
            this.srcPort       = srcPort;
            this.dstPort       = dstPort;
            this.isPingMatcher = false;
        }
    }

    protected TrafficMatcher( TrafficRule rule )
    {
        this( rule.isLive(), rule.getProtocol(),
              rule.getSrcAddress(), rule.getDstAddress(), rule.getSrcPort(), rule.getDstPort());
    }
    
    public boolean isEnabled()
    {
        return isEnabled;
    }
    
    public boolean isMatch( IPNewSessionRequest request, Protocol protocol )
    {
        boolean isMatch = 
            isEnabled && 
            isMatchProtocol( protocol ) &&
            isMatchAddress( request.clientAddr(), request.serverAddr()) &&
            isMatchPort( request.clientPort(), request.serverPort()) &&
            isTimeMatch();

        if ( isMatch )
            logger.debug( "Matched: " + request + " the session " );

        return isMatch;
    
    }

    public boolean isMatch( IPSessionDesc session, Protocol protocol )
    {
        return ( isEnabled && 
                 isMatchProtocol( protocol ) &&
                 isMatchAddress( session.clientAddr(), session.serverAddr()) &&
                 isMatchPort( session.clientPort(), session.serverPort()) &&
                 isTimeMatch());
    }
    
    public boolean isMatch( Protocol protocol, InetAddress srcAddress, InetAddress dstAddress,
                            int srcPort, int dstPort )
    {
        return ( isEnabled && 
                 isMatchProtocol( protocol ) &&
                 isMatchAddress( srcAddress, dstAddress) &&
                 isMatchPort( srcPort, dstPort ) &&
                 isTimeMatch());
    }


    public boolean isMatchProtocol( Protocol protocol ) 
    {
        return this.protocol.isMatch( protocol );
    }
        
    public boolean isMatchAddress( InetAddress src, InetAddress dst ) 
    {
        return ( this.srcAddress.isMatch( src ) && this.dstAddress.isMatch( dst ));
    }
    
    public boolean isMatchPort( int src, int dst )
    {
        return ( this.srcPort.isMatch( src ) && this.dstPort.isMatch( dst ));
    }
    
    /* Unused for now */
    public boolean isTimeMatch()
    {
        return true;
    }
}
