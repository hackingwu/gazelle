package framework.util;

/**
 * Created by Su Sunbin on 2015/2/5.
 */
import java.io.IOException;
import java.util.ArrayList;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

public class ImagesUtil {
	/**
	 * 根据坐标裁剪图片
	 *
	 * @param srcPath   要裁剪图片的路径
	 * @param newPath   裁剪图片后的路径
	 * @param x   起始横坐标
	 * @param y   起始纵坐标
	 * @param x1  结束横坐标
	 * @param y1  结束纵坐标
	 */
	public static void cutImage(String srcPath, String newPath, int x, int y, int x1,
	                            int y1)  throws Exception {
		int width = x1 - x;
		int height = y1 - y;
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		/**  width：裁剪的宽度    * height：裁剪的高度 * x：裁剪的横坐标 * y：裁剪纵坐标  */
		op.crop(width, height, x, y);
		op.addImage(newPath);
		ConvertCmd convert = new ConvertCmd(true);
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) {  //linux下不要设置此值，不然会报错
			convert.setSearchPath("d:/programs/graphicsmagick-1.3.20-q8");
		}
		convert.run(op);
	}
	/**
	 * 根据尺寸缩放图片
	 * @param width  缩放后的图片宽度
	 * @param height  缩放后的图片高度
	 * @param srcPath   源图片路径
	 * @param newPath   缩放后图片的路径
	 */
	public static void zoomImage(Integer width, Integer height, String srcPath, String newPath) throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		if(width == null){//根据高度缩放图片
			op.resize(null, height);
		}else if(height == null){//根据宽度缩放图片
			op.resize(width, null);
		}else {
			op.resize(width, height);
		}
		op.addImage(newPath);
		ConvertCmd convert = new ConvertCmd(true);
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) {  //linux下不要设置此值，不然会报错
			convert.setSearchPath("d:/programs/graphicsmagick-1.3.20-q8");
		}
		convert.run(op);
	}


	/**
	 * 给图片加水印
	 * @param srcPath   源图片路径
	 */
	public static void addImgText(String srcPath,String content) throws Exception {
		IMOperation op = new IMOperation();
		op.font("微软雅黑");
		op.gravity("southeast");
		op.pointsize(18).fill("#BCBFC8").draw("text 0,0 "+content);   //("x1 x2 x3 x4") x1 格式，x2 x轴距离 x3 y轴距离  x4名称
		op.addImage();
		op.addImage();
		ConvertCmd convert = new ConvertCmd(true);
		try {
			convert.run(op,srcPath,srcPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 图片水印
	 *
	 * @param srcImagePath   源图片
	 * @param waterImagePath 水印
	 * @param destImagePath  生成图片
	 * @param gravity  图片位置
	 * @param dissolve 水印透明度
	 */
	public static void waterMark(String waterImagePath, String srcImagePath, String destImagePath, String gravity, int dissolve) {
		IMOperation op = new IMOperation();
		op.gravity(gravity);
		op.dissolve(dissolve);
		op.addImage(waterImagePath);
		op.addImage(srcImagePath);
		op.addImage(destImagePath);
		CompositeCmd cmd = new CompositeCmd(true);
		try {
			cmd.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 图片旋转
	 *
	 * @param srcImagePath
	 * @param destImagePath
	 * @param angle
	 */
	public static void rotate(String srcImagePath, String destImagePath, double angle) {
		try {
			IMOperation op = new IMOperation();
			op.rotate(angle);
			op.addImage(srcImagePath);
			op.addImage(destImagePath);
			ConvertCmd cmd = new ConvertCmd(true);
			cmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片信息
	 *
	 * @param imagePath
	 * @return
	 */
	public static String showImageInfo(String imagePath) {
		String line = null;
		try {
			IMOperation op = new IMOperation();
			op.format("width:%w,height:%h,path:%d%f,size:%b%[EXIF:DateTimeOriginal]");
			op.addImage(1);
			IdentifyCmd identifyCmd = new IdentifyCmd(true);
			ArrayListOutputConsumer output = new ArrayListOutputConsumer();
			identifyCmd.setOutputConsumer(output);
			identifyCmd.run(op, imagePath);
			ArrayList<String> cmdOutput = output.getOutput();
			assert cmdOutput.size() == 1;
			line = cmdOutput.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	/**
	 * 图片合成
	 * @param args
	 * @param maxWidth
	 * @param maxHeight
	 * @param newpath
	 * @param mrg
	 * @param type 1:横,2:竖
	 */
	public static void montage(String[] args,Integer maxWidth,Integer maxHeight,String newpath,Integer mrg,String type) {
		IMOperation op = new IMOperation();
		ConvertCmd cmd = new ConvertCmd(true);
		String thumb_size = maxWidth+"x"+maxHeight+"^";
		String extent = maxWidth+"x"+maxHeight;
		if("1".equals(type)){
			op.addRawArgs("+append");
		}else if("2".equals(type)){
			op.addRawArgs("-append");
		}

		op.addRawArgs("-thumbnail",thumb_size);
		op.addRawArgs("-gravity","center");
		op.addRawArgs("-extent",extent);

		Integer border_w = maxWidth / 40;
		op.addRawArgs("-border",border_w+"x"+border_w);
		op.addRawArgs("-bordercolor","#ccc");

		op.addRawArgs("-border",1+"x"+1);
		op.addRawArgs("-bordercolor","#fff");

		for(String img : args){
			op.addImage(img);
		}
		if("1".equals(type)){
			Integer whole_width = ((mrg / 2) +1 + border_w + maxWidth + border_w + (mrg / 2) +1)*args.length - mrg;
			Integer whole_height = maxHeight + border_w + 1;
			op.addRawArgs("-extent",whole_width + "x" +whole_height);
		}else if("2".equals(type)){
			Integer whole_width = maxWidth + border_w + 1;
			Integer whole_height = ((mrg / 2) +1 + border_w + maxHeight + border_w + (mrg / 2) +1)*args.length - mrg;
			op.addRawArgs("-extent",whole_width + "x" +whole_height);
		}
		op.addImage(newpath);
		try {
			cmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception{
		//addImgText("e://a2.jpg");
		//zoomImage(300, 150, "e://a.jpg", "e://a1.jpg");
		//zoomImage(300, 150, "e://b.jpg", "e://b1.jpg");
		//zoomImage(300, 150, "e://c.jpg", "e://c1.jpg");
		//zoomImage(300, 150, "e://d.jpg", "e://d1.jpg");
		//zoomImage(300, 150, "e://e.jpg", "e://e1.jpg");
		//  zoomImage(300, 150, "e://f.jpg", "e://f1.jpg");
		//waterMark("e://cc.jpg", "e://aa.jpg", "e://bb.jpg", "southeast", 30);
		//rotate("e://aa.jpg", "e://ee.jpg", 90);
		//System.out.println(showImageInfo("e://aa.jpg"));
		String[] files = new String[5];
		files[0] = "e://a1.jpg";
		files[1] = "e://b1.jpg";
		files[2] = "e://c1.jpg";
		files[3] = "e://d1.jpg";
		files[4] = "e://e1.jpg";
		//montage(files, 280, 200, "e://liboy1.jpg", 0,"2");
		//cropImage("e://a.jpg", "e://liboy22.jpg", 1024, 727, 500, 350);
		cutImage("e://a.jpg", "e://cut.jpg", 0, 0, 300, 300);
	}
}
