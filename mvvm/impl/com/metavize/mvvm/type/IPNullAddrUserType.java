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

package com.metavize.mvvm.type;

import com.metavize.mvvm.tran.IPNullAddr;

import java.net.UnknownHostException;

public class IPNullAddrUserType extends StringBasedUserType
{
    public Class returnedClass()
    {
        return IPNullAddr.class;
    }

    protected String userTypeToString( Object v )
    {
        return ((IPNullAddr)v).toString();
    }

    public Object createUserType( String val ) throws Exception
    {
        return IPNullAddr.parse( val );
    }
}
