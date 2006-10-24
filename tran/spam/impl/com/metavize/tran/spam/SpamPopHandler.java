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

package com.metavize.tran.spam;

import java.io.File;
import java.io.IOException;

import com.metavize.mvvm.tapi.TCPSession;
import com.metavize.mvvm.tran.Transform;
import com.metavize.tran.mail.papi.MIMEMessageT;
import com.metavize.tran.mail.papi.MailExport;
import com.metavize.tran.mail.papi.MailTransformSettings;
import com.metavize.tran.mail.papi.WrappedMessageGenerator;
import com.metavize.tran.mail.papi.pop.PopStateMachine;
import com.metavize.tran.mail.papi.safelist.SafelistTransformView;
import com.metavize.tran.mime.HeaderParseException;
import com.metavize.tran.mime.LCString;
import com.metavize.tran.mime.MIMEMessage;
import com.metavize.tran.token.Token;
import com.metavize.tran.token.TokenException;
import com.metavize.tran.token.TokenResult;
import com.metavize.tran.util.TempFileFactory;
import org.apache.log4j.Logger;

public class SpamPopHandler extends PopStateMachine
{
    private final Logger logger = Logger.getLogger(getClass());

    /* no block counter */
    private final static int PASS_COUNTER = Transform.GENERIC_0_COUNTER;
    private final static int MARK_COUNTER = Transform.GENERIC_2_COUNTER;

    private final SpamImpl zTransform;
    private final SpamScanner zScanner;
    private final String zVendorName;

    private final SafelistTransformView zSLTransformView;
    private final SpamPOPConfig zConfig;
    private final SpamMessageAction zMsgAction;
    private final boolean bScan;
    private final int strength;
    private final int giveUpSize;

    private WrappedMessageGenerator zWMsgGenerator;

    // constructors -----------------------------------------------------------

    protected SpamPopHandler(TCPSession session, SpamImpl transform, MailExport zMExport)
    {
        super(session);

        zTransform = transform;
        zScanner = transform.getScanner();
        zVendorName = zScanner.getVendorName();

        zSLTransformView = zMExport.getSafelistTransformView();

        MailTransformSettings zMTSettings = zMExport.getExportSettings();

        if (!session.isInbound()) {
            zConfig = transform.getSpamSettings().getPOPInbound();
            lTimeout = zMTSettings.getPopInboundTimeout();
        } else {
            zConfig = transform.getSpamSettings().getPOPOutbound();
            lTimeout = zMTSettings.getPopOutboundTimeout();
        }
        bScan = zConfig.getScan();
        zMsgAction = zConfig.getMsgAction();
        strength = zConfig.getStrength();
        giveUpSize = zConfig.getMsgSizeLimit();
        zWMsgGenerator = zConfig.getMessageGenerator();
        //logger.debug("scan: " + bScan + ", message action: " + zMsgAction + ", timeout: " + lTimeout);
    }

    // PopStateMachine methods -----------------------------------------------

    protected TokenResult scanMessage() throws TokenException
    {
        if (true == bScan &&
            giveUpSize >= zMsgFile.length() &&
            false == zSLTransformView.isSafelisted(null, zMMessage.getMMHeaders().getFrom(), null)) {

            SpamReport zReport;

            if (null != (zReport = scanFile(zMsgFile)) &&
                SpamMessageAction.MARK == zMsgAction) {
                zTransform.incrementCount(MARK_COUNTER);

                /* wrap spam message and rebuild message token */
                MIMEMessage zWMMessage = zWMsgGenerator.wrap(zMMessage, zReport);
                try {
                    zMsgFile = zWMMessage.toFile(new TempFileFactory(getPipeline()));

                    zMMessageT = new MIMEMessageT(zMsgFile);
                    zMMessageT.setMIMEMessage(zWMMessage);

                    /* do not dispose original message
                     * (wrapped message references original message)
                     */
                } catch (IOException exn) {
                    /* we'll reuse original message */
                    //XXXX - need to dispose wrapped message?
                    throw new TokenException("cannot create wrapped message file after marking message as spam: " + exn);
                }
            } else {
                zTransform.incrementCount(PASS_COUNTER);
            }
        } //else {
            //logger.debug("scan is not enabled");
        //}

        return new TokenResult(new Token[] { zMMessageT }, null);
    }

    private SpamReport scanFile(File zFile) throws TokenException
    {
        try {
            SpamReport zReport = zScanner.scanFile(zFile, strength/10.0f);
            SpamLogEvent event = new SpamLogEvent(zMsgInfo,
                                                  zReport.getScore(),
                                                  zReport.isSpam(),
                                                  zReport.isSpam() ? zMsgAction : SpamMessageAction.PASS,
                                                  zVendorName);
            zTransform.log(event);

            try {
                zMMessage.getMMHeaders().removeHeaderFields(new LCString(zConfig.getHeaderName()));
                zMMessage.getMMHeaders().addHeaderField(zConfig.getHeaderName(),
                  zConfig.getHeaderValue(zReport.isSpam()));
            }
            catch (HeaderParseException exn) {
                /* we'll reuse original message */
                throw new TokenException("cannot add spam report header to scanned message/mime part: " + exn);
            }

            if (true == zReport.isSpam()) {
                return zReport;
            }
            /* else not spam - do nothing */

            return null;
        } catch (Exception exn) { // Should never happen
            /* we'll reuse original message */
            throw new TokenException("cannot scan message/mime part file: " + exn);
        }
    }
}
