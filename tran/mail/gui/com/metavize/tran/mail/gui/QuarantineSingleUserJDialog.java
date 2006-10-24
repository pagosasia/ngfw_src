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

package com.metavize.tran.mail.gui;

import com.metavize.gui.widgets.dialogs.*;
import com.metavize.gui.widgets.editTable.*;
import com.metavize.gui.util.*;

import com.metavize.mvvm.tran.*;
import com.metavize.mvvm.security.Tid;

import com.metavize.tran.mail.papi.*;
import com.metavize.tran.mail.papi.quarantine.*;

import java.lang.reflect.Constructor;

import java.awt.Dimension;
import java.awt.Dialog;
import java.util.*;
import javax.swing.table.*;
import javax.swing.*;



public class QuarantineSingleUserJDialog extends MConfigJDialog {

    private static final String NAME_ALL_ACCOUNTS    = "Email Quarantine Details for: ";

    private String account;
        
    public QuarantineSingleUserJDialog(Dialog topLevelDialog, MailTransformCompoundSettings mailTransformCompoundSettings, String account) {
	super(topLevelDialog);
	compoundSettings = mailTransformCompoundSettings;
        this.account = account;
	INSTANCE = this;
    }

    protected Dimension getMinSize(){
	return new Dimension(640, 550);
    }

    private static QuarantineSingleUserJDialog INSTANCE;
    public static QuarantineSingleUserJDialog instance(){ return INSTANCE; }
    
    protected void generateGui(){
        this.setTitle(NAME_ALL_ACCOUNTS + account);
	reloadJButton.setVisible(false);
        saveJButton.setVisible(false);

        // ALL ACCOUNTS //////
	QuarantineSingleUserJPanel quarantineSingleUserJPanel = new QuarantineSingleUserJPanel(account);
	addRefreshable(NAME_ALL_ACCOUNTS, quarantineSingleUserJPanel);
        addTab(NAME_ALL_ACCOUNTS + account, null, quarantineSingleUserJPanel);
    }

    protected void refreshAll() throws Exception {
	super.refreshAll();
	((MailTransformCompoundSettings)compoundSettings).loadInboxIndex(account);
    }
}
