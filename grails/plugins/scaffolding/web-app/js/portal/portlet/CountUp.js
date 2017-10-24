/*

 countUp.js
 by @inorganik
 v 1.1.1

 */

// target = id of html element or var of previously selected html element where counting occurs
// startVal = the value you want to begin at
// endVal = the value you want to arrive at
// decimals = number of decimal places, default 0
// duration = duration of animation in seconds, default 2
// options = optional object of options (see below)

function countUp(target, startVal, endVal, decimals, duration, options, height) {

    // make sure requestAnimationFrame and cancelAnimationFrame are defined
    // polyfill for browsers without native support
    // by Opera engineer Erik Möller
    var lastTime = 0;
    var vendors = ['webkit', 'moz', 'ms'];
    for(var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
        window.requestAnimationFrame = window[vendors[x]+'RequestAnimationFrame'];
        window.cancelAnimationFrame =
            window[vendors[x]+'CancelAnimationFrame'] || window[vendors[x]+'CancelRequestAnimationFrame'];
    }
    if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function(callback, element) {
            var currTime = new Date().getTime();
            var timeToCall = Math.max(0, 16 - (currTime - lastTime));
            var id = window.setTimeout(function() { callback(currTime + timeToCall); },
                timeToCall);
            lastTime = currTime + timeToCall;
            return id;
        }
    }
    if (!window.cancelAnimationFrame) {
        window.cancelAnimationFrame = function(id) {
            clearTimeout(id);
        }
    }

    // default options
    this.options = options || {
        useEasing : true, // toggle easing
        useGrouping : true, // 1,000,000 vs 1000000
        separator : ',', // character to use as a separator
        decimal : '.' // character to use as a decimal
    }
    if (this.options.separator == '') this.options.useGrouping = false;

    var self = this;

    this.d = (typeof target === 'string') ? document.getElementById(target) : target;
    this.startVal = Number(startVal);
    this.endVal = Number(endVal);
    this.countDown = (this.startVal > this.endVal) ? true : false;
    this.startTime = null;
    this.timestamp = null;
    this.remaining = null;
    this.frameVal = this.startVal;
    this.rAF = null;
    this.decimals = Math.max(0, decimals || 0);
    this.dec = Math.pow(10, this.decimals);
    this.duration = duration * 1000 || 2000;

    this.version = function () { return '1.1.1' }

    // Robert Penner's easeOutExpo
    this.easeOutExpo = function(t, b, c, d) {
        return c * (-Math.pow(2, -10 * t / d) + 1) * 1024 / 1023 + b;
    }
    this.count = function(timestamp) {

        if (self.startTime === null) self.startTime = timestamp;

        self.timestamp = timestamp;

        var progress = timestamp - self.startTime;
        self.remaining = self.duration - progress;

        // to ease or not to ease
        if (self.options.useEasing) {
            if (self.countDown) {
                var i = self.easeOutExpo(progress, 0, self.startVal - self.endVal, self.duration);
                self.frameVal = self.startVal - i;
            } else {
                self.frameVal = self.easeOutExpo(progress, self.startVal, self.endVal - self.startVal, self.duration);
            }
        } else {
            if (self.countDown) {
                var i = (self.startVal - self.endVal) * (progress / self.duration);
                self.frameVal = self.startVal - i;
            } else {
                self.frameVal = self.startVal + (self.endVal - self.startVal) * (progress / self.duration);
            }
        }

        // decimal
        self.frameVal = Math.round(self.frameVal*self.dec)/self.dec;

        // don't go past endVal since progress can exceed duration in the last frame
        if (self.countDown) {
            self.frameVal = (self.frameVal < self.endVal) ? self.endVal : self.frameVal;
        } else {
            self.frameVal = (self.frameVal > self.endVal) ? self.endVal : self.frameVal;
        }

        // format and print value
        self.d.innerHTML = self.formatNumber(self.frameVal.toFixed(self.decimals));

        // whether to continue
        if (progress < self.duration) {
            self.rAF = requestAnimationFrame(self.count);
        } else {
            if (self.callback != null) self.callback();
        }
    }
    this.start = function(callback) {
        self.callback = callback;
        // make sure values are valid
        if (!isNaN(self.endVal) && !isNaN(self.startVal)) {
            self.rAF = requestAnimationFrame(self.count);
        } else {
            console.log('countUp error: startVal or endVal is not a number');
            self.d.innerHTML = '--';
        }
        return false;
    }
    this.stop = function() {
        cancelAnimationFrame(self.rAF);
    }
    this.reset = function() {
        self.startTime = null;
        cancelAnimationFrame(self.rAF);
        self.d.innerHTML = self.formatNumber(self.startVal.toFixed(self.decimals));
    }
    this.resume = function() {
        self.startTime = null;
        self.duration = self.remaining;
        self.startVal = self.frameVal;
        requestAnimationFrame(self.count);
    }
    this.formatNumber = function(nStr) {
        nStr += '';
        var x, x1, x2, rgx;
        x = nStr.split('.');
        x1 = x[0];
        x2 = x.length > 1 ? self.options.decimal + x[1] : '';
        rgx = /(\d+)(\d{3})/;
        if (self.options.useGrouping) {
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + self.options.separator + '$2');
            }
        }
       // font-size:18px; height:30px; font-family:"黑体"; font-weight:bold;
       return "<div style='color:#003ff0;font-size:90px;text-align: center;line-height: "+height+"px;'>"+x1 + x2+"</div>";
//        return "<div style='font-size:20px; height:30px; font-family:\"黑体\"; font-weight:bold;'>"+x1 + x2+"</div>";
//        return "<div style='color:#fff;font-size:40px;text-align: center;text-shadow:0 0 5px #CCCCCC, 0 0 10px #CCCCCC, 0 0 15px #CCCCCC, 0 0 20px #095816, 0 0 25px #095816, 0 0 30px #095816, 0 0 50px #095816, 0 0 80px #095816, 0 0 100px #095816, 0 0 150px #095816;line-height: "+height+"px;'>"+x1 + x2+"</div>";
    }

    // format startVal on initialization
    self.d.innerHTML = self.formatNumber(self.startVal.toFixed(self.decimals));
}


Ext.define('Esm.portlet.CountUp', {

    extend: 'Esm.portlet.PortletPanel',
    alias: 'widget.countupportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,

    //ajax取回数据
    responseData : function(me,option,data){
        data = data || 0;

        var params = Ext.apply({},me.option.params);
        var options = {
            useEasing : true,
            useGrouping : true,
            separator : ',',
            decimal : '.'
        }
        if(Ext.isNumber(data)){
            var demo = new countUp(me.body.dom, 0, data, 0, 2.5, options,me.body.getHeight());
            demo.start();
        }else if(Ext.isArray(data)){
            Ext.each(data,function(count,index){
               var p = document.createElement('p');
                p.innerHTML = '<span>'+count.title+':</span>';
                var c = document.createElement('div');

               var demo =  new countUp(me.body.dom, 0, count.value, 0, 2.5, options,me.body.getHeight());
               demo.start();
                p.appendChild(c);
                me.body.dom.appendChild(p)
             //  Ext.DomHelper.append(me.body.dom,p)
             //  Ext.DomHelper.append(e,c)
            })
        }

    },

    //portlet框架加载完毕后，初始化body内容
    boxready : function(me){


    }
});
