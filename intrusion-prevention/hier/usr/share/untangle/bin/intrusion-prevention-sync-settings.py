#!/usr/bin/python
"""
Synchronize settings:
-   From an initial state with the current signatures.
-   Between previous and current signatures.
-   From UI patches.
"""
import errno
import os
import getopt
import sys
import subprocess
import re
import json
import time
import uvm
from uvm import Manager
from uvm import Uvm

UNTANGLE_DIR = '%s/usr/lib/python%d.%d/dist-packages' % ( "@PREFIX@", sys.version_info[0], sys.version_info[1] )
if ( "@PREFIX@" != ''):
    sys.path.insert(0, UNTANGLE_DIR)
	
import intrusion_prevention

def usage():
    """
    Show usage
    """
    print("usage...")
    print("help\t\tusage")
    print("settings\tSettings configuration file name")
    print("conf\t\tSnort configuration file name")
    print("signatures\t\tSnort signature file name")
    print("app\t\tApp identifier")
    print("debug\t\tEnable debugging")

def get_signature_report(signatures, id):
    report = {
        "id": id,
        "log": 0,
        "block": 0,
        "disabled": 0
    }
    for signature in signatures.get_signatures().values():
        action = signature.get_action()
        if not action["log"] and not action["block"]:
            report["disabled"] += 1
        elif action["block"]:
            report["block"] += 1
        else:
            report["log"] += 1
    return report

def main(argv):
    """
    Main
    """
    global _debug
    _debug = False
    current_signatures_path = None
    previous_signatures_path = None
    settings_file_name = None
    status_file_name = None
    app_id = None
    patch_file_name = None
    settings_file_name = None
    export_mode = False
    summary = False
    summary_report = []
	
    try:
        opts, args = getopt.getopt(argv, "hsrpnace:d", ["help", "settings=", "signatures=", "previous_signatures=", "app_id=", "status=", "patch=", "export", "debug", "summary"] )
    except getopt.GetoptError:
        print("ERROR")
        usage()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ( "-h", "--help"):
            usage()
            sys.exit()
        elif opt in ( "-d", "--debug"):
            _debug = True
        elif opt in ( "-n", "--app_id"):
            app_id = arg
        elif opt in ( "-r", "--signatures"):
            current_signatures_path = arg
        elif opt in ( "-p", "--previous_signatures"):
            previous_signatures_path = arg
        elif opt in ( "-s", "--settings"):
            settings_file_name = arg
        elif opt in ( "-a", "--status"):
            status_file_name = arg
        elif opt in ( "-p", "--patch"):
            patch_file_name = arg
        elif opt in ( "-e", "--export"):
            export_mode = True
        elif opt in ( "--summary"):
            summary = True

    if app_id == None:
        print("Missing app_id")
        sys.exit(1)

    # if current_signatures_path == None:
    #     print("Missing signatures")
    #     sys.exit(1)

    # if settings_file_name == None:
    #     ## Must never write to actual location.
    #     print("Missing settings file name")
    #     sys.exit(1)

    if _debug == True:
        if current_signatures_path != None :
            print("current_signatures_path = " + current_signatures_path)
        if previous_signatures_path != None:
            print("previous_signatures_path = " + previous_signatures_path)
        if settings_file_name != None:
            print("settings_file_name = " + settings_file_name)
        print("app = " + app_id)
        print("_debug = ",  _debug)

    defaults = intrusion_prevention.IntrusionPreventionDefaults()
    defaults.load()

    patch = None
    if patch_file_name != None:
        patch = intrusion_prevention.IntrusionPreventionSettingsPatch()
        patch.load(patch_file_name)

    snort_conf = intrusion_prevention.SnortConf()

    #
    # Get previous rules
    #
    previous_snort_signatures = None
    if previous_signatures_path != None:
        previous_snort_signatures = intrusion_prevention.SnortSignatures( app_id, previous_signatures_path )
        previous_snort_signatures.load( True )
        previous_snort_signatures.update_categories(defaults, True)

    #
    # Get settings
    #
    settings = intrusion_prevention.IntrusionPreventionSettings( app_id )
    if settings.exists() == False:
        settings.create()
    else:
        settings.load()
        settings.convert()

    # Apply patch
    if patch != None:
        settings.set_patch(patch)

    # Update default rules (they may have changed from updates download)
    settings.update_rules(defaults.get_rules())

    #
    # Get current signatures.
    # Work is done on the current rule set as follows:
    # * Process modify/delete diffs from previous into current settings signature set (rare case where users modify signatures directly)
    # * Update current signatures with categories from defaults (combine otherwise uncategoriezed signatures into new categories)
    # * Apply settings rules to current signatures.
    # * Apply settings signatures to current signatures
    # * For all signatures that have not been qualified by rules or signature mods, disable.
    #
    current_snort_signatures = None
    if current_signatures_path != None:
        current_snort_signatures = intrusion_prevention.SnortSignatures( app_id, current_signatures_path )
        current_snort_signatures.load( True )

        if summary:
            summary_report.append( get_signature_report(current_snort_signatures, "initial") )

        ## new routine to get diffs between signature sets

        # Apply category overrides from defaults.
        # !!! will also need to be incorporated into UI signature downloads
        current_snort_signatures.update_categories(defaults.get_categories())

        # # !!! should be done from defaults
        # # True sets log=yes.  May not be good.
        # # current_snort_signatures.update_categories(defaults, True)

        # # settings.signatures.update_categories(defaults)


        # settings.get_rules().update_signatures(settings.get_signatures())

        # # if patch != None and "activeGroups" in patch.settings:
        # #     #
        # #     # Perform updates (e.g.,from signature distributions) preserving existing modifications.
        # #     #
        # #     settings.signatures.update( settings, snort_conf, current_snort_signatures, previous_snort_signatures, True )
        # # else:
        # #     # handle rules here?
        # #     settings.signatures.update( settings, snort_conf, current_snort_signatures, previous_snort_signatures )     

        ## should be in current signtures, pass in settings rules
        settings.apply_rules(current_snort_signatures)

        summary_report.append(get_signature_report(current_snort_signatures, "rules"))

        ### should be in current signatures.
        settings.disable_signatures(current_snort_signatures)

        summary_report.append(get_signature_report(current_snort_signatures, "final"))

        # apply signature overrides from settings

        # # profile_id = settings.settings["profileId"]
        # # if patch != None and "profileId" in patch.settings:
        # #     profile_id = patch.settings["profileId"]
        # # defaults_profile = defaults.get_profile(profile_id)

        # # if defaults_profile != None:
        # #     if patch != None:
        # #         settings.set_patch(patch, defaults_profile)
        # #     else:
        # #         #
        # #         # Disable unenabled signatures.
        # #         #
        # #         settings.get_signatures().filter_group(settings.settings["activeGroups"], defaults_profile)

    if export_mode:
        settings.save( settings_file_name, key=patch.settings.keys()[0] )
    else:
        settings.save(settings_file_name)
    
    if summary:
        for report in summary_report:
            print report
    
    sys.exit()

if __name__ == "__main__":
    main( sys.argv[1:] )
