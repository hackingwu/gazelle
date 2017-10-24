package framework.util

import grails.converters.JSON

/**
 * 文件操作公共方法
 * Created by Su Sunbin on 2014/12/8.
 */
class FileUtil {
	/**
	 * 文件夹初始化
	 * @param directory 文件夹路径
	 * @return
	 */
	public static def fileDirInit(String directory){
		if(StringUtil.isEmpty(directory)){
			return ([success: false, message: "文件夹路径不能为空，请重新尝试！"]as JSON)
		}
		File dir = new File(directory)
		if(!dir.exists()){
			try {
				dir.mkdirs()
			} catch (Exception e) {
				e.printStackTrace()
				return ([success: false, message: "文件夹创建失败，请重新尝试！"]as JSON)
			}
		}
	}
	/**
	 * 根据路径删除文件
	 * @param path
	 */
	public static def deleteFile(String path){
		if(StringUtil.isEmpty(path)){
			return ([success: false, message: "文件夹路径不能为空，请重新尝试！"]as JSON)
		}
		File file = new File(path)
		if(file.exists()){
			try {
				file.delete()
			} catch (e) {
				e.printStackTrace()
				return ([success: false, message: "文件删除失败，请重新尝试！"]as JSON)
			}
		}
	}
	/**
	 * 批量删除文件
	 * @param file
	 * @return
	 */
	public static def deleteFileBatch(def src){
		src?.each {
			File file = new File(it)
			if(file!=null&&file.exists()){
				try {
					file.delete()
				} catch (e) {
					e.printStackTrace()
					return ([success: false, message: "文件删除失败，请重新尝试！"]as JSON)
				}
			}
		}
		return ([success: true, message: "文件删除成功！"]as JSON)
	}

	/**
	 * 文件上传操作 可缩放图片
	 * @param file 从前端获取到的文件
	 * @param regex 文件类型的正则验证，配置在config文件中
	 * @param size 上传的文件的最大值限制，配置在config文件中
	 * @param dir   文件保存路径的根路径 '/data'
	 * @param path  根路径下文件具体的保存位置 '/userImages/poster'
	 * @param scale 是否缩放
	 * @param hight 缩放后图片的高
	 * @param width 缩放后图片的宽
	 * @param white 比例不对是否补白
	 * @return 文件保存路径
	 */
	public static def fileUpload(def file,def regex,def maxSize,def dir,def path,boolean scale,int hight,int width,boolean white) {
		String destPath = dir + path
		if (file==null || file.empty) {
			return ([success: false, message: "文件不能为空！"] as JSON)
		}
		//获取文件名
		String fileName = file.getOriginalFilename()
		if(!(fileName ==~ regex)){
			return ([success: false, message: "请选择正确的文件类型且文件名称不能包含空格！"] as JSON)
		}
		Long size = file.getSize() //获取文件大小
		if(size > maxSize){
			return ([success: false, message: "文件大小不能超过${maxSize/(1024*1024)}MB！"] as JSON)
			return
		}
		//获取文件MD5值
		InputStream is = file.getInputStream();
		String strMD5 = MD5Util.getFileMD5String(is)
		//初始化文件夹
		fileDirInit(destPath)
		//用MD5值+文件后缀重命名文件名
		fileName = strMD5+fileName.substring(fileName.lastIndexOf("."))
		//目标文件：文件上传路径+MD5值+文件后缀
		File destFile = new File("${destPath}/${fileName}")
		try {
			if(!destFile.exists()) {
				//文件写入磁盘
				file.transferTo(destFile)
			}
			if(scale==true){
				ImageUtil.scale2("${destPath}/${fileName}","${destPath}/scale-${fileName}",hight,width,white)
				fileName = "scale-${fileName}"
			}
		} catch (e) {
			e.printStackTrace()
			return ([success: false, message: "文件上传失败，请重新上传！"] as JSON)
		}

		return ([ success: true, message: "文件已成功上传！", data:[id:1,path:"${path}/${fileName}"]] as JSON)
	}
	/**
	 * 文件上传操作
	 * @param file 从前端获取到的文件
	 * @param regex 文件类型的正则验证，配置在config文件中
	 * @param size 上传的文件的最大值限制，配置在config文件中
	 * @param dir   文件保存路径的根路径 '/data'
	 * @param path  根路径下文件具体的保存位置 '/userImages/poster'
	 * return 图片路径
	 */
	public static def fileUpload(def file,def regex,def maxSize,def dir,def path){
		JSON output
		try {
			output = fileUpload(file,regex,maxSize,dir,path,false,0,0,false)
		} catch (e) {
			e.printStackTrace()
			return ([success: false, message: "文件上传失败，请重新上传！"] as JSON)
		}

		return output
	}
	/**
	 * 删除文件，若是文件夹,则删除整个文件夹
	 * @param file
	 */
	public static void delete(File file){
		if (file.exists()){
			if(file.isDirectory()){
				File[] files = file.listFiles()
				files.each {
					delete(it)
				}
			}
			file.delete()
		}
	}
}
