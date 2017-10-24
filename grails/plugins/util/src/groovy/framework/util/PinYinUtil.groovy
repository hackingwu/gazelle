package framework.util

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * @author :林畅辉
 * @version :2014/9/4 修改代码，细节，将原getSuoXie，getQuanPing函数名改为getNameFirstLetter，getName，删除事务标示，以及删除无用的函数，修改一个重复定义的姓氏，修改皇甫拼音错误的一个bug
 * 增加一个区姓,修改返回结果空值用Collections.emptyList()，接口类型定义为准确的List<String>
 */
//利用枚举的方式定义结果的类型
public enum Type {
    SINGLENAME, FULLNAME
}

class PinYinUtil {
    static Map xingShiMap = [
            赵 : "zhao", 钱: "qian", 孙: "sun", 李: "li",
            周 : "zhou", 吴: "wu", 郑: "zheng", 王: "wang",
            冯 : "feng", 陈: "chen", 褚: "chu", 卫: "wei",
            蒋 : "jiang", 沈: "shen", 韩: "han", 杨: "yang",
            朱 : "zhu", 秦: "qin", 尤: "you", 许: "xu",
            何 : "he", 吕: "lu", 施: "shi", 张: "zhang",
            孔 : "kong", 曹: "cao", 严: "yan", 华: "hua",
            金 : "jin", 魏: "wei", 陶: "tao", 姜: "jiang",
            戚 : "qi", 谢: "xie", 邹: "zou", 喻: "yu",
            柏 : "bai", 水: "shui", 窦: "dou", 章: "zhang",
            云 : "yun", 苏: "su", 潘: "pan", 葛: "ge",
            奚 : "xi", 范: "fan", 彭: "peng", 郎: "lang",
            鲁 : "lu", 韦: "wei", 昌: "chang", 马: "ma",
            苗 : "miao", 凤: "feng", 花: "hua", 方: "fang",
            俞 : "yu", 任: "ren", 袁: "yuan", 柳: "liu",
            酆 : "feng", 鲍: "bao", 史: "shi", 唐: "tang",
            费 : "fei", 廉: "lian", 岑: "cen", 薛: "xue",
            雷 : "lei", 贺: "he", 倪: "ni", 汤: "tang",
            滕 : "teng", 殷: "yin", 罗: "luo", 毕: "bi",
            郝 : "hao", 邬: "wu", 安: "an", 常: "chang",
            乐 : "yue", 于: "yu", 时: "shi", 傅: "fu",
            皮 : "pi", 卞: "bian", 齐: "qi", 康: "kang",
            伍 : "wu", 余: "yu", 元: "yuan", 卜: "bu",
            顾 : "gu", 孟: "meng", 平: "ping", 黄: "huang",
            和 : "he", 穆: "mu", 萧: "xiao", 尹: "yin",
            姚 : "yao", 邵: "shao", 湛: "zhan", 汪: "wang",
            祁 : "qi", 毛: "mao", 禹: "yu", 狄: "di",
            米 : "mi", 贝: "bei", 明: "ming", 臧: "zang",
            计 : "ji", 伏: "fu", 成: "cheng", 戴: "dai",
            谈 : "tan", 宋: "song", 茅: "mao", 庞: "pang",
            熊 : "xiong", 纪: "ji", 舒: "shu", 屈: "qu",
            项 : "xiang", 祝: "zhu", 董: "dong", 梁: "liang",
            杜 : "du", 阮: "ruan", 蓝: "lan", 闵: "min",
            席 : "xi", 季: "ji", 麻: "ma", 强: "qiang",
            贾 : "jia", 路: "lu", 娄: "lou", 危: "wei",
            江 : "jiang", 童: "tong", 颜: "yan", 郭: "guo",
            梅 : "mei", 盛: "sheng", 林: "lin", 刁: "diao",
            钟 : "zhong", 徐: "xu", 邱: "qiu", 骆: "luo",
            高 : "gao", 夏: "xia", 蔡: "cai", 田: "tian",
            樊 : "fan", 胡: "hu", 凌: "ling", 霍: "huo",
            虞 : "yu", 万: "wan", 支: "zhi", 柯: "ke",
            昝 : "zan", 管: "guan", 卢: "lu", 莫: "mo",
            经 : "jing", 房: "fang", 裘: "qiu", 缪: "miao",
            干 : "gan", 解: "xie", 应: "ying", 宗: "zong",
            丁 : "ding", 宣: "xuan", 贲: "ben", 邓: "deng",
            郁 : "yu", 单: "shan", 杭: "hang", 洪: "hong",
            包 : "bao", 诸: "zhu", 左: "zuo", 石: "shi",
            崔 : "cui", 吉: "ji", 钮: "niu", 龚: "gong",
            程 : "cheng", 嵇: "ji", 邢: "xing", 滑: "hua",
            裴 : "pei", 陆: "lu", 荣: "rong", 翁: "weng",
            荀 : "xun", 羊: "yang", 於: "yu", 惠: "hui",
            甄 : "zhen", 曲: "qu", 家: "jia", 封: "feng",
            芮 : "rui", 羿: "yi", 储: "chu", 靳: "jin",
            汲 : "ji", 邴: "bing", 糜: "mi", 松: "song",
            井 : "jing", 段: "duan", 富: "fu", 巫: "wu",
            乌 : "wu", 焦: "jiao", 巴: "ba", 弓: "gong",
            牧 : "mu", 隗: "kui", 山: "shan", 谷: "gu",
            车 : "che", 侯: "hou", 宓: "mi", 蓬: "peng",
            全 : "quan", 郗: "xi", 班: "ban", 仰: "yang",
            秋 : "qiu", 仲: "zhong", 伊: "yi", 宫: "gong",
            宁 : "ning", 仇: "qiu", 栾: "luan", 暴: "bao",
            甘 : "gan", 钭: "tou", 厉: "li", 戎: "rong",
            祖 : "zu", 武: "wu", 符: "fu", 刘: "liu",
            景 : "jing", 詹: "zhan", 束: "shu", 龙: "long",
            叶 : "ye", 幸: "xing", 司: "si", 韶: "shao",
            郜 : "gao", 黎: "li", 蓟: "ji", 薄: "bo",
            印 : "yin", 宿: "su", 白: "bai", 怀: "huai",
            蒲 : "pu", 邰: "tai", 从: "cong", 鄂: "e",
            索 : "suo", 咸: "xian", 籍: "ji", 赖: "lai",
            卓 : "zhuo", 蔺: "lin", 屠: "tu", 蒙: "meng",
            池 : "chi", 乔: "qiao", 阴: "yin",
            胥 : "xu", 能: "nai", 苍: "cang", 双: "shuang",
            闻 : "wen", 莘: "shen", 党: "dang", 翟: "zhai",
            谭 : "tan", 贡: "gong", 劳: "lao", 逄: "pang",
            姬 : "ji", 申: "shen", 扶: "fu", 堵: "du",
            冉 : "ran", 宰: "zai", 郦: "li", 雍: "yong",
            郤 : "xi", 璩: "qu", 桑: "sang", 桂: "gui",
            濮 : "pu", 牛: "niu", 寿: "shou", 通: "tong",
            边 : "bian", 扈: "hu", 燕: "yan", 冀: "ji",
            郏 : "jia", 浦: "pu", 尚: "shang", 农: "nong",
            温 : "wen", 别: "bie", 庄: "zhuang", 晏: "yan",
            柴 : "chai", 瞿: "qu", 阎: "yan", 充: "chong",
            慕 : "mu", 连: "lian", 茹: "ru", 习: "xi",
            宦 : "huan", 艾: "ai", 鱼: "yu", 容: "rong",
            向 : "xiang", 古: "gu", 易: "yi", 慎: "shen",
            戈 : "ge", 廖: "liao", 庾: "yu", 终: "zhong",
            暨 : "ji", 居: "ju", 衡: "heng", 步: "bu",
            都 : "du", 耿: "geng", 满: "man", 弘: "hong",
            匡 : "kuang", 国: "guo", 文: "wen", 寇: "kou",
            广 : "guang", 禄: "lu", 阙: "que", 东: "dong",
            欧 : "ou", 殳: "shu", 沃: "wo", 利: "li",
            蔚 : "wei", 越: "yue", 夔: "kui", 隆: "long",
            师 : "shi", 巩: "gong", 厍: "she", 聂: "nie",
            晁 : "chao", 勾: "gou", 敖: "ao", 融: "rong",
            冷 : "leng", 訾: "zi", 辛: "xin", 阚: "kan",
            那 : "na", 简: "jian", 饶: "rao", 空: "kong",
            曾 : "zeng", 母: "mu", 沙: "sha", 乜: "nie",
            养 : "yang", 鞠: "ju", 须: "xu", 丰: "feng",
            巢 : "chao", 关: "guan", 蒯: "kuai", 相: "xiang",
            查 : "zha", 后: "hou", 荆: "jing", 红: "hong",
            游 : "you", 竺: "zhu", 权: "quan", 逯: "lu",
            区 :"ou", 益: "yi", 桓: "huan", 公: "gong",
            万俟: "moqi", 司马: "sima", 上官: "shangguan", 欧阳: "ouyang",
            夏侯: "xiahou", 诸葛: "zhuge", 闻人: "wenren", 东方: "dongfang",
            赫连: "helian", 皇甫: "huangfu", 尉迟: "yuchi", 公羊: "gongyang",
            澹台: "tantai", 公冶: "gongye", 宗政: "zongzheng", 濮阳: "puyang",
            淳于: "chunyu", 单于: "chanyu", 太叔: "taishu", 申屠: "shentu",
            公孙: "gongsun", 仲孙: "zhongsun", 轩辕: "xuanyuan", 令狐: "linghu",
            钟离: "zhongli", 宇文: "yuwen", 长孙: "zhangsun", 慕容: "murong",
            鲜于: "xianyu", 闾丘: "luqiu", 司徒: "situ", 司空: "sikong",
            亓官: "qiguan", 司寇: "sikou", 仉督: "zhangdu", 子车: "ziju",
            颛孙: "zhuansun", 端木: "duanmu", 巫马: "wuma", 公西: "gongxi",
            漆雕: "qidiao", 乐正: "yuezheng", 壤驷: "rangsi", 公良: "gongliang",
            拓跋: "tuoba", 夹谷: "jiagu", 宰父: "zaifu", 榖梁: "guliang",
            晋楚: "jinchu", 闫法: "yanfa", 汝鄢: "ruyan", 涂钦: "tuqin",
            段干: "duangan", 百里: "baili", 东郭: "dongguo", 南门: "nanmen",
            呼延: "huyan", 归海: "guihai", 羊舌: "yangshe", 微生: "weisheng",
            岳帅: "yueshuai", 缑亢: "goukang", 况后: "kuanghou", 有琴: "youqin",
            梁丘: "liangqiu", 左丘: "zuoqiu", 东门: "dongmen", 西门: "ximen",
            商牟: "shangmou", 佘佴: "shenai", 伯赏: "boshang", 南宫: "nangong",
            墨哈: "moha", 谯笪: "qiaoda", 年爱: "nianai", 阳佟: "yangtong",
            第五: "diwu", 言福: "yanfu"
    ]
    static Map fuXingSuoXie = [
            万俟: "mq", 司马: "sm", 上官: "sg", 欧阳: "oy",
            夏侯: "xh", 诸葛: "zg", 闻人: "wr", 东方: "df",
            赫连: "hl", 皇甫: "hf", 尉迟: "yc", 公羊: "gy",
            澹台: "tt", 公冶: "gy", 宗政: "zz", 濮阳: "py",
            淳于: "cy", 单于: "cy", 太叔: "ts", 申屠: "st",
            公孙: "gs", 仲孙: "zs", 轩辕: "xy", 令狐: "lh",
            钟离: "zl", 宇文: "yw", 长孙: "zs", 慕容: "mr",
            鲜于: "xy", 闾丘: "lq", 司徒: "st", 司空: "sk",
            亓官: "qg", 司寇: "sk", 仉督: "zd", 子车: "zj",
            颛孙: "zs", 端木: "dm", 巫马: "wm", 公西: "gx",
            漆雕: "qd", 乐正: "yz", 壤驷: "rs", 公良: "gl",
            拓跋: "tb", 夹谷: "jg", 宰父: "zf", 榖梁: "gl",
            晋楚: "jc", 闫法: "yf", 汝鄢: "ry", 涂钦: "tq",
            段干: "dg", 百里: "bl", 东郭: "dg", 南门: "nm",
            呼延: "hy", 归海: "gh", 羊舌: "ys", 微生: "ws",
            岳帅: "ys", 缑亢: "gk", 况后: "kh", 有琴: "yq",
            梁丘: "lq", 左丘: "zq", 东门: "dm", 西门: "xm",
            商牟: "sm", 佘佴: "sn", 伯赏: "bs", 南宫: "ng",
            墨哈: "mh", 谯笪: "qd", 年爱: "na", 阳佟: "yt",
            第五: "dw", 言福: "yf"
    ]


