package com.nd.cube.common.cache;

import java.util.Date;

/**
 * 过期时间工具类处理
 * @author  2015/1/13.
 */
public class ExpireTimes {

    //一天中的秒数
    public final static long SECOND_IN_DAY = 24L * 60 *60;

    //一个小时的秒数
    public final static long SECOND_IN_HOUR = 60L *60L;

    //99年,相当于永久缓存了
    public final static long FOREVER =99L * 365 * SECOND_IN_DAY;

    /**
     * 根据特定日期生成过期时间
     * @param date
     * @return
     */
    public static long expireTime(Date date){
        if(date != null){
            return date.getTime()/1000;
        }
        return 1L;
    }

    /**
     * 从当前时间计算天后过期
     * @param days
     * @return
     */
    public static long  expireTimeAfterDay(int days){
        return  expireTimeAfterSecond(SECOND_IN_DAY * days);
    }

    /**
     * 从当前时间计算多少小时后过期
     * @param hours
     * @return
     */
    public static long  expireTimeAfterHour(int hours){
        return expireTimeAfterSecond( SECOND_IN_HOUR * hours);
    }

    /**
     * 从当前时间计算多少分钟后过期
     * @param minutes
     * @return
     */
    public static long  expireTimeAfterMinute(int minutes){
        return  expireTimeAfterSecond(60L * minutes);
    }

    /**
     * 从当前时间计算多少秒后过期
     * @param seconds
     * @return
     */
    public static long  expireTimeAfterSecond(long seconds){
        return  System.currentTimeMillis()/1000 + seconds;
    }

}
