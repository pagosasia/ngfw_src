/*
 * $HeadURL$
 * Copyright (c) 2003-2007 Untangle, Inc. 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.untangle.node.shield.reports;

import java.sql.*;

import com.untangle.uvm.reporting.BaseSummarizer;
import com.untangle.uvm.reporting.Util;
import org.apache.log4j.Logger;

public class ShieldSummarizer extends BaseSummarizer {

    private final Logger logger = Logger.getLogger(getClass());

    public ShieldSummarizer() { }

    public String getSummaryHtml(Connection conn, Timestamp startDate, Timestamp endDate)
    {
        long sessionsAccepted = 0;
        long sessionsLimited = 0;
        long sessionsRejected = 0;
        long sessionsDropped = 0;

        long loadRelaxed = 0l;
        long loadLax = 0l;
        long loadTight = 0l;
        long loadClosed = 0l;

        try {
            String sql;
            PreparedStatement ps;
            ResultSet rs;

            sql = "SELECT SUM(accepted), SUM(limited), SUM(rejected), SUM(dropped), SUM(relaxed), SUM(lax), SUM(tight), SUM(closed) FROM n_shield_statistic_evt WHERE time_stamp >= ? AND time_stamp < ?";
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            rs = ps.executeQuery();
            rs.first();
            sessionsAccepted = rs.getLong(1);
            sessionsLimited = rs.getLong(2);
            sessionsRejected = rs.getLong(3);
            sessionsDropped = rs.getLong(4);
            loadRelaxed = rs.getLong(5);
            loadLax = rs.getLong(6);
            loadTight = rs.getLong(7);
            loadClosed = rs.getLong(8);
            rs.close();
            ps.close();

        } catch (SQLException exn) {
            logger.warn("could not summarize", exn);
        }

        long sessionsRequested = sessionsAccepted + sessionsLimited + sessionsRejected + sessionsDropped;
        addEntry("Resource requests", Util.trimNumber("",sessionsRequested));
        addEntry("&nbsp;&nbsp;&nbsp;Accepted", Util.trimNumber("",sessionsAccepted), Util.percentNumber(sessionsAccepted,sessionsRequested));
        addEntry("&nbsp;&nbsp;&nbsp;Limited", Util.trimNumber("",sessionsLimited), Util.percentNumber(sessionsLimited,sessionsRequested));
        addEntry("&nbsp;&nbsp;&nbsp;Dropped", Util.trimNumber("",sessionsDropped), Util.percentNumber(sessionsDropped,sessionsRequested));
        addEntry("&nbsp;&nbsp;&nbsp;Rejected", Util.trimNumber("",sessionsRejected), Util.percentNumber(sessionsRejected,sessionsRequested));

        addEntry("&nbsp;", "&nbsp;");

        long loadTotal = loadRelaxed + loadLax + loadTight + loadClosed;
        addEntry("Resource allocation selectivity", "");
        addEntry("&nbsp;&nbsp;&nbsp;Normal", "", Util.percentNumber(loadRelaxed, loadTotal));
        addEntry("&nbsp;&nbsp;&nbsp;Increased", "", Util.percentNumber(loadLax, loadTotal));
        addEntry("&nbsp;&nbsp;&nbsp;High", "", Util.percentNumber(loadTight, loadTotal));
        addEntry("&nbsp;&nbsp;&nbsp;Defensive", "", Util.percentNumber(loadClosed, loadTotal));

        // XXXX
        String nodeName = "Attack Blocker";

        return summarizeEntries(nodeName);
    }
}
