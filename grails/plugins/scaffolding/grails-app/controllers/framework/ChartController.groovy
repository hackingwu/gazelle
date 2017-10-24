package framework

import grails.converters.JSON

/**
 * 图表演示界面
 * @author xufb
 * @version 2014-07-22
 */
class ChartController {

    /**
     * 默认index跳转至line页面
     * @return
     */
    def index() {
        render(view: "line.gsp")
    }

    /**
     * 折线图界面
     */
    def line() {

    }

    /**
     * 柱状图界面
     */
    def bar() {

    }

    /**
     * 饼图界面
     */
    def pie() {

    }

    /**
     * 折线图数据
     * @return
     */
    def lineChart(){
        Map r = [title:"标题修改",data:[[100, 120, 210, 54, 260, 830, 710],[320, 1132, 601, 234, 120, 90, 20]]]

        if(params['dateTime'] == null || params['dateTime'] == ""){
	        r =[option: [title: [text:'标题修改'],
	                        legend:[data:['德国','葡萄牙']],
	                        xAxis:[[type: 'category',data:['周一', '周二', '周三', '周四', '周五', '周六', '周日']]],
	                        yAxis:[[type: 'value']],
	                        series :[[name:'德国',type:'line',data:[1000, 120, 2100, 54, 260, 830, 10],
	                                  markPoint : [data : [[type : 'max', name: '最大值'],[type : 'min', name: '最小值']]],
	                                  markLine : [ data : [[type : 'average', name: '平均值']]] ],
	                                 [name:'葡萄牙',type:'line',data:[320, 1132, 61, 234, 120, 90, 1200],
	                                  markPoint : [data : [[type : 'max', name: '最大值'],[type : 'min', name: '最小值']]],
	                                  markLine : [ data : [[type : 'average', name: '平均值']]] ],
	                                ]
	        ]]
        }
        render r as JSON
    }

    /**
     * 柱状图数据
     * @return
     */
    def barChart(){
        Map r = [title:"标题修改",data:[[100, 120, 210, 54, 260, 830, 710],[320, 1132, 601, 234, 120, 90, 20]]]

        if(params['dateTime'] == null || params['dateTime'] == ""){
	        r =[option: [title: [text:'标题修改'],
	                     legend:[data:['德国','葡萄牙']],
	                     xAxis:[[type: 'category',data:['周一', '周二', '周三', '周四', '周五', '周六', '周日']]],
	                     yAxis:[[type: 'value']],
	                     series :[[name:'德国',type:'bar',data:[1000, 120, 2100, 54, 260, 830, 10],
	                               markPoint : [data : [[type : 'max', name: '最大值'],[type : 'min', name: '最小值']]],
	                               markLine : [ data : [[type : 'average', name: '平均值']]] ],
	                              [name:'葡萄牙',type:'bar',data:[320, 1132, 61, 234, 120, 90, 1200],
	                               markPoint : [data : [[type : 'max', name: '最大值'],[type : 'min', name: '最小值']]],
	                               markLine : [ data : [[type : 'average', name: '平均值']]] ],
	                     ]
	        ]]
        }
        render r as JSON
    }

    /**
     * 饼图数据
     * @return
     */
    def pieChart(){
        Map data = [data:[
                [value:335, name:'直接访问'],
                [value:310, name:'邮件营销'],
                [value:2340, name:'联盟广告'],
                [value:1350, name:'视频广告'],
                [value:1548, name:'搜索引擎']
        ]]
        render data as JSON
    }
}
