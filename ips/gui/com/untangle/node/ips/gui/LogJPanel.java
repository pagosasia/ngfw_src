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

package com.untangle.node.ips.gui;

import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;

import com.untangle.gui.node.*;
import com.untangle.gui.util.*;
import com.untangle.gui.widgets.editTable.*;
import com.untangle.node.ips.*;
import com.untangle.uvm.logging.EventManager;
import com.untangle.uvm.logging.EventRepository;
import com.untangle.uvm.logging.RepositoryDesc;
import com.untangle.uvm.node.Node;
import com.untangle.uvm.node.PipelineEndpoints;

public class LogJPanel extends MLogTableJPanel {

    private static final String BLOCKED_EVENTS_STRING = "Packet blocked events";

    public LogJPanel(Node node, MNodeControlsJPanel mNodeControlsJPanel){
        super(node, mNodeControlsJPanel);

        final IpsNode ips = (IpsNode)node;

        setTableModel(new LogTableModel());

        EventManager<IpsLogEvent> eventManager = ips.getEventManager();
        for (RepositoryDesc fd : eventManager.getRepositoryDescs()) {
            queryJComboBox.addItem(fd.getName());
        }
    }

    protected void refreshSettings(){
        IpsNode ips = (IpsNode)logNode;
        EventManager<IpsLogEvent> em = ips.getEventManager();
        EventRepository<IpsLogEvent> ef = em.getRepository((String)queryJComboBox.getSelectedItem());
        settings = ef.getEvents();
    }

    class LogTableModel extends MSortedTableModel<Object>{

        public TableColumnModel getTableColumnModel(){
            DefaultTableColumnModel tableColumnModel = new DefaultTableColumnModel();
            //                                 #   min  rsz    edit   remv   desc   typ               def
            addTableColumn( tableColumnModel,  0,  150, true,  false, false, false, Date.class,   null, "timestamp" );
            addTableColumn( tableColumnModel,  1,  55,  true,  false, false, false, String.class, null, "action" );
            addTableColumn( tableColumnModel,  2,  165, true,  false, false, false, IPPortString.class, null, sc.html("client") );
            addTableColumn( tableColumnModel,  3,  150, true,  false, false, false, String.class, null, sc.html("reason for<br>action") );
            addTableColumn( tableColumnModel,  4,  165, true,  false, false, false, IPPortString.class, null, "server" );
            return tableColumnModel;
        }

        public void generateSettings(Object settings, Vector<Vector> tableVector, boolean validateOnly) throws Exception {}


        public Vector<Vector> generateRows(Object settings){
            List<IpsLogEvent> logList = (List<IpsLogEvent>) settings;
            Vector<Vector> allEvents = new Vector<Vector>(logList.size());
            Vector event;

            for( IpsLogEvent log : logList ){
                PipelineEndpoints pe = log.getPipelineEndpoints();

                event = new Vector(6);
                event.add( log.getTimeStamp() );
                event.add( log.isBlocked() ? "block" : "pass" );
                event.add( null == pe ? new IPPortString() : new IPPortString(pe.getCClientAddr(), pe.getCClientPort()) );
                event.add( "#" + log.getRuleSid() + ": " + log.getMessage() );
                event.add( null == pe ? new IPPortString() : new IPPortString(pe.getSServerAddr(), pe.getSServerPort()) );
                allEvents.add( event );
            }

            return allEvents;
        }
    }
}
