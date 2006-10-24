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

package com.metavize.mvvm.engine;

import com.metavize.mvvm.tapi.Fitting;
import com.metavize.mvvm.tapi.MPipe;

class MPipeFitting
{
    final MPipe mPipe;
    final Fitting fitting;
    final MPipe end;

    MPipeFitting(MPipe mPipe, Fitting fitting)
    {
        this.mPipe = mPipe;
        this.fitting = fitting;
        this.end = null;
    }

    MPipeFitting(MPipe mPipe, Fitting fitting, MPipe end)
    {
        this.mPipe = mPipe;
        this.fitting = fitting;
        this.end = end;
    }

    // Object methods ---------------------------------------------------------

    @Override
    public String toString()
    {
        return mPipe.toString();
    }
}
