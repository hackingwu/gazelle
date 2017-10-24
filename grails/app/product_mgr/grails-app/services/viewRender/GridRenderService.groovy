package viewRender

import grails.transaction.Transactional
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

@Transactional
class GridRenderService {
    def grailsApplication

    /**
     * 导入导出管理表格渲染
     */
    String ImportInfoFilePathRender(){
        def g = grailsApplication.mainContext.getBean(ApplicationTagLib.class)
        String output="""
            renderer:function(value){
                  //return '<a href="#" onclick="window.open(\\'${g.resource([dir: '/importFile/error', file: ""])}/'+value+'\\');">'+value+'</a>'
                  var title = "点击下载失败记录"
                  if(value.match(/^export/)){
                    title = "点击下载导出记录"
                  }

                  return value==''?'': '<a href="#" onclick="window.open(\\'${g.createLink([controller: 'importInfo', action: 'exportFile'])}?filename='+value+'\\');">'+title+'</a>'
            }
        """

        return output
    }

    /**
     * @author lichb
     * @version 2014/11/20 活动管理表格的活动时间格式化显示
     */
    String beginDateRender(){
        String output = """
            renderer:function(value){
//                return value.replace(/(\\d+-\\d+-\\d+)\\s+.+/gi,"\$1");
                return value;
            }
        """

        return output
    }

    String customerToolTipRender(){
        String output = """
                        //grid行tooltip功能
                        render:function(_self, opts){
                            var view = _self.getView();
                            var tip = Ext.create('Ext.tip.ToolTip', {
                                // The overall target element.
                                target: _self.getEl(),
                                // Each grid row causes its own separate show and hide.
                                delegate: view.itemSelector,
                                // Moving within the row should not hide the tip.
                                trackMouse: true,
                                // Render immediately so that tip.body can be referenced prior to the first show.
                                renderTo: Ext.getBody(),
                                dismissDelay: 15000,
                                trackMouse: true,
                                listeners: {
                                    beforeshow: function updateTipBody(tip) {
                                        var data = view.getRecord(tip.triggerElement).raw;
                                        var cardType = "身份证";
                                        switch(data.cardType){
                                            case 1:
                                                cardType = "身份证";
                                                break;
                                            case 2:
                                                cardType = "护照";
                                                break;
                                            case 3:
                                                cardType = "军官证";
                                                break;
                                            case 4:
                                                cardType = "港澳居民往来内地通行证";
                                                break;
                                            case 5:
                                                cardType = "台湾居民往来大陆通行证";
                                                break;
                                        }

                                        var political="群众";
                                        switch(data.political){
                                            case 1:
                                                political = "群众";
                                                break;
                                            case 2:
                                                political = "中国共产党党员";
                                                break;
                                            case 3:
                                                political = "中国共产主义青年团团员";
                                                break;
                                            case 4:
                                                political = "中国国民党革命委员会会员";
                                                break;
                                            case 5:
                                                political = "中国民主同盟盟员";
                                                break;
                                            case 6:
                                                political = "中国民主建国会会员";
                                                break;
                                            case 7:
                                                political = "中国民主促进会会员";
                                                break;
                                            case 8:
                                                political = "中国农工民主党党员";
                                                break;
                                            case 9:
                                                political = "中国致公党党员";
                                                break;
                                            case 10:
                                                political = "九三学社社员";
                                                break;
                                            case 11:
                                                political = "台湾民主自治同盟盟员";
                                                break;
                                            case 12:
                                                political = "无党派民主人士";
                                                break;
                                        }

                                        var degree = "本科";
                                        switch(data.degree){
                                            case 0:
                                                degree = "其他";
                                                break;
                                            case 1:
                                                degree = "小学";
                                                break;
                                            case 2:
                                                degree = "初中";
                                                break;
                                            case 3:
                                                degree = "高中";
                                                break;
                                            case 4:
                                                degree = "中技";
                                                break;
                                            case 5:
                                                degree = "中专";
                                                break;
                                            case 6:
                                                degree = "大专";
                                                break;
                                            case 7:
                                                degree = "本科";
                                                break;
                                            case 8:
                                                degree = "硕士";
                                                break;
                                            case 9:
                                                degree = "博士";
                                                break;
                                        }

                                        var tipInfo = "<table>"
                                                    + "<tr><td>姓名：" + data.name + "</td><td width='5'>&nbsp;</td><td>性别：" + (data.gender==1?'男':'女') + "</td></tr>"
                                                    + "<tr><td>民族：" + data.nation + "</td><td width='5'>&nbsp;</td><td>出生年月：" + data.birthday + "</td></tr>"
                                                    + "<tr><td>证件类型：" + cardType + "</td><td width='5'>&nbsp;</td><td>证件号码：" + data.cardId + "</td></tr>"
                                                    + "<tr><td>手机号：" + data.mobile + "</td><td width='5'>&nbsp;</td><td>政治面貌：" + political + "</td></tr>"
                                                    + "<tr><td>最高学历：" + degree + "</td><td width='5'>&nbsp;</td><td>特长/技能：" + data.skill + "</td></tr>"
                                                    + "<tr><td>服务领域：" + data.intentionTypes + "</td><td width='5'>&nbsp;</td><td>服务区域：" + data.area + "</td></tr>";

                                        tipInfo += (data.isSignIn?("<tr><td>签到时间：" + data.signInTime + "</td><td width='5'>&nbsp;</td><td>签到地点：" + (data.signInAddr?data.signInAddr:"") + "</td></tr>"):"");
                                        tipInfo += (data.isSignOut?("<tr><td>签退时间：" + data.signOutTime + "</td><td width='5'>&nbsp;</td><td>签退地点：" + (data.signOutAddr?data.signOutAddr:"") + "</td></tr>"):"");
                                        tipInfo += (data.duration?("<tr><td>时长：" + data.duration + "</td></tr>"):"");

                                        tipInfo += "</table>";

                                        tip.update(tipInfo);
                                    }
                                }
                            });
                        }
        """
        return output

    }


}
