package user
/**
 * 系统用户的基类
 * @author  lch
 * @version 2014-11-20
 */
class SysUser {
    long sysRoleId //资源id
    static constraints = {
        sysRoleId  attributes:[cn: "管理员角色", relation: "ManyToOne",domain: "sysRole",include:"name", nullable: false]
    }
}
