/**
 * 页面事件处理模块
 * @author lcb
 **/

/*
 * 联动事件处理对象
 * @param 
 **/
Ext.eventcore = function(){

    /*
    function _fireEvents(e, f, el){
        
    }
    */
    return {
        /**
         * @逐个触发联动事件的组件
         **/
        fireEvents : function(e, form, objList, val){
            var objArry = objList.split(",");
            for(var k=0;k<objArry.length;k++){
                try{ //哥该不该try你呢，try你，哥就看不到异常了
                    form.down('[name="'+objArry[k]+'"]').fireEvent(e,{value:val/*obj.value*/}); //逐个触发注册事件
                }catch(e){
                    continue;
                }
            }
        }
    };
}();
