/*
 * $HeadURL$
 * Copyright (c) 2003-2007 Untangle, Inc. 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.untangle.uvm.engine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.untangle.uvm.policy.Policy;
import com.untangle.uvm.security.NodeId;

/**
 * Internal state for NodeManagerImpl.
 *
 * @author <a href="mailto:amread@untangle.com">Aaron Read</a>
 * @version 1.0
 */
@Entity
@Table(name="u_node_manager_state", schema="settings")
class NodeManagerState
{
    private Long id;
    private Long lastNodeId = 0L;

    NodeManagerState() { }

    @SuppressWarnings("unused")
	@Id
    @Column(name="id")
    @GeneratedValue
    private Long getId()
    {
        return id;
    }

    @SuppressWarnings("unused")
	private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Last tid assigned.
     *
     * @return last assigned tid.
     */
    @Column(name="last_tid", nullable=false)
    Long getLastNodeId()
    {
        return lastNodeId;
    }

    void setLastNodeId(Long lastNodeId)
    {
        this.lastNodeId = lastNodeId;
    }

    /**
     * Get the next NodeId.
     *
     * @return a <code>Long</code> value
     */
    NodeId nextNodeId(Policy policy, String nodeName)
    {
        return new NodeId(++lastNodeId, policy, nodeName);
    }
}
