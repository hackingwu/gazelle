import accessResources.BtnAndMenu
import com.nd.grails.plugins.log.CacheType
import com.nd.grails.plugins.log.LogService
import demo.FormTreeView
import demo.FormTreeViewService
import demo.FormView
import demo.Organization
import framework.util.MD5Util
import grails.converters.JSON
import org.apache.commons.lang.time.FastDateFormat
import relation.RoleAccessRelation
import relation.RoleAccessRelationService
import role.SysRole
import role.SysRoleService
import system.SysAdmin
import system.SysAdminService

class BootStrap {

    LogService logService
    SysRoleService sysRoleService
    RoleAccessRelationService roleAccessRelationService
    SysAdminService sysAdminService
    FormTreeViewService formTreeViewService
    def init = { servletContext ->
        JSON.registerObjectMarshaller(Date){
            FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault())
            if (it == null) return null
            return format.format(it)
        }
        //初始化日志服务
        logService.Init(CacheType.memory)

        //系统角色初始化
        if (SysRole.count == 0) {
            SysRole sysRole = new SysRole()
            sysRole.name = "超级管理员"
            sysRoleService.save(sysRole)
        }

        //权限列表初始化
        List<RoleAccessRelation> list = []
        Long sysRoleId = sysRoleService.findByProperties([name: "超级管理员"])[0]?.id
        BtnAndMenu.findAll()?.each {
            if (!roleAccessRelationService.findByProperties([sysRoleId: sysRoleId, btnAndMenuId: it.id])) {
                RoleAccessRelation roleAccessRelation = new RoleAccessRelation()
                roleAccessRelation.btnAndMenuId = it.id
                roleAccessRelation.sysRoleId = sysRoleId
                list.add(roleAccessRelation)
            }
        }
        roleAccessRelationService.saveBatch(list)

        //系统管理员初始化
        if (SysAdmin.count == 0) {
            if (SysAdmin.findByLogin('admin') == null) {
                SysAdmin admin = sysAdminService.save(new SysAdmin(
                        name: "系统管理员",
                        login: "admin",
                        sysRoleId: SysRole.findByName('超级管理员')?.id,
                        password: MD5Util.getMD5String("admin")
                ))
            }
        }

        FormTreeView formTreeView
        try{
            if(Organization.count == 0)
            {
                //oneToManyTreeView数据构造 add by lichb 2014-6-9
                Organization root = new Organization(parentId:0,name: "云办公", abbreviation: "ROOT")

                formTreeView = new FormTreeView(
                        companyId:Organization.findByParentId(0)? Organization.findByParentId(0).id : 1,
                        name:"这是一条规则",
                        gender:"保密",
                        date:new Date(),
                        flag:true
                ).save()
                root.formTreeViewId = formTreeView.id
                root.save()
                Organization wl = new Organization(parentId:root.id,name: "网龙", abbreviation: "ND").save()
                Organization c8 = new Organization(parentId:root.id,name: "851", abbreviation: "851").save()
                Organization im = new Organization(parentId:root.id,name: "TM公司", abbreviation: "IM").save()
                Organization cbb = new Organization(parentId:root.id,name: "A项目筹备部", abbreviation: "CBB").save()
                Organization bd = new Organization(parentId:root.id,name: "百度91无线", abbreviation: "BD").save()

                Organization tq2 = new Organization(parentId:wl.id,name: "天晴数码", abbreviation: "TQ").save()
                Organization tl = new Organization(parentId:wl.id,name: "天亮公司", abbreviation: "TL").save()
                Organization hk = new Organization(parentId:wl.id,name: "香港分公司", abbreviation: "HK").save()
                Organization sh = new Organization(parentId:wl.id,name: "上海分公司", abbreviation: "SH").save()

                Organization yr1 = new Organization(parentId:tq2.id,name: "应用产品一部", abbreviation: "YR1").save()
                Organization yr2 = new Organization(parentId:tq2.id,name: "应用产品二部", abbreviation: "YR2").save()
                Organization yr3 = new Organization(parentId:tq2.id,name: "应用产品三部", abbreviation: "YR3").save()

                Organization bb = new Organization(parentId:yr1.id,name: "应用产品一部本部", abbreviation: "BB").save()
                Organization chb = new Organization(parentId:yr1.id,name: "应用产品一部策划部", abbreviation: "CH").save()
                Organization kfb = new Organization(parentId:yr1.id,name: "应用产品一部开发组", abbreviation: "KF").save()
                Organization yyb = new Organization(parentId:yr1.id,name: "应用产品一部运营组", abbreviation: "YY").save()




//                for(int m=0;m<2;m++){
//                    new Employee2(companyId: root.id, name: "小林${m}", gender: "男").save()
//                    new Employee2(companyId: wl.id, name: "小李${m}", gender: "女").save()
//                    new Employee2(companyId: c8.id, name: "小徐${m}", gender: "女").save()
//                    new Employee2(companyId: im.id, name: "小古${m}", gender: "男").save()
//                    new Employee2(companyId: cbb.id, name: "小梁${m}", gender: "女").save()
//                    new Employee2(companyId: bd.id, name: "小刘${m}", gender: "男").save()
//                    new Employee2(companyId: tq2.id, name: "小张${m}", gender: "女").save()
//                    new Employee2(companyId: tl.id, name: "小陈${m}", gender: "女").save()
//                    new Employee2(companyId: hk.id, name: "小郑${m}", gender: "女").save()
//                    new Employee2(companyId: sh.id, name: "小许${m}", gender: "男").save()
//                    new Employee2(companyId: yr1.id, name: "小叶${m}", gender: "女").save()
//                    new Employee2(companyId: yr2.id, name: "小杨${m}", gender: "男").save()
//                    new Employee2(companyId: yr3.id, name: "小黄${m}", gender: "女").save()
//                    new Employee2(companyId: bb.id, name: "小齐${m}", gender: "女").save()
//                    new Employee2(companyId: chb.id, name: "小王${m}", gender: "女").save()
//                    new Employee2(companyId: kfb.id, name: "小施${m}", gender: "男").save()
//                    new Employee2(companyId: yyb.id, name: "小余${m}", gender: "男").save()
//                }
                formTreeViewService.updateConfig(formTreeView.id)
            }
        }catch (Exception e){
            e.printStackTrace()
        }

        FormView formView = new FormView(
                companyId: 1,
                name: "林畅辉",
                gender: "女",
                date: new Date(),
                flag: false,
                gasw: "gdfgd",
                gvs: "fsgfg",
                vxc: "fafa",
                zxv: "sgsf",
                dacte: "dadasd",
                gendfer: "dasdas",
                sa: "fasfsd",
                w: "dadas",
                nafme: "dashdj",
                twq: "asdas",
                wr: "Dasdas",
                asf: "Dasdas",
                flvag: "dasdas"


        ).save()

    }
    def destroy = {
    }
}