    /**
     * 根据参数的类型返回对应的全拼拼音字符串
     *
     * @param name
     *            名字，任意可能性
     * @return List 一个字符串的列表，内容根据传入的name 决定
     */
//转化为全拼的接口
    static List<String> getFullName(String name) {
        if (name != null) {
            //需要对String的长度做判断
            if (name.size() > 1) {
                if (xingShiMap[name[0] + name[1]] != null) {

                    return converterToList(xingShiMap[name[0] + name[1]].toString(), name.substring(2).toString(), Type.FULLNAME)
                } else if (xingShiMap[name[0]] != null) {
                    return converterToList(xingShiMap[name[0]].toString(), name.substring(1).toString(), Type.FULLNAME)
                } else {
                    return converterToList("", name.toString(), Type.FULLNAME)

                }
            } else {
                return converterToList("", name.toString(), Type.FULLNAME)
            }
        } else {
            return Collections.emptyList()
        }
    }
    /**
     * 根据参数的类型返回对应的缩写拼音字符串
     *
     * @param name
     *            名字，任意可能性
     * @return List 一个字符串的列表，内容根据传入的name 决定
     */
    //转化为缩写的接口
    static List<String> getSingleName(String name) {
        if(name != null){
            //需要对String的长度做判断
            if (name != null && name.size() > 1) {
                if (fuXingSuoXie[name[0] + name[1]] != null) {
                    return converterToList(fuXingSuoXie[name[0] + name[1]].toString(), name.substring(2).toString(), Type.SINGLENAME)

                } else if (xingShiMap[name[0]] != null) {
                    return converterToList(xingShiMap[name[0]][0].toString(), name.substring(1).toString(), Type.SINGLENAME)

                } else {
                    return converterToList("", name.toString(), Type.SINGLENAME)

                }
            } else {
                return converterToList("", name.toString(), Type.SINGLENAME)
            }
        }else{
            return Collections.emptyList()
        }
    }


