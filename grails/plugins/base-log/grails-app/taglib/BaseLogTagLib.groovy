

class BaseLogTagLib {
    static namespace = "v"
    def authService

    /**
     * 日志导出
     */
    def extLogExport = { attrs, body ->
        boolean exportDisable=(!authService.Button(session["sysRoleId"], "log_export"))
        String output = ""

        if(!exportDisable){
            output = """
                {
                    itemId:'log_export',
                    text:'导出日志',
                    iconCls:'icon-data-export',
                    handler: function () {
                        logExport();
                    }
                },
            """
        }

        out << output
    }

}
