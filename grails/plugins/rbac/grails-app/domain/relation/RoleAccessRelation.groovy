package relation
/**
 * 角色资源关联表
 * @author  lch
 * @version 2014-11-17
 */
class RoleAccessRelation {
    long sysRoleId //角色id
    long btnAndMenuId //资源id
    static constraints = {
        sysRoleId  attributes:[cn: "角色", widget: "singleselectfield",domain: "sysRole",include:"name",displayField:"name"]
        btnAndMenuId  attributes:[cn: "资源", widget: "singleselectfield",domain: "btnAndMenu",include:"title",displayField:"title"]
    }

    static m =[
            domain:[cn: "系统角色"],
            layout:[type: "standard"]
    ]
}
