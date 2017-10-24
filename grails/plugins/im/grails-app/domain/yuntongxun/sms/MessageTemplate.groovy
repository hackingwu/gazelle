package yuntongxun.sms


class MessageTemplate {

    String templateId;
    String name;
    String content;
    static constraints = {
        templateId attributes:[cn: "模板ID"]
        name       attributes:[cn: "名称"]
        content    attributes:[cn:"内容"]
    }
    static m =[
            domain:[cn: "短信模板"],
            layout:[type: "standard"]
    ]
    String toString(){
        return name;
    }
}
