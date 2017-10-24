package system

import user.SysUser

class SysAdmin extends SysUser{

    String name        //真实姓名
    String login       //账号
    String password    //密码

    static constraints = {
        name              attributes:[cn: "真实姓名"],size:1..127
        login             attributes:[cn:"账号"], nullable: false, unique: true
        password          attributes:[cn:"密码", password:true, notice: '密码长度6-16位，不能包含空格！',regex:/^[^\s]{6,16}$/], nullable: false
    }

    String toString(){
        return name
    }

    static m =[
            domain:[cn: "管理员"],
            layout: [type: "standard"]
    ]
}