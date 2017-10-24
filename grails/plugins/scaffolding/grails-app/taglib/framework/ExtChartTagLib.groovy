package framework
/**
 *echart图表相关的方法配置
 * @className ExtChartTagLib
 * @author Su sunbin
 * @version 2014-09-16 ExtTagLib中的echart图表相关的方法拆分至此
 */
import grails.converters.JSON

class ExtChartTagLib extends ExtTagLib{
//	static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']
	/**
	 * echart图表生成的配置
	 */
	def extChart = { attrs, body ->
		String dataUrl = attrs['dataUrl']
		Map option = attrs['option']
		List conditions = attrs['conditions']
		Map params = attrs['params']
		String chartType = attrs['chartType']

		if(params == null) {
			params = [:]
		}

		if(conditions == null){
			conditions = []
		}
		if(option == null) {
			option = [:]
		}

		String output = """Ext.create('Esm.echart.EsmChart',{dataUrl:'${dataUrl}',chartType:'${chartType}',
                            option : ${option as JSON},
							params:${params as JSON},
							conditions:${conditions as JSON}
                         });"""

		out << output

	}
	/**
	 *桌面的portal的相关配置
	 */
	def extPortal = {attrs, body ->
		List orders = grailsApplication.config.cube.desktop.portal.orders

		int columns = grailsApplication.config.cube.desktop.portal.columns

		Map porlets = grailsApplication.config.cube.desktop.portal.porlets

		String dataUrl = grailsApplication.config.cube.desktop.portal.dataUrl

		int rows = grailsApplication.config.cube.desktop.portal.rows

		int width = grailsApplication.config.cube.desktop.portal.width?:1024

		int height = grailsApplication.config.cube.desktop.portal.height?:512

		List items = []

		String roles =  session["roles"]

		Map rolesPortal = grailsApplication.config.cube.desktop.portal.auths

		orders.each{
			def porlet = porlets.get(it)

			if(porlet != null){
				def m = [:];
				porlet.each({key,value->
					if("options" == key){
						def p = value.get("params")
						if(p != null){
							p.put("data",it)
						}else {
							value.put("params",[data:it])
						}

					}
					if("data" != key) {
						m.put(key,value)
					}
				})
//				if(rolesPortal != null && rolesPortal.containsKey(roles) && rolesPortal.get(roles).contains(it)){
//
//				}
                if(authService.Button(session["sysRoleId"], it)){
                    items.add(m)
                }
			}
		}

		//${g.resource([dir: dataUrl])}
		out << "  Ext.onReady(function(){var width = document.documentElement.clientWidth;var height = document.documentElement.clientHeight;Ext.create('Esm.app.Desktop', "
		out << """{"columns":(width<=${width}||height<=${height})?1:${columns},"rows":(width<=${width}||height<=${height})?1:${rows},"dataUrl":"${g.resource([file: dataUrl])}","items":${items as JSON} }"""
//		out << ([columns :columns,rows:rows,dataUrl:"${g.resource([file: dataUrl])}",items:items] as JSON)
		out <<  ");});"
	}

}