    /**
     * 根据参数的类型返回对应的拼音字符串列表
     *
     * @param firstName
     *            姓氏，可能是一个字也能使复姓
     * @param secondName
     *            名字
     * @param type
     *            转化结果的类型参数，0表示缩写，1表示全拼
     * @return 返回一个List , 里面可能是缩写也能使全拼 , 例如：输入（林，常会，0）
     * 输出["lch"]
     */
    static List converterToList(String firstName, String secondName, Type type) {
        List result = []
        switch (type) {
            case Type.SINGLENAME:
                converterToFirstSpell(secondName).each { String r ->
                    result.add(firstName + r.toString())
                }
                return result
            case Type.FULLNAME:
                converterToSpell(secondName).each { String r ->
                    result.add(firstName + r.toString())
                }
                return result
            default: return result
        }
    }

    /**
     * 姓氏是否符合要求判断函数，是否为纯汉字
     *
     * @param firstName
     *            姓氏，可能是一个字也能使复姓
     * @return true表示为纯汉字，false表示为非纯汉字
     */
    public static String getPureChinese(String firstName) {
        Pattern p = Pattern.compile("[\u4e00-\u9FFF]")
        String result;
        Matcher m
        for (int i = 0; i < firstName.length(); i++) {
            m = p.matcher(firstName[i]);
            if (m.find()) {
                result += firstName[i]
//                println firstName[i]+"是汉字"
            } else {
//                println firstName[i]+"不是汉字"
            }
        }
        return result
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失
     * 支持多音字，生成方式如（重当参:cdc,zds,cds,zdc）
     *
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static List converterToFirstSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            // 取首字母
                            pinyinName.append(strs[j].charAt(0));
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
            pinyinName.append(" ");
        }
        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失
     * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static List converterToSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            pinyinName.append(strs[j]);
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
            pinyinName.append(" ");
        }
        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    }

    /**
     * 去除多音字重复数据
     *
     * @param theStr
     * @return
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;

        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new Hashtable<String, Integer>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, new Integer(1));
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * 解析并组合拼音，对象合并方案(推荐使用)
     *
     * @return
     */
    private static List parseTheChineseByObject(List<Map<String, Integer>> list) {
        Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 每一组集合与上一次组合的Map
            Map<String, Integer> temp = new Hashtable<String, Integer>();
            // 第一次循环，first为空
            if (first != null) {
                // 取出上次组合与此次集合的字符，并保存
                for (String s : first.keySet()) {
                    for (String s1 : list.get(i).keySet()) {
                        String str = s + s1;
                        temp.put(str, 1);
                    }
                }
                // 清理上一次组合数据
                if (temp != null && temp.size() > 0) {
                    first.clear();
                }
            } else {
                for (String s : list.get(i).keySet()) {
                    String str = s;
                    temp.put(str, 1);
                }
            }
            // 保存组合数据以便下次循环使用
            if (temp != null && temp.size() > 0) {
                first = temp;
            }
        }
        List strs = []
        if (first != null) {
            // 遍历取出组合字符串
            for (String str : first.keySet()) {
                strs.add(str)

            }
        }

        return strs;
    }
}
