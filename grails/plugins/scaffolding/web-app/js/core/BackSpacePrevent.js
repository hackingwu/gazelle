/*
 * 阻止按退格键退出系统
 **/
(Ext.isIE ? document: window).onkeydown = function(event) {
    var e = Ext.isIE ? window.event: event;
    var element = Ext.isIE ? e.srcElement: e.target;
    var type = element.type;
    if (e.keyCode === 8 && (!type || element.readOnly || type=="button")) {
        return false;
    }
};

