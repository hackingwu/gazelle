package framework.util

import java.text.SimpleDateFormat

/**
 * 随机数与随机串生成工具
 * @author xfb
 * @sina 2014/8/20.
 */
class RandomUtil {

    /**
     * 随机生成一个小于max的数字
     * @param max
     * @return
     */
    static final int randomInt(int max){
        Random randGen = new Random()
        return randGen.nextInt(max)
    }

   /**
    * 随机生成字符串
    * @param length 生成字符串的长度
    * @return
    */
     static final String randomString(int length) {
        if (length < 1) {
            return ''
        }

        Random randGen = new Random()
        char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        char [] randBuffer = new char[length]

        for (int i=0; i<randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)]
        }
        return new String(randBuffer)
    }
    /**
     * 随机生成纯数字的字符串
     * @param length 生成字符串的长度
     * @return
     */
    static final String randomIntString(int length){
        StringBuilder sb = new StringBuilder()
        length.times{
            sb.append(randomInt(10))
        }
        return sb.toString()
    }
    /**
     * 随机生成二代身份证号码
     */
    static final String randomIdentity(){
        StringBuilder sb = new StringBuilder()

        List city= [11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91];
        sb.append(city[randomInt(city.size())])
        sb.append(randomIntString(4))
        String dateStr = randomBirthday().replace('-','')
        sb.append(dateStr)
        sb.append(randomIntString(3))
        String[] s = sb.toString().split('').reverse()
        int sum = 0
        for(int i =0 ; i <17;i++){
            sum += Integer.parseInt(s[i])*(int)(Math.pow(2,i+1)%11)
        }
        int checkSum = (12-sum%11)%11
        sb.append(checkSum<10?checkSum:'X')
        return sb.toString()
    }
    static final String randomIdentity(String birthday){
        StringBuilder sb = new StringBuilder()

        List city= [11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91];
        sb.append(city[randomInt(city.size())])
        sb.append(randomIntString(4))
        String dateStr = birthday.replace('-','')
        sb.append(dateStr)
        sb.append(randomIntString(3))
        String[] s = sb.toString().split('').reverse()
        int sum = 0
        for(int i =0 ; i <17;i++){
            sum += Integer.parseInt(s[i])*(int)(Math.pow(2,i+1)%11)
        }
        int checkSum = (12-sum%11)%11
        sb.append(checkSum<10?checkSum:'X')
        return sb.toString()
    }
    /**
     * 随机生成出生年月
     *
     */
    static final String randomBirthday(){
        Random random = new Random()
        int month = random.nextInt(12)
        int day = randomInt(31)
        int year = 1950+random.nextInt(60)
        Calendar calendar = Calendar.instance
        calendar.set(year,month,day)
        Date now = calendar.getTime()
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(now)
    }
}
