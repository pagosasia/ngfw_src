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

package com.metavize.tran.clamphish;

import com.metavize.mvvm.tapi.TCPNewSessionRequest;
import com.metavize.mvvm.tapi.TCPSession;
import com.metavize.tran.mail.papi.MailExport;
import com.metavize.tran.mail.papi.MailExportFactory;
import com.metavize.tran.mail.papi.imap.ImapTokenStream;
import com.metavize.tran.mail.papi.safelist.SafelistTransformView;
import com.metavize.tran.spam.SpamIMAPConfig;
import com.metavize.tran.token.TokenHandler;
import com.metavize.tran.token.TokenHandlerFactory;
import org.apache.log4j.Logger;

public class PhishImapFactory implements TokenHandlerFactory
{
  private final Logger m_logger = Logger.getLogger(getClass());

  private final MailExport m_mailExport;
  private final ClamPhishTransform m_transform;
    private SafelistTransformView m_safelist;

    PhishImapFactory(ClamPhishTransform transform) {
      m_transform = transform;
      /* XXX RBS I don't know if this will work */
      m_mailExport = MailExportFactory.factory().getExport();
        m_safelist = m_mailExport.getSafelistTransformView();
    }

    // TokenHandlerFactory methods --------------------------------------------

    public TokenHandler tokenHandler(TCPSession session) {

      boolean inbound = session.isInbound();

      SpamIMAPConfig config = (!inbound)?
        m_transform.getSpamSettings().getIMAPInbound():
        m_transform.getSpamSettings().getIMAPOutbound();

      if(!config.getScan()) {
        m_logger.debug("Scanning disabled.  Return passthrough token handler");
        return new ImapTokenStream(session);
      }

      long timeout = (!inbound)?m_mailExport.getExportSettings().getImapInboundTimeout():
        m_mailExport.getExportSettings().getImapOutboundTimeout();

      return new ImapTokenStream(session,
          new PhishImapHandler(
            session,
            timeout,
            timeout,
            m_transform,
            config,
            m_safelist));
    }

    public void handleNewSessionRequest(TCPNewSessionRequest tsr)
    {
    }
}
