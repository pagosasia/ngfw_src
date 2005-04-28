/*
 * Copyright (c) 2004, 2005 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.mvvm.tran;

import com.metavize.mvvm.MackageDesc;
import com.metavize.mvvm.security.Tid;
import com.metavize.mvvm.tapi.IPSessionDesc;
import net.sf.hibernate.Session;

/**
 * Holds the context for a Transform instance.
 *
 * @author <a href="mailto:amread@metavize.com">Aaron Read</a>
 * @version 1.0
 */
public interface TransformContext
{
    /**
     * Get the Tid for this instance.
     *
     * @return the transform id.
     */
    Tid getTid();

    /**
     * Get the transform for this context.
     *
     * @return this context's transform.
     */
    Transform transform();

    /**
     * Returns desc from mvvm-transform.xml.
     *
     * @return the TransformDesc.
     */
    TransformDesc getTransformDesc();

    /**
     * Returns the transform preferences.
     *
     * @return the TransformPreferences.
     */
    TransformPreferences getTransformPreferences();

    /**
     * Get the {@link MackageDesc} corresponding to this instance.
     *
     * @return the MackageDesc.
     */
    MackageDesc getMackageDesc();

    Session openSession();

    // XXX make private when/if we move all impls to engine
    ClassLoader getClassLoader();

    // call-through methods ---------------------------------------------------

    IPSessionDesc[] liveSessionDescs();

    TransformState getRunState();

    TransformStats getStats();
}
