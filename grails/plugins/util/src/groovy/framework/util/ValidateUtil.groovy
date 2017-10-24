package framework.util

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2014/8/14.
 */
class ValidateUtil {
     static isNumeric(String str){
        if(str==null){
            return false
        }
        return str==~ /(-?\d+)(\.\d+)?/
    }

    /**
     * 判断是否为正确的邮件格式
     * @param str
     * @return boolean
     */
    static isEmail(String str){
        if(str==null){
            return false
        }
        return str==~/^([a-zA-Z0-9_\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/
    }

    /**
     * 判断字符串是否为合法手机号 11位 13 14 15 17 18开头
     * @param str
     * @return boolean
     */
     static isMobile(String str){
         if(str==null){
             return false
         }
         return str==~/(13|14|15|17|18)\d{9}/
    }

    /**
     * 字符串长度判断
     * @param str
     * @param len
     * @return
     */
    static isMaxLength(String str,int len){
        if(str==null){
            return true;
        }
        if(str.length()>len){
            return false;
        }
        return true;
    }

    static isDateStr(String str){
        if(str==null){
            return false;
        }
//        SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd");
//        def matcher=str=~/(\d){4}\/(\d){1,2}\/(\d){1,2}/
        def matcher2=str=~/^(\d){4}\-(\d){1,2}\-(\d){1,2}$/
//        boolean flag1=false
        boolean flag2=false
//        if(matcher.size()>0){
//            String matchedStr = matcher[0][0]
//            Date date=sdf.parse(matchedStr)
//            String reFormatStr=sdf.format(date)
//            if(matchedStr.equals(reFormatStr)){
//                flag1= true;
//            }
//        }
        if(matcher2.size()>0){
            String matchedStr2 = matcher2[0][0]
            Date date=sdf2.parse(matchedStr2)
            String reFormatStr=sdf2.format(date)
            if(matchedStr2.equals(reFormatStr)){
                flag2= true;
            }
        }

        return /*flag1||*/flag2






    }

    /**
     * 验证字符串是否由纯数字组成
     * @param str
     * @return
     */
    public static boolean isDigits(String str) {
        return NumberUtils.isDigits(str)
    }

}
