package framework

import grails.converters.JSON

class ViewobjectsController {

    def grailsApplication

    def index() {

    }

    /**
     * AUTO: 提供列表数据
     */
    def list() {
        String output
        try {
            output = [success: true, message: "", totalCount: 1, data: [[id:1, name:"学生0", age:20, phone:"18705087658", addr:"福建省福州市鼓楼区"],[id:2, name:"学生1", age:22, phone:"18705087658", addr:"福建省福州市鼓楼区"],[id:3, name:"学生3", age:19, phone:"18705087658", addr:"福建省福州市鼓楼区"]]] as JSON
        }catch (e){
            output = [success: false, message: "请输入正确的查询条件！"] as JSON
        }
        render output
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){

        String output = [
                success: true,
                message: "",
                data: [id:1, name:"学生0", age:20, phone:"18705087658", addr:"福建省福州市鼓楼区"]
        ] as JSON

        render output
    }
}
