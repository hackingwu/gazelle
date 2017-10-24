/*
 * 提示消息核心控件实现
 **/
Ext.wintip = function(){
    var msgCt;
    var menuHelpCt, mainHelpCt, topHelpCt;

    function createBox(t, s,type){
        var ret;
        if(type=='msg'){
            ret = '<div class="msg ' + Ext.baseCSSPrefix + 'border-box"><span>' + t + '</span><p>' + s + '</p></div>';
        }else if(type=='error'){
            ret = '<div class="error ' + Ext.baseCSSPrefix + 'border-box"><span>' + t + '</span><p>' + s + '</p></div>';
        }else{
            ret = '<div class="' + type+ '-help ' + Ext.baseCSSPrefix + 'border-box"><span>' + t + '</span><p>' + s + '</p></div>';
        }
       return ret;
    }
    return {
        showTopHelp : function(title, format, type){
            if(!topHelpCt){
                topHelpCt = Ext.DomHelper.insertFirst(document.body, {id:'top-help-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(topHelpCt, createBox(title, s,type), true);
            m.hide();
            m.slideIn('t',{easing: 'easeOut',duration: 500}).ghost("t", { delay: 1300000, remove: true});
        },
        showMenuHelp : function(title, format, type){
            if(!menuHelpCt){
                menuHelpCt = Ext.DomHelper.insertFirst(document.body, {id:'menu-help-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(menuHelpCt, createBox(title, s,type), true);
            m.hide();
            m.slideIn('t',{easing: 'easeOut',duration: 500}).ghost("t", { delay: 1300000, remove: true});
        },
        showMainHelp : function(title, format, type){
            if(!mainHelpCt){
                mainHelpCt = Ext.DomHelper.insertFirst(document.body, {id:'main-help-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(mainHelpCt, createBox(title, s,type), true);
            m.hide();
            m.slideIn('t',{easing: 'easeOut',duration: 500}).ghost("t", { delay: 1300000, remove: true});
        },
        msg : function(title, format){
            if(!msgCt){
                msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(msgCt, createBox(title, s,'msg'), true);
            m.hide();
            m.slideIn('t',{easing: 'easeOut',duration: 500}).ghost("t", { delay: 3000, remove: true});
        },
        error : function(title, format){
            if(!msgCt){
                msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
            var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(msgCt, createBox(title, s,'error'), true);
            m.hide();
            m.slideIn('t',{easing: 'easeOut',duration: 500}).ghost("t", { delay: 6000, remove: true});
        },
        init : function(){
            if(!msgCt){
                msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
        }
    };
}();

Ext.onReady(Ext.wintip.init, Ext.wintip);
