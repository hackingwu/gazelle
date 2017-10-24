Ext.onReady(function(){
    //身份证验证
    Ext.apply(Ext.form.VTypes,{
        identity : function(val,field){
            val = strTrim(val)
//            alert("************"+val)
            var win = eval(field.identity.form)
            var cardType = win.down('form').getForm().findField('cardType').getValue()
            if(cardType=='identity'||cardType==1){
                if(val.length != 18){
                    return false
                }else{
                    return isCardId(val)
                }
            }else{
                return true
            }
        },
        identityText : '身份证无效'
    });
});

function isCardId(cardNo){
    var year = cardNo.substring(6, 10);
    var month = cardNo.substring(10, 12);
    var day = cardNo.substring(12, 14);
    var birthday = new Date(year, parseFloat(month) - 1,
        parseFloat(day));
    //判断日期的有效性
    if (birthday.getFullYear() != parseFloat(year)
        || birthday.getMonth() != parseFloat(month) - 1
        || birthday.getDate() != parseFloat(day)) {
        return false;
    }
    //检查地区
    var city={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"海外"};
    if(!city[cardNo.substring(0,2)]){
        return false
    }
    //检查校验和
    var s = cardNo.substring(0,17).split('').reverse();
    function W(i){
        return Math.pow(2,i-1) % 11;
    }
    function S(){
        var sum = 0;
        for (var j=0;j<17;j++){
            sum += s[j]*W(j+2);
        }

        return sum;
    }
    var checkSum = (12-(S() % 11)) % 11;
    if(checkSum==10){
        if(cardNo[17]!='X'&& cardNo[17]!='x'){
            return false
        }
    }else{
        if(checkSum!=cardNo[17]){
            return false
        }
    }

    return true
}

function strTrim(s)
{
    return s.replace(/(^\s*)|(\s*$)/g,"");
}

/*
 *弹出窗口自适应
 *@param objWnd 弹出的窗口对象
 */
function windowResize(objWnd){
    var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
    if(document.getElementById(objWnd.down('form').id).scrollHeight>autoHeight){
        objWnd.down('form').setHeight(autoHeight);
        //objWnd.down('form').setWidth(750);
        objWnd.center();
    }
}

//全局util
Ext.cubeUtil = {
    /**
     * @从html代码中提取内容
     **/
    noHtml : function(strHtml){
        var ret = '';
        if(strHtml != null){
            var divEl=document.createElement('div');
            divEl.innerHTML = strHtml
            ret = (divEl.textContent || divEl.innerText || "");
        }
        return ret;
    }

};
