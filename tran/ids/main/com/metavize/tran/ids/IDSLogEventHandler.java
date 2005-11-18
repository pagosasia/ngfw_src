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

package com.metavize.tran.ids;

import com.metavize.mvvm.logging.EventHandler;
import com.metavize.mvvm.logging.RepositoryDesc;

public class IDSLogEventHandler implements EventHandler<IDSLogEvent>
{
    private static final RepositoryDesc FILTER_DESC = new RepositoryDesc("All Events");

    private static final String WARM_QUERY = "FROM IDSLogEvent evt "
        + "WHERE evt.pipelineEndpoints.policy = :policy "
        + "ORDER BY evt.timeStamp";

    // constructors -----------------------------------------------------------

    IDSLogEventHandler() { }

    // EventCache methods -----------------------------------------------------

    public RepositoryDesc getRepositoryDesc()
    {
        return FILTER_DESC;
    }

    public String[] getQueries()
    {
        return new String[] { WARM_QUERY };
    }

    public boolean accept(IDSLogEvent e)
    {
        return true;
    }
}
