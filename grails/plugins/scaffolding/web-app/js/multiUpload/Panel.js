Ext.define('Ext.multiUpload.Panel', {
    extend: 'Ext.grid.Panel',
    requires: [
        'Ext.multiUpload.Upload'
    ],
    viewConfig: {
        markDirty: false
    },
    store: {
        fields: ['id', 'name', 'size', 'status', 'progress']
    },
    initComponent: function () {
        var me = this;

        me.addEvents('fileuploadcomplete');

        me.tbar = [{
            xtype: 'uploader',
            uploadConfig: this.uploadConfig,
            listeners:
            {
                'fileadded': function (source, file) {
                    this.up('grid').store.add({
                        id: file.fileIndex,
                        name: file.fileName,
                        size: file.fileSize,
                        status: '等待上传...',
                        progress: 0
                    });
                },
                'uploadstart': function (source, file) {
                    var grid = this.up('grid');
                    var record = grid.store.getById(file.fileIndex);

                    if (record) {
                        record.set('status', '上传中...');
                    }
                },
                'uploadprogress': function (source, file) {
                    var grid = this.up('grid');
                    var record = grid.store.getById(file.fileIndex);
                    if (record) {
                        var p = Math.round(file.fileProgress / file.fileSize * 100);
                        record.set('progress', p);
                    }
                },
                'uploaddatacomplete': function (source, file) {
                    var grid = this.up('grid');
                    var record = grid.store.getById(file.fileIndex);
                    if (record) {
                        record.set('status', '上传成功');
                    }
                    me.fireEvent('fileuploadcomplete', file.data);
                },
                'queuedatacomplete': function (source, data) {
                    Ext.Msg.show({
                        title: '提示',
                        msg: '已成功上传 ' + data.files + ' 个文件.',
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.INFO
                    });
                },
                'uploaderror': function (src, data) {
                    var msg = '错误类型: ' + data.errorType;

                    switch (data.errorType) {
                        case 'FileSize':
                            msg = '文件大小: ' + Ext.util.Format.fileSize(data.fileSize) +
                            '. 最大只能上传 ' + Ext.util.Format.fileSize(data.maxFileSize) + ' 大小的文件.';
                            break;

                        case 'QueueLength':
                            msg = '只能同时上传 ' + data.maxQueueLength + ' 个文件！';
                            break;
                    }

                    Ext.Msg.show({
                        title: '上传错误',
                        msg: msg,
                        buttons: Ext.Msg.OK,
                        icon: Ext.Msg.ERROR
                    });
                }
            }
        }];

        me.columns = [
            /*
            {
                header: '序号',
                dataIndex: 'id',
                width: 75,
                renderer: function (v) { return v + 1; }
            },*/
            { header: '文件名', dataIndex: 'name', flex: 2},
            { header: '大小', dataIndex: 'size', renderer: Ext.util.Format.fileSize, flex: 1},
            { header: '状态', dataIndex: 'status' ,flex: 1},
            {
                header: '进度',
                dataIndex: 'progress',
                flex: 1,
                renderer: function (v) { return v + '%'; }
            }
        ];

        me.callParent(arguments);
    }
});
