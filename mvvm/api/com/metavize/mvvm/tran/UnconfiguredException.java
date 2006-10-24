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
package com.metavize.mvvm.tran;

public class UnconfiguredException extends TransformStartException
{
    public UnconfiguredException( String message )
    {
        super( message );
    }

    public UnconfiguredException( Exception e )
    {
        super( e );
    }

    public UnconfiguredException( String message, Exception e )
    {
        super( message, e );
    }

}
