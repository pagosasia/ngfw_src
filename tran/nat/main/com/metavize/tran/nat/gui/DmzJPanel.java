/*
 * Copyright (c) 2003, 2004, 2005 Metavize Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id: NatEventHandler.java 194 2005-04-06 19:13:55Z rbscott $
 */

package com.metavize.tran.nat.gui;

import java.awt.*;

import com.metavize.tran.nat.*;
import com.metavize.gui.util.Util;
import com.metavize.mvvm.tran.IPaddr;

/**
 *
 * @author  inieves
 */
public class DmzJPanel extends javax.swing.JPanel {
    
    private Color INVALID_COLOR = Color.PINK;
    private Color BACKGROUND_COLOR = new Color(224, 224, 224);
    

    public DmzJPanel() {
        initComponents();
    }
    
    
    
    public void refresh(Object settings) throws Exception {
        if(!(settings instanceof NatSettings)){
            this.setBackground(INVALID_COLOR);
            return;
        }
        else{
            this.setBackground(BACKGROUND_COLOR);
        }
        
        boolean isValid = true;
        
        NatSettings natSettings = (NatSettings) settings;
        boolean dmzEnabled;
        String dmzTargetAddress;
        
        // ENABLED ///////////
        try{
            dmzEnabled = natSettings.getDmzEnabled();
            this.setDmzEnabledDependency(dmzEnabled);
            if( dmzEnabled )
                dmzEnabledJRadioButton.setSelected(true);
            else
                dmzDisabledJRadioButton.setSelected(true);
            dmzEnabledJRadioButton.setBackground( BACKGROUND_COLOR );
            dmzDisabledJRadioButton.setBackground( BACKGROUND_COLOR );
        }
        catch(Exception e){
            dmzEnabledJRadioButton.setBackground( INVALID_COLOR );
            dmzDisabledJRadioButton.setBackground( INVALID_COLOR );
            isValid = false;
        }
        
        // TARGET ADDRESS //////
        try{
            dmzTargetAddress = natSettings.getDmzAddress().toString();
            targetAddressIPaddrJTextField.setText( dmzTargetAddress );
            targetAddressIPaddrJTextField.setBackground( Color.WHITE );
        }
        catch(Exception e){
            targetAddressIPaddrJTextField.setBackground( INVALID_COLOR );
            isValid = false;
        }
        
        if(!isValid)
            throw new Exception();
        
    }

    
    
    public void save(Object settings) throws Exception {
        if(!(settings instanceof NatSettings)){
            this.setBackground(INVALID_COLOR);
            return;
        }
        else{
            this.setBackground(BACKGROUND_COLOR);
        }
        
        
        NatSettings natSettings = (NatSettings) settings;
        boolean dmzEnabled;
        IPaddr dmzTargetAddress = null;
        
        // ENABLED ///////////
        dmzEnabled = dmzEnabledJRadioButton.isSelected();
        if( dmzEnabledJRadioButton.isSelected() ^ dmzDisabledJRadioButton.isSelected() ){
            dmzEnabledJRadioButton.setBackground( BACKGROUND_COLOR );
            dmzDisabledJRadioButton.setBackground( BACKGROUND_COLOR );
        }
        else{
            dmzEnabledJRadioButton.setBackground( INVALID_COLOR );
            dmzDisabledJRadioButton.setBackground( INVALID_COLOR );
            throw new Exception("The DMZ cannot be Enabled and Disabled at the same time.");
        }
        
        // INTERNAL ADDRESS //////
        if(dmzEnabled){
            try{
                dmzTargetAddress = IPaddr.parse( targetAddressIPaddrJTextField.getText() );
                targetAddressIPaddrJTextField.setBackground( Color.WHITE );
            }
            catch(Exception e){
                targetAddressIPaddrJTextField.setBackground( INVALID_COLOR );
                throw new Exception("The Target IP Address must be a valid IP address.");
            }
        }        
        
        // SAVE THE VALUES ////////////////////////////////////
        natSettings.setDmzEnabled( dmzEnabled );
        natSettings.setDmzAddress( dmzTargetAddress );
        
    }
    
    
    

    
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        enabledButtonGroup = new javax.swing.ButtonGroup();
        explanationJPanel = new javax.swing.JPanel();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        dmzEnabledJRadioButton = new javax.swing.JRadioButton();
        dmzDisabledJRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        externalRemoteJPanel = new javax.swing.JPanel();
        restrictIPJPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        targetAddressIPaddrJTextField = new javax.swing.JTextField();
        jTextArea3 = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        explanationJPanel.setLayout(new java.awt.GridBagLayout());

