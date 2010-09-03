if (!Ung.hasResource["Ung.BaseWebFilter"]) {
    Ung.hasResource["Ung.BaseWebFilter"] = true;
    Ung.NodeWin.registerClassName('untangle-base-webfilter', 'Ung.BaseWebFilter');

    Ung.BaseWebFilter = Ext.extend(Ung.NodeWin, {
        gridExceptions : null,
        gridEventLog : null,
        // called when the component is rendered
        initComponent : function() {
            // keep initial base settings
            this.initialBaseSettings = Ung.Util.clone(this.getBaseSettings());

            this.buildBlockLists();
            this.buildPassLists();
            this.buildEventLog();
        this.buildUnblockEventLog();
            // builds the tab panel with the tabs
            this.buildTabPanel([this.panelBlockLists, this.panelPassLists, 
                this.gridEventLog, this.gridUnblockEventLog]);
            Ung.BaseWebFilter.superclass.initComponent.call(this);
        },
        // Block Lists Panel
        buildBlockLists : function() {
            this.panelBlockLists = new Ext.Panel({
                name : 'Block Lists',
                helpSource : 'block_lists',
                // private fields
                winBlacklistCategories : null,
                winBlockedUrls : null,
                winBlockedExtensions : null,
                winBlockedMimeTypes : null,
                parentId : this.getId(),

                title : this.i18n._('Block Lists'),
                layout : "form",
                cls: 'ung-panel',
                autoScroll : true,
                defaults : {
                    xtype : 'fieldset',
                    autoHeight : true,
                    buttonAlign : 'left'
                },
                items : [{
                    name : "fieldset_manage_categories",
                    items : [{
                        xtype : "button",
                        name : "manage_categories",
                        text : this.i18n._("Edit Categories"),
                        handler : function() {
                            this.panelBlockLists.onManageBlacklistCategories();
                        }.createDelegate(this)
                    }]
                },{
                    items : [{
                        xtype : "button",
                        name : 'manage_sites',
                        text : this.i18n._("Edit Sites"),
                        handler : function() {
                            this.panelBlockLists.onManageBlockedUrls();
                        }.createDelegate(this)
                    }]
                },{
                    items : [{
                        xtype : "button",
                        name : "manage_file_types",
                        text : this.i18n._("Edit File Types"),
                        handler : function() {
                            this.panelBlockLists.onManageBlockedExtensions();
                        }.createDelegate(this)
                    }]
                },{
                    items : [{
                        xtype : "button",
                        name : "manage_mime_types",
                        text : this.i18n._("Edit MIME Types"),
                        handler : function() {
                            this.panelBlockLists.onManageBlockedMimeTypes();
                        }.createDelegate(this)
                    }]
                },{
                    name : "fieldset_miscellaneous",
                    items : [{
                        xtype : "checkbox",
                        boxLabel : this.i18n._("Block pages from IP only hosts"),
                        hideLabel : true,
                        name : 'Block IPHost',
                        checked : this.getBaseSettings().blockAllIpHosts,
                        listeners : {
                            "check" : {
                                fn : function(elem, checked) {
                                    this.getBaseSettings().blockAllIpHosts = checked;
                                }.createDelegate(this)
                            }
                        }
                    },{
                        xtype : "combo",
                        editable : false,
                        mode : "local",
                        fieldLabel : this.i18n._("User Bypass"),
                        name : "user_bypass",
                        store : new Ext.data.SimpleStore({
                            fields : ['userWhitelistValue', 'userWhitelistName'],
                            data : [["NONE", this.i18n._("None")], ["USER_ONLY", this.i18n._("Temporary")],
                                    ["USER_AND_GLOBAL", this.i18n._("Permanent and Global")]]
                        }),
                        displayField : "userWhitelistName",
                        valueField : "userWhitelistValue",
                        value : this.getBaseSettings().userWhitelistMode,
                        triggerAction : "all",
                        listClass : 'x-combo-list-small',
                        listeners : {
                            "change" : {
                                fn : function(elem, newValue) {
                                    this.getBaseSettings().userWhitelistMode = newValue;
                                }.createDelegate(this)
                            }
                        }
                    }]
                }],

                onManageBlacklistCategories : function() {
                    if (!this.winBlacklistCategories) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildBlacklistCategories();
                        this.winBlacklistCategories = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridBlacklistCategories.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelBlockLists.winBlacklistCategories.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    );
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelBlockLists.winBlacklistCategories.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("Categories")
                            }],
                            grid : settingsCmp.gridBlacklistCategories,
                            applyAction : function(forceLoad){
                                Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                var saveList = settingsCmp.gridBlacklistCategories.getSaveList();
                                settingsCmp.getRpcNode().updateBlacklistCategories(function(result, exception) {
                                    Ext.MessageBox.hide();
                                    if(Ung.Util.handleException(exception)) return;
                                    if(forceLoad===true){                                                
                                        this.gridBlacklistCategories.reloadGrid();
                                    }
                                }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }                                                        
                        });
                    }
                    this.winBlacklistCategories.show();
                },
                onManageBlockedUrls : function() {
                    if (!this.winBlockedUrls) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildBlockedUrls();
                        this.winBlockedUrls = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridBlockedUrls.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelBlockLists.winBlockedUrls.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    )
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelBlockLists.winBlockedUrls.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("Sites")
                            }],
                            grid : settingsCmp.gridBlockedUrls,
                            applyAction : function(forceLoad){
                                Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                var saveList = settingsCmp.gridBlockedUrls.getSaveList();
                                settingsCmp.alterUrls(saveList);
                                settingsCmp.getRpcNode().updateBlockedUrls(function(result, exception) {
                                    if(Ung.Util.handleException(exception)){
                                        Ext.MessageBox.hide();
                                        return;
                                    }
                                    this.getRpcNode().getBaseSettings(function(result2,exception2){
                                        Ext.MessageBox.hide();                                                
                                        if(Ung.Util.handleException(exception2)){
                                            return;
                                        }
                                        this.gridBlockedUrls.setTotalRecords(result2.blockedUrlsLength);
                                        if(forceLoad===true){                                                
                                            this.gridBlockedUrls.reloadGrid();
                                        }                                                    
                                    }.createDelegate(this));
                                }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }                                                        
                        });
                    }
                    this.winBlockedUrls.show();
                },
                onManageBlockedExtensions : function() {
                    if (!this.winBlockedExtensions) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildBlockedExtensions();
                        this.winBlockedExtensions = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridBlockedExtensions.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelBlockLists.winBlockedExtensions.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    )
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelBlockLists.winBlockedExtensions.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("File Types")
                            }],
                            grid : settingsCmp.gridBlockedExtensions,
                            applyAction : function(forceLoad){
                                Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                var saveList = settingsCmp.gridBlockedExtensions.getSaveList();
                                settingsCmp.getRpcNode().updateBlockedExtensions(function(result, exception) {
                                    if(Ung.Util.handleException(exception)){
                                        Ext.MessageBox.hide();
                                        return;
                                    }
                                    this.getRpcNode().getBaseSettings(function(result2,exception2){
                                        Ext.MessageBox.hide();                                                
                                        if(Ung.Util.handleException(exception2)){
                                            return;
                                        }
                                        this.gridBlockedExtensions.setTotalRecords(result2.blockedExtensionsLength);
                                        if(forceLoad===true){                                                
                                            this.gridBlockedExtensions.reloadGrid();
                                        }                                                    
                                    }.createDelegate(this));
                                }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }                                                        
                        });
                    }
                    this.winBlockedExtensions.show();
                },
                onManageBlockedMimeTypes : function() {
                    if (!this.winBlockedMimeTypes) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildBlockedMimeTypes();
                        this.winBlockedMimeTypes = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridBlockedMimeTypes.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelBlockLists.winBlockedMimeTypes.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    )
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelBlockLists.winBlockedMimeTypes.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("MIME Types")
                            }],
                            grid : settingsCmp.gridBlockedMimeTypes,
                            applyAction : function(forceLoad){
                                            Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                            var saveList = settingsCmp.gridBlockedMimeTypes.getSaveList();
                                            settingsCmp.getRpcNode().updateBlockedMimeTypes(function(result, exception) {
                                                if(Ung.Util.handleException(exception)){
                                                    Ext.MessageBox.hide();
                                                    return;
                                                }
                                                this.getRpcNode().getBaseSettings(function(result2,exception2){
                                                    Ext.MessageBox.hide();                                                
                                                    if(Ung.Util.handleException(exception2)){
                                                        return;
                                                    }
                                                    this.gridBlockedMimeTypes.setTotalRecords(result2.blockedMimeTypesLength);
                                                    if(forceLoad===true){                                                
                                                        this.gridBlockedMimeTypes.reloadGrid();
                                                    }                                                    
                                                }.createDelegate(this));
                                            }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }
                        });
                    }
                    this.winBlockedMimeTypes.show();
                },
                beforeDestroy : function() {
                    Ext.destroy(this.winBlacklistCategories, this.winBlockedUrls, this.winBlockedExtensions, this.winBlockedMimeTypes);
                    Ext.Panel.prototype.beforeDestroy.call(this);
                }
            });
        },
        // Block Categories
        buildBlacklistCategories : function() {
            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Block"),
                dataIndex : 'block',
                fixed : true,
                changeRecord : function(record) {
                    Ext.grid.CheckColumn.prototype.changeRecord.call(this, record);
                    var blocked = record.get(this.dataIndex);
                    if (blocked) {
                        record.set('log', true);
                    }
                }
            });
            var logColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Flag"),
                dataIndex : 'log',
                fixed : true,
                tooltip : this.i18n._("Flag as Violation")
            });

            this.gridBlacklistCategories = new Ung.EditorGrid({
                name : 'Categories',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().blacklistCategoriesLength,
                hasAdd : false,
                hasDelete : false,
                title : this.i18n._("Categories"),
                recordJavaClass : "com.untangle.node.webfilter.BlacklistCategory",
                proxyRpcFn : this.getRpcNode().getBlacklistCategories,
                fields : [{
                    name : 'id'
                }, {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'displayName',
                    type : 'string',
                    convert : function(v) {
                        return this.i18n._(v)
                    }.createDelegate(this)
                }, {
                    name : 'block'
                }, {
                    name : 'log'
                }, {
                    name : 'description',
                    type : 'string',
                    convert : function(v) {
                        return this.i18n._(v)
                    }.createDelegate(this)
                }],
                columns : [{
                    id : 'displayName',
                    header : this.i18n._("category"),
                    width : 200,
                    dataIndex : 'displayName'
                }, liveColumn, logColumn, {
                    id : 'description',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'description',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'displayName',
                columnsDefaultSortable : true,
                autoExpandColumn : 'description',
                plugins : [liveColumn, logColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "Category",
                    dataIndex : "displayName",
                    fieldLabel : this.i18n._("Category"),
                    allowBlank : false,
                    width : 200,
                    disabled : true,
                    ctCls: "fixed-pos"
                }), new Ext.form.Checkbox({
                    name : "Block",
                    dataIndex : "block",
                    fieldLabel : this.i18n._("Block"),
                    listeners : {
                        "check" : {
                            fn : function(elem, checked) {
                                var rowEditor = this.gridBlacklistCategories.rowEditor;
                                if (checked) {
                                    rowEditor.inputLines[2].setValue(true);
                                }
                            }.createDelegate(this)
                        }
                    }
                }), new Ext.form.Checkbox({
                    name : "Flag",
                    dataIndex : "log",
                    fieldLabel : this.i18n._("Flag"),
                    tooltip : this.i18n._("Flag as Violation")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "description",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,
                    height : 60
                })]
            });
        },
        // Block Sites
        buildBlockedUrls : function() {
            var urlValidator = function(fieldValue) {
                if (fieldValue.indexOf("https://") == 0) {
                    return this.i18n._("\"URL\" specified cannot be blocked because it uses secure http (https)");
                }
                if (fieldValue.indexOf("http://") == 0) {
                    fieldValue = fieldValue.substr(7);
                }
                if (fieldValue.indexOf("www.") == 0) {
                    fieldValue = fieldValue.substr(4);
                }
                if (fieldValue.indexOf("/") == fieldValue.length - 1) {
                    fieldValue = fieldValue.substring(0, fieldValue.length - 1);
                }
                if (fieldValue.trim().length == 0) {
                    return this.i18n._("Invalid \"URL\" specified");
                }
                return true;
            }.createDelegate(this);
            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Block"),
                dataIndex : 'live',
                fixed : true
            });
            var logColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Flag"),
                dataIndex : 'log',
                fixed : true,
                tooltip : this.i18n._("Flag as Violation")
            });

            this.gridBlockedUrls = new Ung.EditorGrid({
                name : 'Sites',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().blockedUrlsLength,
                emptyRow : {
                    "string" : this.i18n._("[no site]"),
                    "live" : true,
                    "log" : true,
                    "description" : this.i18n._("[no description]")
                },
                title : this.i18n._("Sites"),
                recordJavaClass : "com.untangle.uvm.node.StringRule",
                proxyRpcFn : this.getRpcNode().getBlockedUrls,
                fields : [{
                    name : 'id'
                }, {
                    name : 'string',
                    type : 'string'
                }, {
                    name : 'live'
                }, {
                    name : 'log'
                }, {
                    name : 'description',
                    type : 'string'
                }],
                columns : [{
                    id : 'string',
                    header : this.i18n._("site"),
                    width : 200,
                    dataIndex : 'string',
                    editor : new Ext.form.TextField({
                        allowBlank : false,
                        validator : urlValidator,
                        blankText : this.i18n._("Invalid \"URL\" specified")
                    })
                }, liveColumn, logColumn, {
                    id : 'description',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'description',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'string',
                columnsDefaultSortable : true,
                autoExpandColumn : 'description',
                plugins : [liveColumn, logColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "Site",
                    dataIndex : "string",
                    fieldLabel : this.i18n._("Site"),
                    allowBlank : false,
                    width : 200,
                    validator : urlValidator,
                    blankText : this.i18n._("Invalid \"URL\" specified")
                }), new Ext.form.Checkbox({
                    name : "Block",
                    dataIndex : "live",
                    fieldLabel : this.i18n._("Block")
                }), new Ext.form.Checkbox({
                    name : "Flag",
                    dataIndex : "log",
                    fieldLabel : this.i18n._("Flag"),
                    tooltip : this.i18n._("Flag as Violation")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "description",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,
                    height : 60
                })]
            });
        },
        // Block File Types
        buildBlockedExtensions : function() {
            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Block"),
                dataIndex : 'live',
                fixed : true
            });
            var logColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Flag"),
                dataIndex : 'log',
                fixed : true,
                tooltip : this.i18n._("Flag as Violation")
            });

            this.gridBlockedExtensions = new Ung.EditorGrid({
                name : 'File Types',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().blockedExtensionsLength,
                emptyRow : {
                    "string" : "[no extension]",
                    "live" : true,
                    "log" : true,
                    "name" : this.i18n._("[no description]")
                },
                title : this.i18n._("File Types"),
                recordJavaClass : "com.untangle.uvm.node.StringRule",
                proxyRpcFn : this.getRpcNode().getBlockedExtensions,
                fields : [{
                    name : 'id'
                }, {
                    name : 'string',
                    type : 'string'
                }, {
                    name : 'live'
                }, {
                    name : 'log'
                }, {
                    name : 'name',
                    type : 'string'
                }],
                columns : [{
                    id : 'string',
                    header : this.i18n._("file type"),
                    width : 200,
                    dataIndex : 'string',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }, liveColumn, logColumn, {
                    id : 'name',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'name',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'string',
                columnsDefaultSortable : true,
                autoExpandColumn : 'name',
                plugins : [liveColumn, logColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "File Type",
                    dataIndex : "string",
                    fieldLabel : this.i18n._("File Type"),
                    allowBlank : false,
                    width : 200
                }), new Ext.form.Checkbox({
                    name : "Block",
                    dataIndex : "live",
                    fieldLabel : this.i18n._("Block")
                }), new Ext.form.Checkbox({
                    name : "Flag",
                    dataIndex : "log",
                    fieldLabel : this.i18n._("Flag"),
                    tooltip : this.i18n._("Flag as Violation")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "name",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,
                    height : 60
                })]
            });
        },
        // Block MIME Types
        buildBlockedMimeTypes : function() {
            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Block"),
                dataIndex : 'live',
                fixed : true
            });
            var logColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("Flag"),
                dataIndex : 'log',
                fixed : true,
                tooltip : this.i18n._("Flag as Violation")
            });

            this.gridBlockedMimeTypes = new Ung.EditorGrid({
                name : 'MIME Types',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().blockedMimeTypesLength,
                emptyRow : {
                    "mimeType" : this.i18n._("[no mime type]"),
                    "live" : true,
                    "log" : true,
                    "name" : this.i18n._("[no description]")
                },
                title : this.i18n._("MIME Types"),
                recordJavaClass : "com.untangle.uvm.node.MimeTypeRule",
                proxyRpcFn : this.getRpcNode().getBlockedMimeTypes,
                fields : [{
                    name : 'id'
                }, {
                    name : 'mimeType',
                    type : 'string'
                }, {
                    name : 'live'
                }, {
                    name : 'log'
                }, {
                    name : 'name',
                    type : 'string'
                }],
                columns : [{
                    id : 'mimeType',
                    header : this.i18n._("MIME type"),
                    width : 200,
                    dataIndex : 'mimeType',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }, liveColumn, logColumn, {
                    id : 'name',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'name',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'mimeType',
                columnsDefaultSortable : true,
                autoExpandColumn : 'name',
                plugins : [liveColumn, logColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "MIME Type",
                    dataIndex : "mimeType",
                    fieldLabel : this.i18n._("MIME Type"),
                    allowBlank : false,
                    width : 200
                }), new Ext.form.Checkbox({
                    name : "Block",
                    dataIndex : "live",
                    fieldLabel : this.i18n._("Block")
                }), new Ext.form.Checkbox({
                    name : "Flag",
                    dataIndex : "log",
                    fieldLabel : this.i18n._("Flag"),
                    tooltip : this.i18n._("Flag as Violation")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "name",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,
                    height : 60
                })]
            });
        },

        // Pass Lists Panel
        buildPassLists : function() {
            this.panelPassLists = new Ext.Panel({
                // private fields
                name : 'Pass Lists',
                helpSource : 'pass_lists',
                winPassedUrls : null,
                winPassedClients : null,
                parentId : this.getId(),
                autoScroll : true,
                title : this.i18n._('Pass Lists'),
                layout : "form",
                bodyStyle : 'padding:5px 5px 0px; 5px;',
                defaults : {
                    xtype : 'fieldset',
                    autoHeight : true,
                    buttonAlign : 'left'
                },
                items : [{
                    buttons : [{
                        name : 'Sites manage list',
                        text : this.i18n._("Edit Passed Sites"),
                        handler : function() {
                            this.panelPassLists.onManagePassedUrls();
                        }.createDelegate(this)
                    }]
                }, {
                    buttons : [{
                        name : 'Client IP addresses manage list',
                        text : this.i18n._("Edit Passed Client IPs"),
                        handler : function() {
                            this.panelPassLists.onManagePassedClients();
                        }.createDelegate(this)
                    }]
                }],

                onManagePassedUrls : function() {
                    if (!this.winPassedUrls) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildPassedUrls();
                        this.winPassedUrls = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridPassedUrls.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelPassLists.winPassedUrls.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    )
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelPassLists.winPassedUrls.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("Sites")
                            }],
                            grid : settingsCmp.gridPassedUrls,
                            applyAction : function(forceLoad){
                                Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                var saveList = settingsCmp.gridPassedUrls.getSaveList();
                                settingsCmp.alterUrls(saveList);
                                settingsCmp.getRpcNode().updatePassedUrls(function(result, exception) {
                                    if(Ung.Util.handleException(exception)){
                                        Ext.MessageBox.hide();
                                        return;
                                    }
                                    this.getRpcNode().getBaseSettings(function(result2,exception2){
                                        Ext.MessageBox.hide();                                                
                                        if(Ung.Util.handleException(exception2)){
                                            return;
                                        }
                                        this.gridPassedUrls.setTotalRecords(result2.passedUrlsLength);
                                        if(forceLoad===true){                                                
                                            this.gridPassedUrls.reloadGrid();
                                        }                                                    
                                    }.createDelegate(this));
                                }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }                            
                        });
                    }
                    this.winPassedUrls.show();
                },
                onManagePassedClients : function() {
                    if (!this.winPassedClients) {
                        var settingsCmp = Ext.getCmp(this.parentId);
                        settingsCmp.buildPassedClients();
                        this.winPassedClients = new Ung.ManageListWindow({
                            breadcrumbs : [{
                                title : i18n._(rpc.currentPolicy.name),
                                action : function() {
                                    Ung.Window.cancelAction(
                                       this.gridPassedClients.isDirty() || this.isDirty(),
                                       function() {
                                            this.panelPassLists.winPassedClients.closeWindow();
                                            this.closeWindow();
                                       }.createDelegate(this)
                                    )
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.node.md.displayName,
                                action : function() {
                                    this.panelPassLists.winPassedClients.cancelAction();
                                }.createDelegate(settingsCmp)
                            }, {
                                title : settingsCmp.i18n._("Client IP addresses")
                            }],
                            grid : settingsCmp.gridPassedClients,
                            saveAction : function()
                            {
                                this.commitSettings(this.completeSaveAction.createDelegate(this));
                            },
                            completeSaveAction : function()
                            {
                                Ext.MessageBox.hide();
                                this.closeWindow();
                            },
                            applyAction : function()
                            {
                                this.commitSettings(this.reloadSettings.createDelegate(this));
                            },
                            reloadSettings : function()
                            {
                                this.grid.reloadGrid();
                                Ext.MessageBox.hide();
                            },
                            commitSettings : function(callback){
                                var saveList = settingsCmp.gridPassedClients.getSaveList();
                                if ( !settingsCmp.validateServer(saveList)) {
                                    return;
                                }
                                
                                Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
                                
                                settingsCmp.getRpcNode().updatePassedClients(function(result, exception) {
                                    if(Ung.Util.handleException(exception)){
                                        return;
                                    }
                                    this.getRpcNode().getBaseSettings(function(result2,exception2){
                                        
                                        if(Ung.Util.handleException(exception2)){
                                            return;
                                        }
                                        this.gridPassedClients.setTotalRecords(result2.passedClientsLength);
                                        callback();
                                    }.createDelegate(this));
                                }.createDelegate(settingsCmp), saveList[0],saveList[1],saveList[2]);
                            }                                                 
                        });
                    }
                    this.winPassedClients.show();
                },
                beforeDestroy : function() {
                    Ext.destroy(this.winPassedUrls, this.winPassedClients);
                    Ext.Panel.prototype.beforeDestroy.call(this);
                }
            });
        },
        // Passed Sites
        buildPassedUrls : function() {
            var urlValidator = function(fieldValue) {
                if (fieldValue.indexOf("https://") == 0) {
                    return this.i18n._("\"URL\" specified cannot be passed because it uses secure http (https)");
                }
                if (fieldValue.indexOf("http://") == 0) {
                    fieldValue = fieldValue.substr(7);
                }
                if (fieldValue.indexOf("www.") == 0) {
                    fieldValue = fieldValue.substr(4);
                }
                if (fieldValue.indexOf("/") == fieldValue.length - 1) {
                    fieldValue = fieldValue.substring(0, fieldValue.length - 1);
                }
                if (fieldValue.trim().length == 0) {
                    return this.i18n._("Invalid \"URL\" specified");
                }
                return true;
            }.createDelegate(this);

            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("pass"),
                dataIndex : 'live',
                fixed : true
            });

            this.gridPassedUrls = new Ung.EditorGrid({
                name : 'Sites',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().passedUrlsLength,
                emptyRow : {
                    "string" : this.i18n._("[no site]"),
                    "live" : true,
                    "description" : this.i18n._("[no description]")
                },
                title : this.i18n._("Sites"),
                recordJavaClass : "com.untangle.uvm.node.StringRule",
                proxyRpcFn : this.getRpcNode().getPassedUrls,
                fields : [{
                    name : 'id'
                }, {
                    name : 'string',
                    type : 'string'
                }, {
                    name : 'live'
                }, {
                    name : 'description',
                    type : 'string'
                }],
                columns : [{
                    id : 'string',
                    header : this.i18n._("site"),
                    width : 200,
                    dataIndex : 'string',
                    editor : new Ext.form.TextField({
                        allowBlank : false,
                        validator : urlValidator,
                        blankText : this.i18n._("Invalid \"URL\" specified")
                    })
                }, liveColumn, {
                    id : 'description',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'description',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'string',
                columnsDefaultSortable : true,
                autoExpandColumn : 'description',
                plugins : [liveColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "Site",
                    dataIndex : "string",
                    fieldLabel : this.i18n._("Site"),
                    allowBlank : false,
                    width : 200,
                    validator : urlValidator,
                    blankText : this.i18n._("Invalid \"URL\" specified")
                }), new Ext.form.Checkbox({
                    name : "Pass",
                    dataIndex : "live",
                    fieldLabel : this.i18n._("Pass")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "description",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,

                    height : 60
                })]
            });
        },
        // Passed IP Addresses
        buildPassedClients : function() {
            var liveColumn = new Ext.grid.CheckColumn({
                header : this.i18n._("pass"),
                dataIndex : 'live',
                fixed : true
            });

            this.gridPassedClients = new Ung.EditorGrid({
                name : 'Client IP addresses',
                settingsCmp : this,
                totalRecords : this.getBaseSettings().passedClientsLength,
                emptyRow : {
                    "ipMaddr" : "1.2.3.4",
                    "live" : true,
                    "description" : this.i18n._("[no description]")
                },
                title : this.i18n._("Client IP addresses"),
                recordJavaClass : "com.untangle.uvm.node.IPMaddrRule",
                proxyRpcFn : this.getRpcNode().getPassedClients,
                fields : [{
                    name : 'id'
                }, {
                    name : 'ipMaddr'
                }, {
                    name : 'live'
                }, {
                    name : 'description',
                    type : 'string'
                }],
                columns : [{
                    id : 'ipMaddr',
                    header : this.i18n._("IP address/range"),
                    width : 200,
                    dataIndex : 'ipMaddr',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }, liveColumn, {
                    id : 'description',
                    header : this.i18n._("description"),
                    width : 200,
                    dataIndex : 'description',
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }],
                sortField : 'ipMaddr',
                columnsDefaultSortable : true,
                autoExpandColumn : 'description',
                plugins : [liveColumn],
                rowEditorInputLines : [new Ext.form.TextField({
                    name : "IP address/range",
                    dataIndex : "ipMaddr",
                    fieldLabel : this.i18n._("IP address/range"),
                    allowBlank : false,
                    width : 200
                }), new Ext.form.Checkbox({
                    name : "Pass",
                    dataIndex : "live",
                    fieldLabel : this.i18n._("Pass")
                }), new Ext.form.TextArea({
                    name : "Description",
                    dataIndex : "description",
                    fieldLabel : this.i18n._("Description"),
                    width : 200,
                    height : 60
                })]
            });
        },
        // Event Log
        buildEventLog : function() {
            var asClient = function(value) {
                return (value === null  || value.pipelineEndpoints === null) ? "" : value.pipelineEndpoints.CClientAddr + ":" + value.pipelineEndpoints.CClientPort;
            };
            var asServer = function(value) {
                return (value === null  || value.pipelineEndpoints === null) ? "" : value.pipelineEndpoints.SServerAddr + ":" + value.pipelineEndpoints.SServerPort;
            };
            var asRequest = function(value) {
                return (value === null  || value.url === null) ? "" : value.url;
            };

            this.gridEventLog = new Ung.GridEventLog({
                settingsCmp : this,
                fields : [{
                    name : 'timeStamp',
                    sortType : Ung.SortTypes.asTimestamp
                }, {
                    name : 'displayAction',
                    mapping : 'actionType',
                    type : 'string',
                    convert : function(value) {
                        switch (value) {
                            case 0 : // PASSED
                                return this.i18n._("pass");
                            default :
                            case 1 : // BLOCKED
                                return this.i18n._("block");
                        }
                    }.createDelegate(this)
                }, {
                    name : 'client',
                    mapping : 'requestLine',
                    sortType : asClient
                }, {
                    name : 'server',
                    mapping : 'requestLine',
                    sortType : asServer
                }, {
                    name : 'request',
                    mapping : 'requestLine',
                    sortType : asRequest
                }, {
                    name : 'reason',
                    type : 'string',
                    convert : function(value) {
                        switch (value) {
                            case 'BLOCK_CATEGORY' :
                                return this.i18n._("in Categories Block list");
                            case 'BLOCK_URL' :
                                return this.i18n._("in URLs Block list");
                            case 'BLOCK_EXTENSION' :
                                return this.i18n._("in File Extensions Block list");
                            case 'BLOCK_MIME' :
                                return this.i18n._("in MIME Types Block list");
                            case 'BLOCK_ALL' :
                                return this.i18n._("blocking all traffic");
                            case 'BLOCK_IP_HOST' :
                                return this.i18n._("hostname is an IP address");
                            case 'PASS_URL' :
                                return this.i18n._("in URLs Pass list");
                            case 'PASS_CLIENT' :
                                return this.i18n._("in Clients Pass list");
                            case 'PASS_BYPASS' :
                                return this.i18n._("Client Bypass");
                            default :
                            case 'DEFAULT' :
                                return this.i18n._("no rule applied");
                        }
                    }.createDelegate(this)

                }],
                autoExpandColumn: 'request',
                columns : [{
                    header : this.i18n._("timestamp"),
                    width : 120,
                    sortable : true,
                    dataIndex : 'timeStamp',
                    renderer : function(value) {
                        return i18n.timestampFormat(value);
                    }
                }, {
                    header : this.i18n._("action"),
                    width : 100,
                    sortable : true,
                    dataIndex : 'displayAction'
                }, {
                    header : this.i18n._("client"),
                    width : 120,
                    sortable : true,
                    dataIndex : 'client',
                    renderer : asClient
                }, {
                    id: 'request',
                    header : this.i18n._("request"),
                    width : 200,
                    sortable : true,
                    dataIndex : 'request',
                    renderer : asRequest
                }, {
                    header : this.i18n._("reason for action"),
                    width : 150,
                    sortable : true,
                    dataIndex : 'reason'
                }, {
                    header : this.i18n._("server"),
                    width : 120,
                    sortable : true,
                    dataIndex : 'server',
                    renderer : asServer
                }]

            });
        },
        buildUnblockEventLog : function() {
            this.gridUnblockEventLog = new Ung.GridEventLog({
                settingsCmp : this,
        eventManagerFn : this.getRpcNode().getUnblockEventManager(),
        name : "Unblock Log",
        title : i18n._('Unblock Log'),
                fields : [{
                    name : 'timeStamp',
            mapping : 'timeStamp',
                    sortType : Ung.SortTypes.asTimestamp
                },
                {
                    name : 'isPermanent',
            mapping : 'isPermanent',
                    type : 'string',
                    convert : function(value) {
                        return ( value ) ? this.i18n._("permanent") : this.i18n._("temporary");
                    }.createDelegate(this)
                }, {
                    name : 'clientAddress',
            mapping : 'clientAddress'
                }, {
                    name : 'request',
            mapping : 'requestUri'
        }],
                autoExpandColumn: 'request',
                columns : [{
                    header : this.i18n._("timestamp"),
                    width : 120,
                    sortable : true,
                    dataIndex : 'timeStamp',
                    renderer : function(value) {
                        return i18n.timestampFormat(value);
                    }
                }, {
                    header : this.i18n._("permanent"),
                    width : 100,
                    sortable : true,
                    dataIndex : 'isPermanent'
                }, {
                    header : this.i18n._("client"),
                    width : 120,
                    sortable : true,
                    dataIndex : 'clientAddress'
                }, {
                    id : "request",
                    header : this.i18n._("request"),
                    width : 200,
                    sortable : true,
                    dataIndex : 'request'
                }]
            });
        },
        // private method
        alterUrls : function(list) {
            if (list != null) {
                // added
                for (var i = 0; i < list[0].list.length; i++) {
                    list[0].list[i]["string"] = this.alterUrl(list[0].list[i]["string"]);
                }
                // modified
                for (var i = 0; i < list[2].list.length; i++) {
                    list[2].list[i]["string"] = this.alterUrl(list[2].list[i]["string"]);
                }
            }
        },
        // private method
        alterUrl : function(value) {
            if (value.indexOf("http://") == 0) {
                value = value.substr(7);
            }
            if (value.indexOf("www.") == 0) {
                value = value.substr(4);
            }
            if (value.indexOf("/") == value.length - 1) {
                value = value.substring(0, value.length - 1);
            }
            return value.trim();
        },
        validateServer : function(passedClientsSaveList)
        {
            // ipMaddr list must be validated server side\            
            if (passedClientsSaveList != null) {
                var ipMaddrList = [];
                // added
                for (var i = 0; i < passedClientsSaveList[0].list.length; i++) {
                    ipMaddrList.push(passedClientsSaveList[0].list[i]["ipMaddr"]);
                }
                // modified
                for (var i = 0; i < passedClientsSaveList[2].list.length; i++) {
                    ipMaddrList.push(passedClientsSaveList[2].list[i]["ipMaddr"]);
                }
                if (ipMaddrList.length > 0) {
                    try {
                        var result = null;
                        try {
                            result = this.getValidator().validate({
                                list : ipMaddrList,
                                "javaClass" : "java.util.ArrayList"
                            });
                        } catch (e) {
                            Ung.Util.rpcExHandler(e);
                        }
                        if (!result.valid) {
                            var errorMsg = "";
                            switch (result.errorCode) {
                                case 'INVALID_IPMADDR' :
                                    errorMsg = this.i18n._("Invalid subnet specified") + ": " + result.cause;
                                break;
                                default :
                                    errorMsg = this.i18n._(result.errorCode) + ": " + result.cause;
                            }

                            this.panelPassLists.onManagePassedClients();
                            this.gridPassedClients.focusFirstChangedDataByFieldValue("ipMaddr", result.cause);
                            Ext.MessageBox.alert(this.i18n._("Validation failed"), errorMsg);
                            return false;
                        }
                    } catch (e) {
                        var message = e.message;
                        if (message == null || message == "Unknown") {
                            message = i18n._("Please Try Again");
                        }
                        
                        Ext.MessageBox.alert(i18n._("Failed"), message);
                        return false;
                    }
                }
            }
            return true;
        },
        validate : function()
        {
            return true;
        },
        saveAction : function()
        {
            this.commitSettings(this.completeSaveAction.createDelegate(this));
        },
        completeSaveAction : function()
        {
            Ext.MessageBox.hide();
            // exit settings screen
            this.closeWindow();
        },
        // save function
        commitSettings : function(callback)
        {
            if (!this.validate()) {
                return;
            }
            
            Ext.MessageBox.wait(i18n._("Saving..."), i18n._("Please wait"));
            this.getRpcNode().setBaseSettings(function(result, exception) {
                if(Ung.Util.handleException(exception)) {
                    return;
                }
                
                callback();
            }.createDelegate(this), this.getBaseSettings());
        },
        isDirty : function() {
            return !Ung.Util.equals(this.getBaseSettings(), this.initialBaseSettings);
        }
    });
}
