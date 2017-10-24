/**
 * 动态创建url，保证js与gsp分离
 * @author lcb
 **/
// 创建一个script节点
 var scriptBlock=document.createElement("script");

//注入脚本
 scriptBlock.text="function createLink(controller,action){"
                 +"    var url = '${g.createLink([controller: \"controller\", action:\"action\"])}';"
                 +"    return url.replace('controller',controller).replace('action',action);"
                 +"}";

alert(scriptBlock.text);

// 将该文件加入的html文件的头部。
document.getElementsByTagName("head")[0].appendChild(scriptBlock);

