package com.nd.cube.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html工具类,处理html内容
 * xufb on 2015/1/30.
 */
public class HtmlUtils extends org.springframework.web.util.HtmlUtils {

    //提取html正则表达式
    public static final String EXTRACTION_TEXT = "<[^>]+>";
    //dom.text
    private static final Pattern PATTERN_TEXT = Pattern.compile(EXTRACTION_TEXT);

    /**
     * 提取html中text内容,js $().text
     * @param html
     * @return
     */
    public static String getText(String html){
        if(html != null){
            Matcher matcher = PATTERN_TEXT.matcher(html);
            StringBuffer sb = new StringBuffer();
            while(matcher.find()){
                matcher.appendReplacement(sb,"");
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return "";
    }
}
