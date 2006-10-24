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
package com.metavize.tran.boxbackup;

import com.metavize.mvvm.tran.Transform;
import com.metavize.mvvm.logging.EventManager;

public interface BoxBackup extends Transform
{
    BoxBackupSettings getBoxBackupSettings();
    void setBoxBackupSettings(BoxBackupSettings settings);
    EventManager<BoxBackupEvent> getEventManager();
}
