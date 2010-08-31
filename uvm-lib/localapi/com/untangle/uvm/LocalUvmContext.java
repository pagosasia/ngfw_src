/*
 * $HeadURL$
 * Copyright (c) 2003-2007 Untangle, Inc.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2,
 * as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package com.untangle.uvm;

import java.io.File;

import java.io.IOException;

import com.sleepycat.je.Environment;
import com.untangle.uvm.addrbook.RemoteAddressBook;
import com.untangle.uvm.benchmark.LocalBenchmarkManager;
import com.untangle.uvm.client.RemoteUvmContext;
import com.untangle.uvm.license.LocalLicenseManager;
import com.untangle.uvm.localapi.LocalIntfManager;
import com.untangle.uvm.logging.EventLogger;
import com.untangle.uvm.logging.RemoteLoggingManager;
import com.untangle.uvm.logging.SyslogManager;
import com.untangle.uvm.logging.LogEvent;
import com.untangle.uvm.message.LocalMessageManager;
import com.untangle.uvm.message.RemoteMessageManager;
import com.untangle.uvm.networking.LocalNetworkManager;
import com.untangle.uvm.node.LocalNodeManager;
import com.untangle.uvm.policy.LocalPolicyManager;
import com.untangle.uvm.portal.BasePortalManager;
import com.untangle.uvm.reports.RemoteReportingManager;
import com.untangle.uvm.security.RegistrationInfo;
import com.untangle.uvm.security.RemoteAdminManager;
import com.untangle.uvm.servlet.UploadManager;
import com.untangle.uvm.toolbox.ToolboxManager;
import com.untangle.uvm.toolbox.UpstreamManager;
import com.untangle.uvm.util.TransactionWork;
import com.untangle.uvm.vnet.MPipeManager;
import com.untangle.uvm.vnet.PipelineFoundry;
import com.untangle.uvm.LocalTomcatManager;

/**
 * Provides an interface to get all local UVM components from an UVM
 * instance.  This interface is accessible locally.
 *
 * @author <a href="mailto:amread@untangle.com">Aaron Read</a>
 * @version 1.0
 */
public interface LocalUvmContext extends RemoteUvmContext
{
    /**
     * Gets the current state of the UVM
     *
     * @return a <code>UvmState</code> enumerated value
     */
    UvmState state();

    /**
     * Get the <code>ToolboxManager</code> singleton.
     *
     * @return a <code>ToolboxManager</code> value
     */
    ToolboxManager toolboxManager();

    /**
     * Get the <code>NodeManager</code> singleton.
     *
     * @return a <code>NodeManager</code> value
     */
    LocalNodeManager localNodeManager();

    /**
     * Get the <code>RemoteLoggingManager</code> singleton.
     *
     * @return a <code>RemoteLoggingManager</code> value
     */
    RemoteLoggingManager loggingManager();

    SyslogManager syslogManager();


    /**
     * Get the <code>UpstreamManager</code> singleton.
     * This provides registration & control of upstream
     * services, normally those running on the local machine.
     *
     * @return the <code>UpstreamManager</code>
     */
    UpstreamManager upstreamManager();

    /**
     * Get the <code>PolicyManager</code> singleton.
     *
     * @return a <code>PolicyManager</code> value
     */
    LocalPolicyManager localPolicyManager();

    /**
     * Get the <code>RemoteAdminManager</code> singleton.
     *
     * @return a <code>RemoteAdminManager</code> value
     */
    RemoteAdminManager adminManager();

    /**
     * Get the <code>PortalManager</code> singleton.
     *
     * @return a <code>PortalManager</code> value
     */
    BasePortalManager portalManager();

    ArgonManager argonManager();

    LocalIntfManager localIntfManager();

    LocalNetworkManager localNetworkManager();

    RemoteReportingManager reportingManager();

    RemoteConnectivityTester getRemoteConnectivityTester();

    MailSender mailSender();

    /**
     * Get the AppServerManager singleton for this instance
     *
     * @return the singleton
     */
    LocalAppServerManager localAppServerManager();

    /**
     * Get the AddressBook singleton for this instance
     *
     * @return the singleton
     */
    RemoteAddressBook appAddressBook();

    /**
     * The BrandingManager manages customization of logo and
     * branding information.
     *
     * @return the RemoteBrandingManager.
     */
    RemoteBrandingManager brandingManager();

    /**
     * The OemManager manages OEM parameteres such as CompanyName
     *
     * @return the RemoteOemManager
     */
    RemoteOemManager oemManager();
    
    /**
     * Get the <code>RemoteSkinManager</code> singleton.
     *
     * @return the RemoteSkinManager.
     */
    RemoteSkinManager skinManager();

    /**
     * Get the <code>RemoteLanguageManager</code> singleton.
     *
     * @return the RemoteLanguageManager.
     */
    RemoteLanguageManager languageManager();

    RemoteMessageManager messageManager();

    LocalMessageManager localMessageManager();

    /**
     * The license manager.
     *
     * @return the LocalLicenseManager
     */
    LocalLicenseManager localLicenseManager() throws UvmException;

