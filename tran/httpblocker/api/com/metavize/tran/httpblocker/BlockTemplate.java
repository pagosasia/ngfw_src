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

package com.metavize.tran.httpblocker;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.metavize.mvvm.security.Tid;

/**
 * Message to be displayed when a message is blocked.
 *
 * @author <a href="mailto:amread@metavize.com">Aaron Read</a>
 * @version 1.0
 */
@Entity
@Table(name="tr_httpblk_template", schema="settings")
public class BlockTemplate implements Serializable
{
    private static final long serialVersionUID = -2176543704833470091L;

    // XXX someone, make this pretty
    private static final String BLOCK_TEMPLATE
        = "<HTML><HEAD>"
        + "<TITLE>403 Forbidden</TITLE>"
        + "</HEAD><BODY>"
        + "<center><b>%s</b></center>"
        + "<p>This site blocked because of inappropriate content</p>"
        + "<p>Host: %s</p>"
        + "<p>URI: %s</p>"
        + "<p>Category: %s</p>"
        + "<p>Please contact %s</p>"
        + "<HR>"
        + "<ADDRESS>Metavize EdgeGuard</ADDRESS>"
        + "</BODY></HTML>";

    private Long id;
    private String header = "Metavize Content Filter";
    private String contact = "your network administrator";

    // constructor ------------------------------------------------------------

    /**
     * Hibernate constructor.
     */
    public BlockTemplate() { }

    public BlockTemplate(String header, String contact)
    {
        this.header = header;
        this.contact = contact;
    }

    // business methods ------------------------------------------------------

    public String render(String host, URI uri, String category)
    {
        return String.format(BLOCK_TEMPLATE, header, host, uri, category,
                             contact);
    }

    // accessors --------------------------------------------------------------

    @Id
    @Column(name="message_id")
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Customizable banner on the block page.
     *
     * @return the header.
     */
    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    /**
     * Contact information.
     */
    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }
}
