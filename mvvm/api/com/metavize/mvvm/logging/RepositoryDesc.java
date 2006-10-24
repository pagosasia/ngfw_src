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

package com.metavize.mvvm.logging;

import java.io.Serializable;

public class RepositoryDesc implements Serializable
{
    private final String name;

    public RepositoryDesc(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