    /**
     * Once settings have been restored, and the UVM has been booted, call
     * into here to get the corresponding OS files rewritten.  This calls through
     * into callbacks in each manager, as appropriate.  All managers that write
     * OS config files must implement this.
     */
    void syncConfigFiles();

    Process exec(String cmd) throws IOException;
    Process exec(String[] cmd) throws IOException;
    Process exec(String[] cmd, String[] envp) throws IOException;
    Process exec(String[] cmd, String[] envp, File dir) throws IOException;

    void shutdown();

    /**
     * Reboots the Untangle Server. Note that this currently will not reboot a
     * dev box.
     */
    void rebootBox();

    /**
     * Shutdown the Untangle Server
     */
    void shutdownBox();

    /**
     * Force a Full Garbage Collection.
     */   
    void doFullGC();

    /**
     * Return the Version
     */
    String version();

    String getFullVersion();

    /**
     * Get the <code>MPipeManager</code> singleton.
     *
     * @return a <code>MPipeManager</code> value
     */
    MPipeManager mPipeManager();

    /**
     * The pipeline compiler.
     *
     * @return a <code>PipelineFoundry</code> value
     */
    PipelineFoundry pipelineFoundry();

    /**
     * Returns true if the product has been activated, false otherwise
     *
     * @return a <code>boolean</code> value
     */
    boolean isActivated();

    /**
     * Returns true if the product has been registered, false otherwise
     *
     * @return a <code>boolean</code> value
     */
    boolean isRegistered();

    /**
     * Return true if running in a development environment.
     *
     * @return a <code>boolean</code> true if in development.
     */
    boolean isDevel();

    /**
     * Return true if running in an Untangle Appliance (non-cd/iso install).
     *
     * @return a <code>boolean</code> true if in an untangle appliance.
     */
    boolean isUntangleAppliance();

    /**
     * Return true if running inside a Virtualized Platform (like VMWare)
     *
     * @return a <code>boolean</code> true if platform is running in a virtualized machine
     */
    boolean isInsideVM();

    /**
     * Returns the Untangle Installation type.  Currently there is
     * iso and u4w.
     * @return a <code>string</code> for the Untangle installation type.
     */
    String installationType();

    /**
     * Activates the Untangle Server using the given key and registration info.
     * Returns true if the activation succeeds, false otherwise (if the key is
     * bogus).
     *
     * @param key a <code>String</code> giving the key to be activated
     * under
     * @return a <code>boolean</code> true if the activation succeeded
     */
    boolean activate(String key, RegistrationInfo regInfo);

    boolean runTransaction(TransactionWork<?> tw);

    Thread newThread(Runnable runnable);

    Thread newThread(Runnable runnable, String name);

    EventLogger<LogEvent> eventLogger();

    void waitForStartup();

    LocalTomcatManager tomcatManager();
    
    CronJob makeCronJob(Period p, Runnable r);

    /**
     * Get the activation key.  <b>Don't be naughty and use this</b>
     *
     * @return the activation key.
     */
    String getActivationKey();

    /**
     * Create a backup which the client can save to a local disk.  The
     * returned bytes are for a .tar.gz file, so it is a good idea to
     * either use a ".tar.gz" extension so basic validation can be
     * performed for {@link #restore restore}.
     *
     * @return the byte[] contents of the backup.
     *
     * @exception IOException if something goes wrong (a lot can go wrong,
     *            but it is nothing the user did to cause this).
     */
    byte[] createBackup() throws IOException;

    /**
     * Restore from a previous {@link #createBackup backup}.
     *
     * @exception IOException something went wrong to prevent the
     *            restore (not the user's fault).
     * @exception IllegalArgumentException if the provided bytes do not seem
     *            to have come from a valid backup (is the user's fault).
     */
    void restoreBackup(byte[] backupFileBytes)
        throws IOException, IllegalArgumentException;

    /**
     * Restore from a previous {@link #createBackup backup} file.
     *
     * @exception IOException something went wrong to prevent the
     *            restore (not the user's fault).
     * @exception IllegalArgumentException if the provided bytes do not seem
     *            to have come from a valid backup (is the user's fault).
     */
    void restoreBackup(String fileName)
        throws IOException, IllegalArgumentException;

    /**
     * Loads premium functionality.
     *
     * @return true if premium functionality was loaded.
     */
    boolean loadRup();

    /**
     * Attempts to load the premium portal manager.
     */
    void loadPortalManager();

    /*
     * Loads a shared library (.so) into the UVM classloader.  This
     * is so a node dosen't load it into its own, which doesn't
     * work right.
     */
    void loadLibrary(String libname);

    Environment getBdbEnvironment();

    /**
     * Returns the UID of the server
     * Example: aaaa-bbbb-cccc-dddd
     */
    String getServerUID();
    
    UploadManager uploadManager();
    
    /**
     * Retrieve the jStore manager. See
     * http://wiki-private/mediawiki/index.php/JStore for more information.
     */
    SettingsManager settingsManager();
    
    /**
     * Retrieve the benchmark manager.
     */
    LocalBenchmarkManager localBenchmarkManager();
}