        explanationJPanel.setBorder(new javax.swing.border.TitledBorder(null, "DMZ Usage", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 16)));
        jTextArea2.setEditable(false);
        jTextArea2.setLineWrap(true);
        jTextArea2.setText("DMZ allows you to direct all incoming traffic to a specific computer on your network.  This is typically used for webservers or other servers which must be accesible from outside your secured network.  If you disable DMZ, no incoming traffic will enter your network.  (Note:  If you use Redirect, you can allow certain incoming traffic to enter your network regardless of if a DMZ is being used.)");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        explanationJPanel.add(jTextArea2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        enabledButtonGroup.add(dmzEnabledJRadioButton);
        dmzEnabledJRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        dmzEnabledJRadioButton.setText("Enabled");
        dmzEnabledJRadioButton.setFocusPainted(false);
        dmzEnabledJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dmzEnabledJRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(dmzEnabledJRadioButton, gridBagConstraints);

        enabledButtonGroup.add(dmzDisabledJRadioButton);
        dmzDisabledJRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        dmzDisabledJRadioButton.setText("Disabled");
        dmzDisabledJRadioButton.setFocusPainted(false);
        dmzDisabledJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dmzDisabledJRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(dmzDisabledJRadioButton, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("DMZ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        explanationJPanel.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        add(explanationJPanel, gridBagConstraints);

        externalRemoteJPanel.setLayout(new java.awt.GridBagLayout());

        externalRemoteJPanel.setBorder(new javax.swing.border.TitledBorder(null, "Target Address", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 16)));
        restrictIPJPanel.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setText("Target IP Address: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        restrictIPJPanel.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        restrictIPJPanel.add(targetAddressIPaddrJTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        externalRemoteJPanel.add(restrictIPJPanel, gridBagConstraints);

        jTextArea3.setEditable(false);
        jTextArea3.setLineWrap(true);
        jTextArea3.setText("The target address is the address of the computer inside your network that will receive all incoming traffic when DMZ is enabled.");
        jTextArea3.setWrapStyleWord(true);
        jTextArea3.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        externalRemoteJPanel.add(jTextArea3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(externalRemoteJPanel, gridBagConstraints);

    }//GEN-END:initComponents

    private void dmzDisabledJRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dmzDisabledJRadioButtonActionPerformed
        this.setDmzEnabledDependency(false);
    }//GEN-LAST:event_dmzDisabledJRadioButtonActionPerformed

    private void dmzEnabledJRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dmzEnabledJRadioButtonActionPerformed
        this.setDmzEnabledDependency(true);
    }//GEN-LAST:event_dmzEnabledJRadioButtonActionPerformed
    
    private void setDmzEnabledDependency(boolean enabled){
        targetAddressIPaddrJTextField.setEnabled(enabled);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JRadioButton dmzDisabledJRadioButton;
    public javax.swing.JRadioButton dmzEnabledJRadioButton;
    private javax.swing.ButtonGroup enabledButtonGroup;
    private javax.swing.JPanel explanationJPanel;
    private javax.swing.JPanel externalRemoteJPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JPanel restrictIPJPanel;
    public javax.swing.JTextField targetAddressIPaddrJTextField;
    // End of variables declaration//GEN-END:variables
    
}
