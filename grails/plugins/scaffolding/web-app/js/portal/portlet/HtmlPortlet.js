Ext.define('Esm.portlet.HtmlPortlet', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.htmlportlet',

    border:false,

    autoScroll : true ,

    config :{

    },
    listeners :{
        boxready : {
            fn:function(me,wt,ht,e){
                // alert(me.getEl());
                var params = Ext.apply({},me.option.params);

                Ext.Ajax.request({
                    url: me.dataUrl,
                    params: params,
                    success: function(response){

                        var text = response.responseText;
                        var op = Ext.JSON.decode(text);

                        var title = op.title;
                        if(title){
                            me.ownerCt.setTitle(title);
                        }
                        me.body.setHTML(op.data);

                        //me.ownerCt.setHTML(text);
                       //  me.ownerCt.setTitle(text);
                        // me.removeAll();
                       // me.add({height:"100%",width:"100%", html:'dddddsf<b>ddsd</b>'});
                        //alert( me.xtype);
                        //alert( me.ownerCt.xtype);
                      //  me.ownerCt.add({xtype:'panel', html:'ffdgfdg'});


                    }
                });

            }
        }
    }
});
