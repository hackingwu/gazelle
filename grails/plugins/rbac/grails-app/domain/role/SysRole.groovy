package role

/**
 * 角色表
 * @author  lch
 * @version 2014-11-17
 */
class SysRole {
    String      name            //角色名



    static constraints = {
        name              attributes:[cn: "角色名"],size:1..127

    }

    String toString(){
        return name
    }

    static m =[
            domain:[cn: "系统角色"],
            layout:[type: "standard"]
    ]
}
