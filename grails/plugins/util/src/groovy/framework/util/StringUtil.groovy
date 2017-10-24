package framework.util

/**
 * 工具类
 * @author wuzj
 * @version 2014/8/25.
 */
class StringUtil {

    public static boolean isEmpty(String s){
        if(s!=null&&s.trim().length()>0){
            return false
        }
        return true
    }
    //判断字符串中是否含有特殊字符
    public static boolean checkSpecial(String s){
        int[] specials = (0x21..0x2F)+(0x3A..0x3F)
        for(int i = 0;i<specials.length;i++){
            if(s.trim().indexOf(specials[i])>-1)
                return false
        }
        return true
    }
    //首字母小写
    public static String toLowerCaseFirstOne(String s){
        if (Character.isLowerCase(s.charAt(0)))
            return s
        else
            return new StringBuilder().append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString()
    }

    //首字母大写
    public static String toUpperCaseFirstOne(String s){
        if (Character.isUpperCase(s.charAt(0)))
            return s
        else
            return new StringBuilder().append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString()
    }

    public static String longestCommonString(String s1,String s2){
        if (s1 == "" || s2 == "") return ""
        char c1 = s1.charAt(0)
        char c2 = s2.charAt(0)
        String nextS1 = s1.substring(1)
        String nextS2 = s2.substring(1)
        if (c1 == c2) return c1 + longestCommonString(nextS1,nextS2)
        String lcs1 = longestCommonString(s1,nextS2)
        String lcs2 = longestCommonString(nextS1,s2)
        lcs1.length() > lcs2.length() ? lcs1 : lcs2
    }

    public static int longestCommonStringLength(String s1,String s2){
        return longestCommonString(s1,s2).length()
    }

    public static String longestPrefix(String s,String prefix){
        if (StringUtil.isEmpty(s)||StringUtil.isEmpty(prefix))return ""
        if (s.startsWith(prefix)) return prefix
        longestPrefix(s,prefix.substring(0,prefix.length()-1))
    }
    public static int longestPrefixLength(String s,String prefix){
        return longestPrefix(s,prefix).length()
    }
}
