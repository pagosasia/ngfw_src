/**
 * $Id: RouterEventHandler.java 36443 2013-11-19 23:32:09Z dmorris $
 */
package com.untangle.node.router;

import java.net.InetAddress;
import java.util.Random;

import org.apache.log4j.Logger;

import com.untangle.uvm.UvmContextFactory;
import com.untangle.uvm.logging.LogEvent;
import com.untangle.uvm.vnet.AbstractEventHandler;
import com.untangle.uvm.vnet.IPNewSessionRequest;
import com.untangle.uvm.vnet.NodeSession;
import com.untangle.uvm.vnet.TCPNewSessionRequest;
import com.untangle.uvm.vnet.UDPNewSessionRequest;
import com.untangle.uvm.vnet.Protocol;

class RouterEventHandler extends AbstractEventHandler
{
    private final Logger logger = Logger.getLogger(RouterEventHandler.class);

    /* Router Node */
    private final RouterImpl node;

    private Random rand;
    
    public static final short PROTO_TCP = 6;

    /* Setup  */
    RouterEventHandler( RouterImpl node )
    {
        super(node);
        this.rand = new Random();
        this.node = node;
    }

    public void handleTCPNewSessionRequest( TCPNewSessionRequest request )
    {
        InetAddress origClientAddr = request.getOrigClientAddr();
        InetAddress newClientAddr = request.getNewClientAddr();
        InetAddress origServerAddr = request.getOrigServerAddr();
        InetAddress newServerAddr = request.getNewServerAddr();
        int origClientPort = request.getOrigClientPort();
        int newClientPort  = request.getNewClientPort();
        int origServerPort = request.getOrigServerPort();
        int newServerPort  = request.getNewServerPort();

        if ( logger.isDebugEnabled()) {
            logger.debug( "pre-translation : " + origClientAddr + ":" + origClientPort +  " -> " + origServerAddr + ":" + origServerPort );
            logger.debug( "post-translation: " + newClientAddr + ":" + newClientPort +  " -> " + newServerAddr + ":" + newServerPort );
        }

        /**
         * The sole purpose of the router event handler is to rewrite the source port of
         * TCP connections
         *
         * The masquerading is handled by the kernel, however the kernel considers the
         * following two connections to be unique:
         *
         * 1.2.3.4:1234 -> 10.0.0.1:1234
         * 1.2.3.4:1234 -> 192.168.1.100:1234
         *
         * Because the kernel can differentiate the two even while both sessions
         * use the same port on 1.2.3.4.
         *
         * However, because we will be non-local binding we cant have both sessions
         * bind sockets to 1.2.3.4:1234
         *
         * As such, we must use or own port assignment scheme for TCP.
         * (UDP doesn't matter since we don't bind to sockets)
         */
        
        // if doing NAT, then rewrite the source port
        if ( !origClientAddr.equals(newClientAddr) ||
             !origServerAddr.equals(newServerAddr) ||
             (origClientPort != newClientPort) ||
             (origServerPort != newServerPort) ){

            if( request.getProtocol() == PROTO_TCP && !origClientAddr.equals(newClientAddr)){
                int port = getFreePort( newClientAddr );

                if ( logger.isDebugEnabled()) {
                    logger.debug( "Mangling server-side client port from " + origClientPort + " to " + port );
                }
                request.setNewClientPort( port );
            }
        }

        request.release();
    }

    public void handleUDPNewSessionRequest( UDPNewSessionRequest request )
    {
        request.release();
        return;
    }

    private int getFreePort( InetAddress addr )
    {
        int port = rand.nextInt(40000) + 10000;

        boolean used = UvmContextFactory.context().netcapManager().isTcpPortUsed( addr, port );

        for ( int i = 0; used && i < 100 ; i++ ) {
            port++;
            used = UvmContextFactory.context().netcapManager().isTcpPortUsed( addr, port );
        }
        
        if ( used ) {
            logger.warn("Unable to find a free port: " + port);
            // just use it anyway
        }

        return port;
    }
}
