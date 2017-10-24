package system

import framework.biz.GenericService
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SysAdminService extends GenericService<SysAdmin,Long> {

    public SysAdminService(){
        super(SysAdmin.class)

    }

}
