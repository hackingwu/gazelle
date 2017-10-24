package export

/**
 * 导入导出管理模块
 * @author lichb
 * @version 2015-1-30
 */
class ImportInfo {
    String      userName     //操作用户名
    String      fileName     //文件名
    Date        operTime     //操作时间
    int         operType     //操作类型
    int         fileStatus   //状态
    long        total        //总记录数
    long        sucessConut  //成功记录数
    String      filePath     //文件下载
    String      md5          //文件MD5值
    long      creatorId      //创建者id

    static constraints = {
        userName              attributes:[cn: "用户名"],size:2..32
        fileName              attributes:[cn: "文件名",flex:5],size:4..256  //文件名不能含有下划线
        operTime              attributes:[cn: "操作时间",flex:2,widget:"datetimefield"]
        operType              attributes:[cn: "操作类型", flex:2, inListLabel:["导入CPP成膜数据","导入CPP成膜物理性能测试数据","导入热处理数据",
                              "导入复合数据","导入拉伸数据","导入拉伸物理性能测试结果","导入大分层数据","导入半成品入库数据","导入半成品出库数据",
                              "导入小分层数据","导入分切数据","导出拉伸物理性能测试数据","导出日志","导出卡券"]],inList:[0,1,2,3,4,5,6,7,8,9,10,11,12,13]
        fileStatus            attributes:[cn: "状态", flex:2, inListLabel:["文件解析失败", "导入完毕","处理中，请稍后...", "导出完毕", "导出失败"]],inList:[1, 2, 3, 4, 5]
        total                 attributes:[cn: "总记录数",flex:1], min: 0l, max: 1000000l
        sucessConut           attributes:[cn: "成功记录数",flex:1], min: 0l, max: 1000000l
        filePath              attributes:[cn: "文件下载",renderer: "ImportInfoFilePathRender",flex:2],size:2..256
        md5                   attributes:[cn: "MD5值"],size:2..256
        creatorId             attributes:[cn: "上传者id"],nullable:true

    }

    String toString(){
        return fileName
    }

    static m =[
            domain:[cn: "导入导出"],
            layout:[type: "standard"]
    ]
}
