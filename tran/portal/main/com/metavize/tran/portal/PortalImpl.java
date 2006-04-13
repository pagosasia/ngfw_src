/*
 * Copyright (c) 2005, 2006 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.tran.portal;

import com.metavize.mvvm.MvvmContextFactory;
import com.metavize.mvvm.tapi.AbstractTransform;
import com.metavize.mvvm.tapi.PipeSpec;
import com.metavize.mvvm.tran.TransformException;
import org.apache.log4j.Logger;

public class PortalImpl extends AbstractTransform implements Portal
{
    private final Logger logger = Logger.getLogger(PortalImpl.class);

    private final PipeSpec[] pipeSpecs = new PipeSpec[0];

    // constructors -----------------------------------------------------------

    public PortalImpl()
    {
        logger.debug("<init>");
    }

    private void deployWebAppIfRequired(Logger logger) {
        if (MvvmContextFactory.context().appServerManager().loadWebApp("/browser", "browser")) {
            logger.debug("Deployed Browser web app");
        } else {
            logger.error("Unable to deploy Browser web app");
        }

        if (MvvmContextFactory.context().appServerManager().loadWebApp("/proxy", "proxy")) {
            logger.debug("Deployed Proxy web app");
        } else {
            logger.error("Unable to deploy Proxy web app");
        }
    }

    private void unDeployWebAppIfRequired(Logger logger) {
        if (MvvmContextFactory.context().appServerManager().unloadWebApp("/browser")) {
            logger.debug("Unloaded Browser web app");
        } else {
            logger.error("Unable to unload Browser web app");
        }

        if (MvvmContextFactory.context().appServerManager().unloadWebApp("/proxy")) {
            logger.debug("Unloaded Proxy web app");
        } else {
            logger.error("Unable to unload Proxy web app");
        }
    }

    // Portal methods ---------------------------------------------------------

    // Transform methods ------------------------------------------------------

    public void reconfigure()
    {
    }

    protected void initializeSettings() { }

    @Override
        protected void preDestroy() throws TransformException {
        super.preDestroy();
        logger.debug("preDestroy()");
        unDeployWebAppIfRequired(logger);
    }

    protected void postInit(String[] args)
    {
        logger.debug("postInit()");

        deployWebAppIfRequired(logger);
    }

    // AbstractTransform methods ----------------------------------------------

    @Override
        protected PipeSpec[] getPipeSpecs()
    {
        return pipeSpecs;
    }

    // XXX soon to be deprecated ----------------------------------------------

    public Object getSettings() { return null; }

    public void setSettings(Object settings) { }
}
