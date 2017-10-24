package biz

class ConnectionMonitor {
    String name
    static constraints = {
        name              attributes:[cn: "真实姓名"],size:1..127
    }
    static m =[
            domain:[cn: "连接监控"]
    ]
}
