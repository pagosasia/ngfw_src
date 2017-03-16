Ext.define('Ung.apps.captiveportal.view.CaptivePage', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-captive-portal-captivepage',
    itemId: 'captivepage',
    title: 'Captive Page'.t(),

    viewModel: {
        formulas: {
            _redirUrl: {
                get: function (get) {
                    return get('settings.redirectUrl');
                },
                set: function (value) {
                    var useValue = value;
                    if ((value.length > 0) && (value.indexOf('http://') !== 0) && (value.indexOf('https://') !== 0)) {
                        useValue = ('http://' + value);
                    }
                    this.set('settings.redirectUrl', useValue);
                }
            }
        }
    },

    bodyPadding: 10,
    scrollable: 'y',

    layout: {
        type: 'vbox'
    },

    tbar: [{
        text: 'Preview Captive Portal Page'.t(),
        iconCls: 'fa fa-eye',
        handler: 'previewCaptivePage'
    }],

    items: [{
        xtype: 'radiogroup',
        margin: '0 0 20 0',
        bind: '{settings.pageType}',
        simpleValue: true,
        columns: 3,
        items: [
            { boxLabel: '<strong>' + 'Basic Message'.t() + '</strong>', inputValue: 'BASIC_MESSAGE', width: 150 },
            { boxLabel: '<strong>' + 'Basic Login'.t() + '</strong>', inputValue: 'BASIC_LOGIN', width: 150 },
            { boxLabel: '<strong>' + 'Custom'.t() + '</strong>', inputValue: 'CUSTOM', width: 150 }
        ]
    }, {
        xtype: 'fieldset',
        width: '100%',
        title: 'Captive Portal Page Configuration'.t(),
        padding: 10,
        hidden: true,
        bind: {
            hidden: '{settings.pageType !== "BASIC_MESSAGE"}'
        },
        defaults: {
            xtype: 'textfield',
            allowBlank: false,
            labelWidth: 120,
            labelAlign: 'right'
        },
        items: [{
            fieldLabel: 'Page Title'.t(),
            bind: '{settings.basicMessagePageTitle}'
        }, {
            fieldLabel: 'Welcome Text'.t(),
            width: 400,
            bind: '{settings.basicMessagePageWelcome}'
        }, {
            xtype: 'textarea',
            width: 400,
            height: 250,
            fieldLabel: 'Message Text'.t(),
            bind: '{settings.basicMessageMessageText}'
        }, {
            xtype: 'checkbox',
            fieldLabel: 'Agree Checkbox'.t(),
            bind: '{settings.basicMessageAgreeBox}'
        }, {
            fieldLabel: 'Agree Text'.t(),
            width: 400,
            bind: '{settings.basicMessageAgreeText}'
        }, {
            fieldLabel: 'Lower Text'.t(),
            width: 400,
            bind: '{settings.basicMessageFooter}'
        }]
    }, {
        xtype: 'fieldset',
        width: '100%',
        title: 'Captive Portal Page Configuration'.t(),
        padding: 10,
        hidden: true,
        bind: {
            hidden: '{settings.pageType !== "BASIC_LOGIN"}'
        },
        defaults: {
            xtype: 'textfield',
            allowBlank: false,
            labelWidth: 120,
            labelAlign: 'right'
        },
        items: [{
            fieldLabel: 'Page Title'.t(),
            bind: '{settings.basicLoginPageTitle}'
        }, {
            fieldLabel: 'Welcome Text'.t(),
            width: 400,
            bind: '{settings.basicLoginPageWelcome}'
        }, {
            fieldLabel: 'Username Text'.t(),
            bind: '{settings.basicLoginUsername}'
        }, {
            fieldLabel: 'Password Text'.t(),
            bind: '{settings.basicLoginPassword}'
        }, {
            xtype: 'textarea',
            allowBlank: true,
            width: 600,
            height: 200,
            fieldLabel: 'Message Text'.t(),
            bind: '{settings.basicLoginMessageText}'
        }, {
            fieldLabel: 'Lower Text'.t(),
            width: 600,
            bind: '{settings.basicLoginFooter}'
        }]
    }, {
        xtype: 'fieldset',
        width: '100%',
        title: 'Captive Portal Page Configuration'.t(),
        padding: 10,
        hidden: true,
        bind: {
            hidden: '{settings.pageType !== "CUSTOM"}'
        },
        defaults: {
            xtype: 'form',
            url: 'upload',
            border: false
        },
        items: [{
            items: [{
                xtype: 'fileuploadfield',
                name: 'upload_file',
                allowBlank: false,
                width: 500
            },{
                xtype: 'hidden',
                name: 'type',
                value: 'custom_page'
            },{
                xtype: 'hidden',
                name: 'argument',
                value: 'UPLOAD'
            },{
                xtype: 'button',
                formBind: true,
                name: 'upload',
                text: 'Upload Custom File'.t(),
                // handler: Ext.bind(this.onUploadCustomFile, this)
            }]
        }, {
            margin: '10 0 0 0',
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Active Custom File'.t(),
                labelAlign: 'top',
                readOnly: true,
                name: 'custom_file',
                bind: '{settings.customFilename}',
                width: 500
            }, {
                xtype: 'fileuploadfield',
                name: 'remove_file',
                allowBlank: true,
                hidden: true
            }, {
                xtype: 'hidden',
                name: 'type',
                value: 'custom_page'
            }, {
                xtype: 'hidden',
                name: 'argument',
                value: 'REMOVE'
            }, {
                xtype: 'button',
                name: 'remove',
                text: 'Remove Custom File'.t(),
                // handler: Ext.bind(this.onRemoveCustomFile, this)
            }]
        }]
    }, {
        xtype: 'fieldset',
        width: '100%',
        title: 'HTTPS/SSL Root Certificate Detection'.t(),
        padding: 10,
        hidden: true,
        bind: {
            hidden: '{settings.pageType === "CUSTOM"}'
        },
        items: [{
            xtype: 'radiogroup',
            bind: '{settings.certificateDetection}',
            simpleValue: true,
            columns: 1,
            vertical: true,
            items: [
                { boxLabel: 'Disable certificate detection.'.t(), inputValue: 'DISABLE_DETECTION' },
                { boxLabel: 'Check certificate. Show warning when not detected.'.t(), inputValue: 'CHECK_CERTIFICATE' },
                { boxLabel: 'Require certificate. Prohibit login when not detected.'.t(), inputValue: 'REQUIRE_CERTIFICATE' }
            ]
        }]
    }, {
        xtype: 'fieldset',
        width: '100%',
        title: 'Session Redirect'.t(),
        padding: 10,
        items: [{
            xtype: 'checkbox',
            boxLabel: 'Always use HTTPS for the capture page redirect'.t(),
            bind: '{settings.alwaysUseSecureCapture}'
        }, {
            xtype: 'textfield',
            width: 600,
            fieldLabel: 'Redirect URL'.t(),
            bind: '{_redirUrl}'
        }, {
            xtype: 'component',
            margin: '10 0 0 0',
            html: '<B>NOTE:</B> The Redirect URL field must start with http:// or https:// and allows you to specify a page to display immediately after user authentication.  If you leave this field blank, users will instead be forwarded to their original destination.'.t()
        }]
    }]
});