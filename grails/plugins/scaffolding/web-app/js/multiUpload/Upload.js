Ext.define('Ext.multiUpload.Upload', {
    extend: 'Ext.flash.Component',
    requires: [
        'Ext.multiUpload.UploadManager'
    ],
    alias: 'widget.uploader',
    width: 101,
    height: 22,
    wmode: 'transparent',
    //url: '', //'/creative/plugins/scaffolding-2.0.3/js/multiUpload/Upload.swf',
    statics: {
        instanceId: 0
    },
    constructor: function (config) {
        config = config || {};
        config.instanceId = Ext.String.format('upload-{0}', ++Ext.multiUpload.Upload.instanceId);
        config.flashVars = config.flashVars || {};
        config.flashVars = Ext.apply({
            instanceId: config.instanceId,
            buttonImagePath: '', //'/creative/plugins/scaffolding-2.0.3/js/multiUpload/button.png',
            buttonImageHoverPath: '', //'/creative/plugins/scaffolding-2.0.3/js/multiUpload/button_hover.png',
            //fileFilters: 'Images (*.jpg)|*.jpg',
            uploadUrl: '/upload/url',
            maxFileSize: 0,
            maxQueueLength: 0,
            maxQueueSize: 0,
            callback: 'Ext.multiUpload.UploadManager.uploadCallback'
        }, config.uploadConfig);

        config.url = config.uploadConfig.url;

        this.addEvents(
            'fileadded',
            'uploadstart',
            'uploadprogress',
            'uploadcomplete',
            'uploaddatacomplete',
            'queuecomplete',
            'queuedatacomplete',
            'uploaderror'
        );

        this.callParent([config]);
    },
    initComponent: function () {
        Ext.multiUpload.UploadManager.register(this);
        this.callParent(arguments);
    },
    onDestroy: function () {
        Ext.multiUpload.UploadManager.unregister(this);
        this.callParent(arguments);
    }
});
