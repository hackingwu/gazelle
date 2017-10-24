package com.nd.cube.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 根据日期生成目录路径
 * Created by Administrator on 2015/3/9.
 */
class DateDirGenerator {

    final Pattern pattern = Pattern.compile('\\$\\{([^\\}]*)\\}')

    final long MILLIS_OF_DAY = 24L*60*60*1000

    private String template = '${yyyy}/${MM}/${dd}/'

    private String tmp = ''

    private String[] dirs = new String[0]

    private String[] argNames = new String[0]

    //一天的最小时间开始时间
    private long beginOfDay = 0L

    /**
     * TODO 异步生成目录
     */
    boolean syn = false;


    public DateDirGenerator(String template){
        this.template = template
        parse()
        generatDir()
    }

    public String generat(){
        if(System.currentTimeMillis() - beginOfDay > MILLIS_OF_DAY){
            generatDir()
        }
        return tmp
    }


    private String replacePlaceholders(Map<String,String> args){
        StringBuilder sb = new StringBuilder()
        if(dirs.length > 1){
            for(int i = 0; i < dirs.length; i++){
                sb.append(dirs[i])
                if(i < argNames.length){
                    if(args.containsKey(argNames[i])){
                        sb.append(args.get(argNames[i]))
                    }else {
                        sb.append(argNames[i])
                    }
                }
            }
        }else {
            sb.append(dirs[0])
        }
        return sb.toString()
    }

    private void parse(){
        Matcher matcher = pattern.matcher(this.template)

        StringBuffer sb = new StringBuffer()
        List<String> args = new ArrayList<String>()

        while(matcher.find()){
            args.add(matcher.group(1))
            matcher.appendReplacement(sb, "<")
        }
        matcher.appendTail(sb)
        dirs = sb.toString().split("<")
        argNames = args.toArray(argNames)
    }


    private void generatDir(){
        Calendar cal = Calendar.getInstance()
        Map<String,String> args = new HashMap<String,String>()
        args.put("yy", parseIntToString(cal.get(Calendar.YEAR)%100))
        args.put("yyyy", parseIntToString(cal.get(Calendar.YEAR)))
        args.put("mm", parseIntToString(cal.get(Calendar.MONTH)))
        args.put("dd", parseIntToString(cal.get(Calendar.DAY_OF_MONTH)))

//        args.put("mis", String.valueOf(cal.get(Calendar.MILLISECOND)))
//        args.put("ss", String.valueOf(cal.get(Calendar.SECOND)))

        this.beginOfDay = beginOfDay()
        this.tmp = replacePlaceholders(args)
    }


    private String parseIntToString(int i){
        i = i +100
        return String.valueOf(i).substring(1)
    }

    private long beginOfDay(){
        Calendar cal = Calendar.getInstance()
        cal.set(Calendar.HOUR, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTimeInMillis()
    }

//    public static void main(String[] args) throws Exception {
//        DateDirGenerator dg = new DateDirGenerator("${yyyy}/${MM}/${dd}/gg/${ss}ss/${mis}")
//        System.out.println(dg.generat())
//        Thread.sleep(3)
//        System.out.println(dg.generat())
//        Thread.sleep(16)
//        System.out.println(dg.generat())
//        Thread.sleep(5)
//        System.out.println(dg.generat())
//    }
}
