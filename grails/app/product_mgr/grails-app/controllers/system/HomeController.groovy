package system

class HomeController {
    def sysAdminService
    def sysRoleService

    def index() {
        String userName = ''
        String userRole = ''
        String id = session['adminId']
        if(id != null){
            def sysAdmin = sysAdminService.findById(id)
            if(sysAdmin != null){
                userName = sysAdmin.name
                if(sysAdmin.properties.containsKey("sysRoleId")){
                    userRole = sysRoleService.findById(Long.valueOf(session['sysRoleId']))?.name
                }else{
                    userRole = "管理员"
                }
            }
        }

        [userName:userName,userRole:userRole]
    }
}
