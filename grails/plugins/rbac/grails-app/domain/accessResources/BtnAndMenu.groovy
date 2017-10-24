package accessResources
/**
 * 菜单按钮资源项
 * @author lch
 * @version 2014-11-12
 **/
class BtnAndMenu {
    String title           //菜单操作按钮管理时显示名称，列表及授权时显示名称
    String label            //title的别名操作按钮在界面上显示的名称
    Boolean isMenu = false  //true：菜单,false:操作按钮
    String code             //标识，用于上下级处理,菜单通常是controller的名称，操作:controller_action
    long parentId           //上级节点code,空:一级节点，按钮上级通常为menu
    String controller       //Controller的名称
    String action           //Action名称
    int showOrder           //显示顺序
    String link             //点击链接
    Date click              //点击触发时间

    static constraints = {
        title attributes:[cn: "菜单按钮名称"]
        label attributes:[cn: "功能名称"]
        isMenu attributes:[cn: "是否菜单"]
        code attributes:[cn: "标示"]
        parentId attributes:[cn: "上级节点Id"],nullable: true
        controller attributes:[cn: "Controller名称"],nullable: true
        action attributes:[cn: "Action名称"],nullable: true
        showOrder attributes:[cn: "显示顺序"],nullable: true
        link attributes:[cn: "点击链接"],nullable: true
        click attributes:[cn: "点击触发时间"],nullable: true
    }

    String toString(){
        return code
    }

    static m =[
            domain:[cn: "菜单按钮资源项"],
            layout:[type: "standard"]
    ]
}
