package demo

class Organization {

    long    parentId
    String  name
    String  abbreviation
    Long    formTreeViewId

    static constraints = {
        parentId        attributes:[cn: "父节点", relation: "ManyToOne", domain: "organization"],nullable:true
        name            attributes:[cn: "名称"],size: 2..32
        abbreviation    attributes:[cn: "简写"],size: 2..8
        formTreeViewId      attributes:[cn: "配置信息", relation: "ManyToOne", domain: "formTreeView"],nullable: true
    }

    String toString(){
        return "${name}(${abbreviation})"

    }

    static m =[
            domain:[cn: "公司"],
            layout:[type: "standard"]
    ]
}