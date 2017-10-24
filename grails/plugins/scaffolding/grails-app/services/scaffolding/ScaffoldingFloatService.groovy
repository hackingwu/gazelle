package scaffolding
/**
 * 生成Float类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingFloatService {
    static transactional = false

    def grailsApplication

    String Scaffolding(Map field, String mode,String domainName){
        Map constraint = field.constraint

        String output = """{xtype: 'numberfield', fieldLabel: '${field.cn}', name: '${field.name}', decimalPrecision: 6"""

        if(constraint.max != null){
            output += """, maxValue: ${constraint.max}"""
        }

        if(constraint.min != null){
            output +=""", minValue: ${constraint.min}"""
        }

        if(mode == "detail")
        {
            output += ", readOnly:true"
        }else if(constraint.default!=null){
            output = output + ", value:${constraint.default}"
        }

        output += "}"

        return output
    }
}
