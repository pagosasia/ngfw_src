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

package com.metavize.tran.protofilter;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 * Settings for the ProtoFilter transform.
 *
 * @author <a href="mailto:amread@metavize.com">Aaron Read</a>
 * @version 1.0
 */
@Entity
@Table(name="tr_protofilter_settings", schema="settings")
public class ProtoFilterSettings implements java.io.Serializable
{
    private static final long serialVersionUID = 266434887860496780L;

    private Long id;
    private Tid tid;
    private int byteLimit  = 2048;
    private int chunkLimit = 10;
    private String unknownString = "[unknown]";
    private boolean stripZeros = true;
    private List<ProtoFilterPattern> patterns = null;

    /**
     * Hibernate constructor.
     */
    private ProtoFilterSettings() {}

    /**
     * Real constructor
     */
    public ProtoFilterSettings(Tid tid)
    {
        this.tid = tid;
        this.patterns = new ArrayList<ProtoFilterPattern>();
    }

    @Id
    @Column(name="settings_id")
    @GeneratedValue
    private Long getId() { return id; }
    private void setId(Long id) { this.id = id; }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="tid", nullable=false)
    public Tid getTid() { return tid; }
    public void setTid(Tid tid) { this.tid = tid; }

    public int getByteLimit() { return this.byteLimit; }
    public void setByteLimit(int i) { this.byteLimit = i; }

    public int getChunkLimit() { return this.chunkLimit; }
    public void setChunkLimit(int i) { this.chunkLimit = i; }

    public String getUnknownString() { return this.unknownString; }
    public void setUnknownString(String s) { this.unknownString = s; }

    public boolean isStripZeros() { return this.stripZeros; }
    public void setStripZeros(boolean b) { this.stripZeros = b; }

    /**
     * Pattern rules.
     *
     * @return the list of Patterns
     */
    @OneToMany(fetch=FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinColumn(name="settings_id")
    @IndexColumn(name="position")
    public List<ProtoFilterPattern> getPatterns() { return patterns; }
    public void setPatterns(List<ProtoFilterPattern> s) { this.patterns = s; }
}
