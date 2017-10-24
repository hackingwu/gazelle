package demo

class FormTreeView {
    long        companyId
    String      name
    String      gender
    Date        date
    Boolean     flag

    static constraints = {
        companyId       attributes:[cn: "归属地区", relation: "ManyToOne", domain: "organization", searchFields:["name", "abbreviation"]]
        name            attributes:[cn: "姓名"],size: 2..6
        gender          attributes:[cn: "性别"],inList: ["保密", "男", "女"]
        date            attributes:[cn: "日期",widget:"datefield"],nullable: true
    }

    String toString(){
        return name
    }

    static m =[
            domain:[cn: "员工"],
            layout:[type: "formTreeView", domain: "organization", key: "companyId"]
    ]
}
