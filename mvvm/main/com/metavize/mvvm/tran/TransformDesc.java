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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.metavize.mvvm.security.Tid;

/**
 * Transform settings and properties.
 *
 * @author <a href="mailto:amread@metavize.com">Aaron Read</a>
 * @version 1.0
 */
public class TransformDesc implements Serializable
{
    private static final long serialVersionUID = 4361064848435100972L;

    private final Tid tid;
    private final String name;

    private final String className;
    private final String guiClassName;

    private final boolean casing;
    private final Set<String> parents;
    private final boolean singleInstance;

    private final String displayName;

    private final int tcpClientReadBufferSize = 8192;
    private final int tcpServerReadBufferSize = 8192;
    private final int udpMaxPacketSize = 16384;


    public TransformDesc(Tid tid, String name, String className,
                         String guiClassName, boolean casing,
                         Set parents, boolean singleInstance,
                         String displayName)
    {
        this.tid = tid;
        this.name = name;
        this.className = className;
        this.guiClassName = guiClassName;
        this.casing = casing;
        this.parents = Collections.unmodifiableSet(new HashSet(parents));
        this.singleInstance = singleInstance;
        this.displayName = displayName;
    }

    // accessors --------------------------------------------------------------

    /**
     * Transform id.
     *
     * @return tid for this instance.
     */
    public Tid getTid()
    {
        return tid;
    }

    /**
     * Internal name of the transform.
     *
     * @return the transform's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Name of the main transform Class.
     *
     * @return transform class name.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Only a single instance may be initialized in the system.
     *
     * @return true if the transform is a casing.
     */
    public boolean isCasing()
    {
        return casing;
    }

    /**
     * The parent transform, usually a casing.
     *
     * @return the parent transform, null if transform has no parent.
     */
    public Set<String> getParents()
    {
        return parents;
    }

    /**
     * Only a single instance may be initialized in the system.
     *
     * @return true if only a single instance may be loaded.
     */
    public boolean isSingleInstance()
    {
        return singleInstance;
    }

    /**
     * TCP client read BufferSize is between 1 and 65536 bytes.
     *
     * @return the TCP client read bufferSize.
     */
    public int getTcpClientReadBufferSize()
    {
        return tcpClientReadBufferSize;
    }

    /**
     * TCP server read bufferSize is between 1 and 65536 bytes.
     *
     * @return the TCP server read bufferSize.
     */
    public int getTcpServerReadBufferSize()
    {
        return tcpServerReadBufferSize;
    }

    /**
     * UDP max packet size, between 1 and 65536 bytes, defaults to 16384.
     *
     * @return UDP max packet size.
     */
    public int getUdpMaxPacketSize()
    {
        return udpMaxPacketSize;
    }

    /**
     * The name of the transform, for display purposes.
     *
     * @return display name.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * The class name of the GUI module.
     *
     * @return class name of GUI component.
     */
    public String getGuiClassName()
    {
        return guiClassName;
    }

    // Object methods ---------------------------------------------------------

    /**
     * Equality based on the business key (tid).
     *
     * @param o the object to compare to.
     * @return true if equal.
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof TransformDesc)) {
            return false;
        }

        TransformDesc td = (TransformDesc)o;

        return tid.equals(td.getTid());
    }
}
