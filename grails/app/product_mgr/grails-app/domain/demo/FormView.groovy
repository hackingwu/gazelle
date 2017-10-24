package demo

class FormView {
    long companyId
    String name
    String gender
    Date date
    Boolean flag
//    String      nsgame
//    String      gegsdfnder
//    Date        dagasgte
//    Boolean     flgasag
//    String      nagsfgme
//    String      gengasgder
//    Date        dagaste
//    Boolean     gas
//    String      gs
//    String      gvxs
    String gvs
    String wr
    String twq
    String vxc
    String zxv
    String asf
    String sa
    String w
    String gasw
    String flvag
    String nafme
    String gendfer
    String dacte


    static constraints = {
        companyId attributes: [cn: "归属地区", relation: "ManyToOne", domain: "organization", searchFields: ["name", "abbreviation"]]
        name attributes: [cn: "姓名"], size: 2..6
        gender attributes: [cn: "性别"], inList: ["保密", "男", "女"]
        date attributes: [cn: "日期", widget: "datefield"], nullable: true
        nafme attributes: [widget: "htmleditor"]
        twq attributes: [widget: "textareafield"]
//        gendfer            attributes:[cn: "姓名", widget: "htmleditor"],size: 2..6
//        sa            attributes:[cn: "姓名", widget: "textareafield"],size: 2..6

    }

    String toString() {
        return name
    }

    static m = [
            domain: [cn: "员工"],
            layout: [type: "formView"]
    ]
}
