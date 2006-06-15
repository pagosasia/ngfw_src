/*
 * Copyright (c) 2006 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.tran.portal.browser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metavize.mvvm.portal.PortalLogin;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

public class SmbLogin extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException
    {
        PortalLogin pl = (PortalLogin)req.getUserPrincipal();

        String d = req.getParameter("domain");
        String u = req.getParameter("username");
        String p = req.getParameter("password");

        System.out.println("D: " + d + " U: " + u + " P: " + p);

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(d, u, p);
        try {
            PrintWriter writer = resp.getWriter();

            try {
                UniAddress ua = UniAddress.getByName(d);
                SmbSession.logon(ua, auth);
                pl.addNtlmAuth(auth);
                writer.println("<?xml version=\"1.0\" ?>");
                writer.println("<auth status='success' principal='" + auth + "'/>");
            } catch (UnknownHostException exn) {
                writer.println("<?xml version=\"1.0\" ?>");
                writer.println("<auth status='failure' principal='" + auth + "'/>");
            } catch(SmbAuthException sae) {
                sae.printStackTrace();
                writer.println("<?xml version=\"1.0\" ?>");
                writer.println("<auth status='failure' principal='" + auth + "'/>");
            } catch(SmbException se) {
                writer.println("<?xml version=\"1.0\" ?>");
                writer.println("<auth status='failure' principal='" + auth + "'/>");
            }
        } catch (IOException exn) {
            throw new ServletException(exn);
        }
    }
}
