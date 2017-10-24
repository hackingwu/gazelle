package framework.util

/**
 * 日期计算器
 * @author linyu
 * @version 2014-07-22
 */
class DateCalculator {
    /**
     * 根据给定的参数返回日期对象
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 小时
     * @param minute 分
     * @param second 秒
     * @return Date对象
     */
    static Date Generate(int year, int month, int day, int hour = 0, int minute = 0, int second = 0) {
        Calendar calendar = Calendar.getInstance()

        //初始化
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.getTime()
    }

    /**
     * 当前时间+/-单位值，如：当前时间-2天，当前时间+2分钟
     * @param unit 单位，如：Calendar.MONTH, Calendar.DAY
     * @param value 偏离值
     * @return Date对象
     */
    static Date PlusUnit(int unit, int value) {
        Calendar calendar = Calendar.getInstance()


        calendar.setTime(new Date())
        calendar.set(unit, calendar.get(unit) + value)

        return calendar.getTime()
    }

    /**
     * 给定时间+/-单位值，如：当前时间-2天，当前时间+2分钟
     * @param date 给定时间
     * @param unit 单位，如：Calendar.MONTH, Calendar.DAY
     * @param value 偏离值
     * @return Date对象
     */
    static Date PlusUnit(Date date, int unit, int value) {
        Calendar calendar = Calendar.getInstance()

        calendar.setTime(date)
        calendar.set(unit, calendar.get(unit) + value)

        return calendar.getTime()
    }

    static int getAge(Date birthday) {
        int age = 0
        if (birthday != null) {
            Calendar calendar = Calendar.instance
            calendar.setTime(birthday)
            //出生的年月日
            int birthYear = calendar.get(Calendar.YEAR)
            int birthMonth = calendar.get(Calendar.MONTH)
            int birthDay = calendar.get(Calendar.DATE)
            calendar.setTime(new Date())
            //当前的年月日
            int nowYear = calendar.get(Calendar.YEAR)
            int nowMonth = calendar.get(Calendar.MONTH)
            int nowDay = calendar.get(Calendar.DATE)
            age = nowYear - birthYear
            if (birthMonth > nowMonth) {
                age = age - 1
            } else if (birthMonth == nowMonth) {
                if (birthDay > nowDay) {
                    age = age - 1
                }
            }
        }

        return age
    }

    /**
     * 计算两个日期的小时差
     * @param beginDate 起始时间
     * @param endDate 结束时间
     * @return Long类型的小时差值
     */
    static Float TimeDifferencUnit(Date beginDate,Date endDate) {
        Float totalTime = 0
        totalTime = endDate.getTime()-beginDate.getTime()
        return totalTime / (1000 * 60 * 60)
    }

    /**
     * 将时分秒设置为0
     * @param date
     * @return
     */
    static Date DateInWeeHours(Date date){
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)
        return calendar.getTime()
    }

}
