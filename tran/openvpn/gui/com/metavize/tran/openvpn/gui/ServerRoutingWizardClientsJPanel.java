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

package com.metavize.tran.openvpn.gui;

import com.metavize.mvvm.security.*;
import com.metavize.gui.widgets.wizard.*;
import com.metavize.gui.widgets.editTable.*;
import com.metavize.gui.util.*;
import javax.swing.SwingUtilities;
import javax.swing.ComboBoxModel;

import java.awt.Color;

import java.util.*;

import com.metavize.tran.openvpn.*;
import com.metavize.mvvm.tran.*;

public class ServerRoutingWizardClientsJPanel extends MWizardPageJPanel {

    private VpnTransform vpnTransform;
	
    public ServerRoutingWizardClientsJPanel(VpnTransform vpnTransform) {
	this.vpnTransform = vpnTransform;
	initComponents();
	((MEditTableJPanel)configClientToSiteJPanel).setShowDetailJPanelEnabled(false);
	((MEditTableJPanel)configClientToSiteJPanel).setInstantRemove(true);
	((MEditTableJPanel)configClientToSiteJPanel).setFillJButtonEnabled(false);
    }

    protected boolean enteringForwards(){
	try{
	    SwingUtilities.invokeAndWait( new Runnable(){ public void run() {
		try{		
		    GroupList groupList = vpnTransform.getAddressGroups();
		    ((TableModelClientToSite)((MEditTableJPanel)configClientToSiteJPanel).getTableModel()).updateGroupModel( (List<VpnGroup>) groupList.getGroupList() );
		    ((MEditTableJPanel)configClientToSiteJPanel).getTableModel().clearAllRows();
		    
		}	    
		catch(Exception e){ Util.handleExceptionNoRestart("Error updating group list", e);}
	    }});
	}
	catch(Exception e){ Util.handleExceptionNoRestart("Error updating group list", e);}
	return true;
    }
    
    Vector<Vector> filteredDataVector;
    List<VpnClient> elemList;
    Exception exception;
    
    public void doSave(Object settings, boolean validateOnly) throws Exception {

	SwingUtilities.invokeAndWait( new Runnable(){ public void run() {
	    ((MEditTableJPanel)configClientToSiteJPanel).getJTable().getCellEditor().stopCellEditing();
	    ((MEditTableJPanel)configClientToSiteJPanel).getJTable().clearSelection();
	    filteredDataVector = ((MEditTableJPanel)configClientToSiteJPanel).getTableModel().getFilteredDataVector();
	    
	    exception = null;

	    elemList = new ArrayList<VpnClient>(filteredDataVector.size());
	    VpnClient newElem = null;
	    int rowIndex = 0;
	
	    for( Vector rowVector : filteredDataVector ){
		rowIndex++;
		newElem = new VpnClient();
		newElem.setDistributeClient(false);
		newElem.setLive( (Boolean) rowVector.elementAt(2) );
		newElem.setName( (String) rowVector.elementAt(3) );
		newElem.setGroup( (VpnGroup) ((ComboBoxModel) rowVector.elementAt(4)).getSelectedItem() );
		newElem.setDescription( (String) rowVector.elementAt(7) );
		elemList.add(newElem);
	    }
	}});

        if( exception != null)
            throw exception;
	        
        if( !validateOnly ){
	    try{
		ServerRoutingWizard.getInfiniteProgressJComponent().startLater("Adding VPN Clients...");
		ClientList clientList = new ClientList(elemList);
		vpnTransform.setClients(clientList);
		ServerRoutingWizard.getInfiniteProgressJComponent().stopLater(1500l);
	    }
	    catch(Exception e){
		ServerRoutingWizard.getInfiniteProgressJComponent().stopLater(-1l);
		throw e;
	    }
        }
    }
    
    
        private void initComponents() {//GEN-BEGIN:initComponents
                jLabel2 = new javax.swing.JLabel();
                configClientToSiteJPanel = new ConfigClientToSiteJPanel();
                jLabel3 = new javax.swing.JLabel();

                setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                setOpaque(false);
                jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel2.setText("<html><b>Optionally add VPN Clients.</b><br>VPN Clients can remotely login to the VPN, and access any exported hosts or networks, and visa versa.</html>");
                add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 400, -1));

                add(configClientToSiteJPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 465, 210));

                jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/metavize/tran/openvpn/gui/ProductShot.png")));
                jLabel3.setEnabled(false);
                add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-130, 230, -1, -1));

        }//GEN-END:initComponents
    
    
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JPanel configClientToSiteJPanel;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        // End of variables declaration//GEN-END:variables
    
}
