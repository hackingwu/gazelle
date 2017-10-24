//grails.app.title = "企业应用快速开发平台"
//grails.app.subtitle = "创新来自偷懒，快乐来自分享"
//grails.app.footer = "版权所有 © 网龙计算机有限公司 1999 - 2014"
//
////----------------------------------------------
//// 导航菜单相关配置
////----------------------------------------------
//cube.navigation.home=[title:"系统首页",controller:"desktop",action: "index"] //系统首页
//
//cube.navigation.groups=[
//        [code: "ScaffoldingDemo1", label: "基本控件"],
//        [code: "ScaffoldingDemo2", label: "基本图表"]
//]
//
//cube.navigation.items=[
//        ScaffoldingDemo1:[
//                [controller:"viewobjects",action: "index", label: "viewobjects", dir: "/images/icons/blueberry", img: "options_2.png", plugin: "scaffolding"]
//        ],
//
//        ScaffoldingDemo2:[
//                [controller:"chart",action: "line", label: "折线图", dir: "/images/icons/blueberry", img: "tag_green.png", plugin: "scaffolding"],
//                [controller:"chart",action: "pie", label: "饼图", dir: "/images/icons/blueberry", img: "tag_orange.png", plugin: "scaffolding"],
//                [controller:"chart",action: "bar", label: "柱状图", dir: "/images/icons/blueberry", img: "tag_red.png", plugin: "scaffolding"]
//        ]
//]
//
////----------------------------------------------
//// 权限相关配置
////----------------------------------------------
//
////系统角色
//cube.auth.roles=[
//        [code: "ROLE_ADMIN", name: "管理员"],
//        [code: "ROLE_USER", name: "用户"]
//]
////角色菜单(采用黑名单方式)
//cube.auth.menus.disable=[
//        ROLE_ADMIN: [],
//        ROLE_USER:  ["sysUserIndex","vipInfoIndex","bugTypeIndex","appInfoIndex"]
//]
//
////角色按钮(采用黑名单方式)
//cube.auth.buttons.disable=[
//        ROLE_ADMIN: [:
//        ],
//        ROLE_USER: [
//                vipInfoIndex:["create","update","delete"]
//        ]
//]
//
////对象属性权限限制(采用黑名单方式)
//cube.auth.attributes.disable=[
//        ROLE_ADMIN: [:
//
//        ],
//        ROLE_USER: [
//                teacher: ["gender"],
//                programmer: ["description"]
//        ]
//]
//
////----------------------------------------------
//// 桌面菜单相关配置
////----------------------------------------------
//cube.desktop.portal = [
//        columns :3,
//        rows :3,
//        dataUrl : "desktop/porletData",
//
//        orders : [
//                "LINE_PORLET",
//                "BAR_PORLET",
//                "COUNTER_PORLET",
//                "HTML_PORLET",
//                "MSG_PORLET",
//                "GRID_PORLET",
//                "GAUGE_PORLET",
//                "MAP_PORLET",
//                "COLUMN_PORLET",
//                "PIE_PORLET",
//                "DYNAMIC_PORLET",
//                "MULTIGAUGE_PORLET"
//        ],
//
//        porlets : [
//                LINE_PORLET:[
//                        xtype: "chartlineportlet",
//                        title:"折线",
//                        options : [
//                                lines :[
//                                        "成交1","y轴"
//                                ],
//                                x : ['一','二','周三','周四','周五','周六','周日']
//                        ],
//                        data: {  params,session,grailsApplication ->
//                            //def option = """{title:"标题修改",data:[[10, 12, 210, 54, 260, 830, 710],[1320, 1132, 601, 234, 120, 90, 20]]} """
//                            return [title:"折线图",data:[[10, 12, 210, 54, 260, 830, 710],[1320, 1132, 601, 234, 120, 90, 20]]]
//                        }
//                ],
//                GRID_PORLET : [
//                        xtype: "gridportlet",
//                        title:"BUG信息",
//                        options : [
//                                columns: [[
//                                                  text   : '版本',
//                                                  width: 65,
//                                                  // sortable : true,
//                                                  dataIndex: 'name'
//                                          ],[
//                                                  text   : 'BUG类型',
//                                                  flex: 1,
//                                                  // sortable : true,
//                                                  //renderer : this.change,
//                                                  dataIndex: 'class'
//                                          ],
//                                          [
//                                                  text   : '发生时间',
//                                                  width    : 250,
//                                                  // sortable : true,
//                                                  //renderer : this.change,
//                                                  dataIndex: 'age'
//                                          ]]
//                        ],
//                        data: { params,session,grailsApplication ->
//                            //def option = '[["天亮","客服","18"],["天亮","vip客服","35"],["天晴","hkk","20"]]'
//                            return [data:[["V1.0","内存溢出","2014-7-4 09:43:00"],["V1.1","指针异常","2014-7-4 09:43:08"],["V1.2","其他","2014-7-4 09:43:16"]]]
//                        }
//                ],
//                BAR_PORLET:[
//                        xtype: "chartbarportlet",
//                        title:"柱状图",
//                        options : [
//                                bars :[
//                                        "降雨量","污染指数"
//                                ],
//                                x :  ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
//                        ],
//                        data: { params,session,grailsApplication ->
//                            //def option = """[[10, 12, 210, 54, 260, 830, 710],[1320, 1132, 601, 234, 120, 90, 20]] """
//                            return [data:[[10, 12, 210, 54, 260, 830, 710],[1320, 1132, 601, 234, 120, 90, 20]]]
//                        }
//                ],
//                COUNTER_PORLET : [
//                        xtype: "countupportlet",
//                        title:"计数器",
//                        options : [:],
//                        data: {  params,session,grailsApplication ->
//
//                            return [title:'计数',data:10000]
//                        }
//                ],
//                HTML_PORLET : [
//                        xtype: "htmlportlet",
//                        title:"HTML内容",
//                        options : [:],
//                        data: {  params,session,grailsApplication ->
//
//                            return [title: '通知公告', data:"""
//                            <style type="text/css">
//                            #notice { margin: 5px 5px 5px 5px; border: 1px solid #999; padding: 5px 5px 5px 5px; border-radius:10px;}
//                            li{list-style: none;}
//                            h2 {text-align: center;}
//                            </style>
//                            <div id="notice">
//                                <h2>重要通知</h2>
//                                <p>根据《中华人民共和国宪法》和相关法律法规规定，在保护公民合法言论自由的同时，禁止利用互联网、通讯工具、媒体以及其他方式从事以下行为：</p>
//                                <ul>
//                                    <li>一、组织、煽动抗拒、破坏宪法和法律、法规实施的。</li>
//                                    <li>二、捏造或者歪曲事实，散布谣言，妨害社会管理秩序的。</li>
//                                    <li>三、组织、煽动非法集会、游行、示威、扰乱公共场所秩序的。</li>
//                                    <li>四、从事其他侵犯国家、社会、集体利益和公民合法权益的。</li>
//                                </ul>
//                                <p>管理部门将依法严加监管上述行为并予以处理；对构成犯罪的，司法机关将追究刑事责任。</p>
//                            <div>
//                        """]
//                        }
//                ],
//                MAP_PORLET : [
//                        xtype: "chartchinamapportlet",
//                        title:"HTML内容",
//                        options : [
//                                maps:['groovy']
//                        ],
//                        data: {  params,session,grailsApplication ->
//
//                            List data = [
//
//                                    [ [name: '北京',value: Math.round(Math.random()*1000)],
//                                      [name: '天津',value: Math.round(Math.random()*1000)],
//                                      [name: '上海',value: Math.round(Math.random()*1000)],
//                                      [name: '重庆',value: Math.round(Math.random()*1000)],
//                                      [name: '河北',value: Math.round(Math.random()*1000)],
//                                      [name: '河南',value: Math.round(Math.random()*1000)],
//                                      [name: '云南',value: Math.round(Math.random()*1000)],
//                                      [name: '辽宁',value: Math.round(Math.random()*1000)],
//                                      [name: '黑龙江',value: Math.round(Math.random()*1000)],
//                                      [name: '湖南',value: Math.round(Math.random()*1000)],
//                                      [name: '安徽',value: Math.round(Math.random()*1000)],
//                                      [name: '山东',value: Math.round(Math.random()*1000)],
//                                      [name: '新疆',value: Math.round(Math.random()*1000)],
//                                      [name: '江苏',value: Math.round(Math.random()*1000)],
//                                      [name: '浙江',value: Math.round(Math.random()*1000)],
//                                      [name: '江西',value: Math.round(Math.random()*1000)],
//                                      [name: '湖北',value: Math.round(Math.random()*1000)],
//                                      [name: '广西',value: Math.round(Math.random()*1000)],
//                                      [name: '甘肃',value: Math.round(Math.random()*1000)],
//                                      [name: '山西',value: Math.round(Math.random()*1000)],
//                                      [name: '内蒙古',value: Math.round(Math.random()*1000)],
//                                      [name: '陕西',value: Math.round(Math.random()*1000)],
//                                      [name: '吉林',value: Math.round(Math.random()*1000)],
//                                      [name: '福建',value: Math.round(Math.random()*1000)],
//                                      [name: '贵州',value: Math.round(Math.random()*1000)],
//                                      [name: '广东',value: Math.round(Math.random()*1000)],
//                                      [name: '青海',value: Math.round(Math.random()*1000)],
//                                      [name: '西藏',value: Math.round(Math.random()*1000)],
//                                      [name: '四川',value: Math.round(Math.random()*1000)],
//                                      [name: '宁夏',value: Math.round(Math.random()*1000)],
//                                      [name: '海南',value: Math.round(Math.random()*1000)],
//                                      [name: '台湾',value: Math.round(Math.random()*1000)],
//                                      [name: '香港',value: Math.round(Math.random()*1000)],
//                                      [name: '澳门',value: Math.round(Math.random()*1000)]
//                                    ]]
//                            return [title: '中国地图', name:'groovy',data:data]
//                        }
//                ],
//                PIE_PORLET : [
//                        xtype: "chartpieportlet",
//                        title:"饼图",
//                        options : [:],
//                        data: { params,session,grailsApplication ->
//                            List data = [
//                                    [value:335, name:'直接访问'],
//                                    [value:310, name:'邮件营销'],
//                                    [value:2340, name:'联盟广告'],
//                                    [value:1350, name:'视频广告'],
//                                    [value:1548, name:'搜索引擎']
//                            ]
//                            return [data:data]
//                        }
//                ],
//                GAUGE_PORLET : [
//                        xtype: "chartgaugeportlet",
//                        title:"仪表盘",
//                        options : [:],
//                        data: {  params,session,grailsApplication ->
//                            List data=[[value: 50, name: '']]
//                            return [data:data]
//                        }
//                ],
//                MULTIGAUGE_PORLET : [
//                        xtype: "chartmultigaugeportlet",
//                        title:"多仪表盘",
//                        options : [:],
//                        data: {  params,session,grailsApplication ->
//                            List data=[[value: 40, name: ''],
//                                       [value: 1.5, name: ''],
//                                       [value: 0.5, name: '']
//                            ]
//                            return [data:data]
//                        }
//                ]
////                NET_PORLET : [
////                        xtype: "chartnetportlet",
////                        title:"网状图",
////                        options : [category : [[name:'人物', itemStyle: [normal:[color :'#ff7f50']]],
////                                               [name:'家人',itemStyle: [normal:[color : '#87cdfa']]],
////                                               [name:'朋友',itemStyle: [normal:[color : '#9acd32']]]]],
////                        data: {  params,session,grailsApplication ->
////
////                            return [data:[
////                                    nodes:[
////                                            [category:0, name: '乔布斯', value : 10],
////                                            [category:1, name: '丽萨-乔布斯',value : 2],
////                                            [category:1, name: '保罗-乔布斯',value : 3],
////                                            [category:1, name: '克拉拉-乔布斯',value : 3],
////                                            [category:1, name: '劳伦-鲍威尔',value : 7],
////                                            [category:2, name: '史蒂夫-沃兹尼艾克',value : 5],
////                                            [category:2, name: '奥巴马',value : 8],
////                                            [category:2, name: '比尔-盖茨',value : 9],
////                                            [category:2, name: '乔纳森-艾夫',value : 4],
////                                            [category:2, name: '蒂姆-库克',value : 4],
////                                            [category:2, name: '龙-韦恩',value : 1]
////                                    ],
////                                    links:[
////                                            [source : 1, target : 0, weight : 1],
////                                            [source : 2, target : 0, weight : 2],
////                                            [source : 3, target : 0, weight : 1],
////                                            [source : 4, target : 0, weight : 2],
////                                            [source : 5, target : 0, weight : 3],
////                                            [source : 6, target : 0, weight : 6],
////                                            [source : 7, target : 0, weight : 6],
////                                            [source : 8, target : 0, weight : 1],
////                                            [source : 9, target : 0, weight : 1],
////                                            [source : 10, target : 0, weight : 1],
////                                            [source : 3, target : 2, weight : 1],
////                                            [source : 6, target : 2, weight : 1],
////                                            [source : 6, target : 3, weight : 1],
////                                            [source : 6, target : 4, weight : 1],
////                                            [source : 6, target : 5, weight : 1],
////                                            [source : 7, target : 6, weight : 6],
////                                            [source : 7, target : 3, weight : 1],
////                                            [source : 9, target : 6, weight : 1]
////                                    ]
////                            ]
////                            ]
////                        }
////                ]
//
//        ]
//]
//
////----------------------------------------------
//// viewobjects页面对象配置
////----------------------------------------------
//viewobjects {
//    viewobjects {
//        title = '学生管理'
//        type = 'standard'
//        fields = [
//                [type:'string',name:'name',cn: "姓名",constraints:[blank:false,nullable:true,attributes:[default: '小张']],],
//                [type:'int',name:'age',cn: "年龄"],
//                [type:'string',name:'phone',cn: "电话"],
//                [type:'string',name:'addr',cn: "地址"]
//        ]
//    }
//}
//
