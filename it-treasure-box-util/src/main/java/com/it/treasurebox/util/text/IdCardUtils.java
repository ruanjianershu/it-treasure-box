/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: matieli[ma_tl@suixingpay.com]
 * @date: 2018年1月11日 下午5:53:40
 * @Copyright ©2018 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.it.treasurebox.util.text;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份证工具类
 *
 */
@Slf4j
public class IdCardUtils {

    /**
     * 中国公民身份证号码最小长度。
     */
    public static final int CHINA_ID_MIN_LENGTH = 15;

    /**
     * 中国公民身份证号码最大长度。
     */
    public static final int CHINA_ID_MAX_LENGTH = 18;

    /**
     * 省、直辖市代码表
     */
    public static final String CITYCODE[] = {"11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34",
            "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64",
            "65", "71", "81", "82", "91"};

    /**
     * 每位加权因子
     */
    public static final int POWER[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 第18位校检码
     */
    public static final String VERIFYCODE[] = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    /**
     * 最低年限
     */
    public static final int MIN = 1930;
    private static final Map<String, String> CITYCODES = new HashMap<String, String>();
    /**
     * 台湾身份首字母对应数字
     */
    private static final Map<String, Integer> TWFIRSTCODE = new HashMap<String, Integer>();
    /**
     * 香港身份首字母对应数字
     */
    private static final Map<String, Integer> HKFIRSTCODE = new HashMap<String, Integer>();

    static {
        CITYCODES.put("11", "北京");
        CITYCODES.put("12", "天津");
        CITYCODES.put("13", "河北");
        CITYCODES.put("14", "山西");
        CITYCODES.put("15", "内蒙古");
        CITYCODES.put("21", "辽宁");
        CITYCODES.put("22", "吉林");
        CITYCODES.put("23", "黑龙江");
        CITYCODES.put("31", "上海");
        CITYCODES.put("32", "江苏");
        CITYCODES.put("33", "浙江");
        CITYCODES.put("34", "安徽");
        CITYCODES.put("35", "福建");
        CITYCODES.put("36", "江西");
        CITYCODES.put("37", "山东");
        CITYCODES.put("41", "河南");
        CITYCODES.put("42", "湖北");
        CITYCODES.put("43", "湖南");
        CITYCODES.put("44", "广东");
        CITYCODES.put("45", "广西");
        CITYCODES.put("46", "海南");
        CITYCODES.put("50", "重庆");
        CITYCODES.put("51", "四川");
        CITYCODES.put("52", "贵州");
        CITYCODES.put("53", "云南");
        CITYCODES.put("54", "西藏");
        CITYCODES.put("61", "陕西");
        CITYCODES.put("62", "甘肃");
        CITYCODES.put("63", "青海");
        CITYCODES.put("64", "宁夏");
        CITYCODES.put("65", "新疆");
        CITYCODES.put("71", "台湾");
        CITYCODES.put("81", "香港");
        CITYCODES.put("82", "澳门");
        CITYCODES.put("91", "国外");
        TWFIRSTCODE.put("A", 10);
        TWFIRSTCODE.put("B", 11);
        TWFIRSTCODE.put("C", 12);
        TWFIRSTCODE.put("D", 13);
        TWFIRSTCODE.put("E", 14);
        TWFIRSTCODE.put("F", 15);
        TWFIRSTCODE.put("G", 16);
        TWFIRSTCODE.put("H", 17);
        TWFIRSTCODE.put("J", 18);
        TWFIRSTCODE.put("K", 19);
        TWFIRSTCODE.put("L", 20);
        TWFIRSTCODE.put("M", 21);
        TWFIRSTCODE.put("N", 22);
        TWFIRSTCODE.put("P", 23);
        TWFIRSTCODE.put("Q", 24);
        TWFIRSTCODE.put("R", 25);
        TWFIRSTCODE.put("S", 26);
        TWFIRSTCODE.put("T", 27);
        TWFIRSTCODE.put("U", 28);
        TWFIRSTCODE.put("V", 29);
        TWFIRSTCODE.put("X", 30);
        TWFIRSTCODE.put("Y", 31);
        TWFIRSTCODE.put("W", 32);
        TWFIRSTCODE.put("Z", 33);
        TWFIRSTCODE.put("I", 34);
        TWFIRSTCODE.put("O", 35);
        HKFIRSTCODE.put("A", 1);
        HKFIRSTCODE.put("B", 2);
        HKFIRSTCODE.put("C", 3);
        HKFIRSTCODE.put("R", 18);
        HKFIRSTCODE.put("U", 21);
        HKFIRSTCODE.put("Z", 26);
        HKFIRSTCODE.put("X", 24);
        HKFIRSTCODE.put("W", 23);
        HKFIRSTCODE.put("O", 15);
        HKFIRSTCODE.put("N", 14);
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idCard 15位身份编码
     * @return 18位身份编码
     */
    public static String conver15CardTo18(String idCard) {
        String idCard18 = "";
        if (idCard.length() != CHINA_ID_MIN_LENGTH) {
            return null;
        }
        if (isNum(idCard)) {
            // 获取出生年月日
            String birthday = idCard.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null) {
                cal.setTime(birthDate);
            }
            // 获取出生年(完全表现形式,如：2010)
            String sYear = String.valueOf(cal.get(Calendar.YEAR));
            idCard18 = idCard.substring(0, 6) + sYear + idCard.substring(8);
            // 转换字符数组
            char[] cArr = idCard18.toCharArray();
            if (cArr != null) {
                int[] iCard = converCharToInt(cArr);
                int iSum17 = getPowerSum(iCard);
                // 获取校验位
                String sVal = getCheckCode18(iSum17);
                if (sVal.length() > 0) {
                    idCard18 += sVal;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        return idCard18;
    }

    /**
     * 验证身份证是否合法
     */
    public static boolean validateCard(String idCard) {
        String card = idCard.trim();
        if (validateIdCard18(card)) {
            return true;
        }
        if (validateIdCard15(card)) {
            return true;
        }
        String[] cardval = validateIdCard10(card);
        if (cardval != null) {
            if (cardval[2].equals("true")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证18位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard18(String idCard) {
        boolean bTrue = false;
        if (idCard.length() == CHINA_ID_MAX_LENGTH) {
            // 前17位
            String code17 = idCard.substring(0, 17);
            // 第18位
            String code18 = idCard.substring(17, CHINA_ID_MAX_LENGTH);
            if (isNum(code17)) {
                char[] cArr = code17.toCharArray();
                if (cArr != null) {
                    int[] iCard = converCharToInt(cArr);
                    int iSum17 = getPowerSum(iCard);
                    // 获取校验位
                    String val = getCheckCode18(iSum17);
                    if (val.length() > 0) {
                        if (val.equalsIgnoreCase(code18)) {
                            bTrue = true;
                        }
                    }
                }
            }
        }
        return bTrue;
    }

    /**
     * 验证15位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard15(String idCard) {
        if (idCard.length() != CHINA_ID_MIN_LENGTH) {
            return false;
        }
        if (isNum(idCard)) {
            String proCode = idCard.substring(0, 2);
            if (CITYCODES.get(proCode) == null) {
                return false;
            }
            String birthCode = idCard.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null) {
                cal.setTime(birthDate);
            }
            if (!valiDate(cal.get(Calendar.YEAR), Integer.valueOf(birthCode.substring(2, 4)),
                    Integer.valueOf(birthCode.substring(4, 6)))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 验证10位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 身份证信息数组
     * <p>
     * [0] - 台湾、澳门、香港 [1] - 性别(男M,女F,未知N) [2] - 是否合法(合法true,不合法false)
     * 若不是身份证件号码则返回null
     * </p>
     */
    public static String[] validateIdCard10(String idCard) {
        String[] info = new String[3];
        String card = idCard.replaceAll("[\\(|\\)]", "");
        if (card.length() != 8 && card.length() != 9 && idCard.length() != 10) {
            return null;
        }
        if (idCard.matches("^[a-zA-Z][0-9]{9}$")) { // 台湾
            info[0] = "台湾";
            String char2 = idCard.substring(1, 2);
            if (char2.equals("1")) {
                info[1] = "M";
            } else if (char2.equals("2")) {
                info[1] = "F";
            } else {
                info[1] = "N";
                info[2] = "false";
                return info;
            }
            info[2] = validateTWCard(idCard) ? "true" : "false";
        } else if (idCard.matches("^[1|5|7][0-9]{6}\\(?[0-9A-Z]\\)?$")) { // 澳门
            info[0] = "澳门";
            info[1] = "N";
            // TODO
        } else if (idCard.matches("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$")) { // 香港
            info[0] = "香港";
            info[1] = "N";
            info[2] = validateHKCard(idCard) ? "true" : "false";
        } else {
            return null;
        }
        return info;
    }

    /**
     * 验证台湾身份证号码
     *
     * @param idCard 身份证号码
     * @return 验证码是否符合
     */
    public static boolean validateTWCard(String idCard) {
        String start = idCard.substring(0, 1);
        String mid = idCard.substring(1, 9);
        String end = idCard.substring(9, 10);
        Integer iStart = TWFIRSTCODE.get(start);
        Integer sum = iStart / 10 + (iStart % 10) * 9;
        char[] chars = mid.toCharArray();
        Integer iflag = 8;
        for (char c : chars) {
            sum = sum + Integer.valueOf(c + "") * iflag;
            iflag--;
        }
        return (sum % 10 == 0 ? 0 : (10 - sum % 10)) == Integer.valueOf(end) ? true : false;
    }

    /**
     * 验证香港身份证号码(存在Bug，部份特殊身份证无法检查)
     * <p>
     * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35
     * 最后一位校验码为0-9的数字加上字符"A"，"A"代表10
     * </p>
     * <p>
     * 将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效
     * </p>
     *
     * @param idCard 身份证号码
     * @return 验证码是否符合
     */
    public static boolean validateHKCard(String idCard) {
        String card = idCard.replaceAll("[\\(|\\)]", "");
        Integer sum = 0;
        if (card.length() == 9) {
            sum = (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 9
                    + (Integer.valueOf(card.substring(1, 2).toUpperCase().toCharArray()[0]) - 55) * 8;
            card = card.substring(1, 9);
        } else {
            sum = 522 + (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 8;
        }
        String mid = card.substring(1, 7);
        String end = card.substring(7, 8);
        char[] chars = mid.toCharArray();
        Integer iflag = 7;
        for (char c : chars) {
            sum = sum + Integer.valueOf(c + "") * iflag;
            iflag--;
        }
        if (end.toUpperCase().equals("A")) {
            sum = sum + 10;
        } else {
            sum = sum + Integer.valueOf(end);
        }
        return (sum % 11 == 0) ? true : false;
    }

    /**
     * 将字符数组转换成数字数组
     *
     * @param ca 字符数组
     * @return 数字数组
     */
    public static int[] converCharToInt(char[] ca) {
        int len = ca.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
        }
        return iArr;
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr
     * @return 身份证编码。
     */
    public static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (POWER.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < POWER.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * POWER[j];
                    }
                }
            }
        }
        return iSum;
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum
     * @return 校验位
     */
    public static String getCheckCode18(int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10:
                sCode = "2";
                break;
            case 9:
                sCode = "3";
                break;
            case 8:
                sCode = "4";
                break;
            case 7:
                sCode = "5";
                break;
            case 6:
                sCode = "6";
                break;
            case 5:
                sCode = "7";
                break;
            case 4:
                sCode = "8";
                break;
            case 3:
                sCode = "9";
                break;
            case 2:
                sCode = "x";
                break;
            case 1:
                sCode = "0";
                break;
            case 0:
                sCode = "1";
                break;
            default:
        }
        return sCode;
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard 身份编号
     * @return 年龄
     */
    public static int getAgeByIdCard(String idCard) {
        int iAge = 0;
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        String year = idCard.substring(6, 10);
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirthByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        return idCard.substring(6, 14);
    }

    /**
     * 根据身份编号获取生日年
     *
     * @param idCard 身份编号
     * @return 生日(yyyy)
     */
    public static Short getYearByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        return Short.valueOf(idCard.substring(6, 10));
    }

    /**
     * 根据身份编号获取生日月
     *
     * @param idCard 身份编号
     * @return 生日(MM)
     */
    public static Short getMonthByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        return Short.valueOf(idCard.substring(10, 12));
    }

    /**
     * 根据身份编号获取生日天
     *
     * @param idCard 身份编号
     * @return 生日(dd)
     */
    public static Short getDateByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        return Short.valueOf(idCard.substring(12, 14));
    }

    /**
     * 根据身份编号获取性别
     *
     * @param idCard 身份编号
     * @return 性别(M - 男 ， F - 女 ， N - 未知)
     */
    public static String getGenderByIdCard(String idCard) {
        String sGender = "N";
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            String tmp = conver15CardTo18(idCard);
            if (null != tmp && !tmp.isEmpty()) {
                idCard = tmp;
            }
        }
        String sCardNum = idCard.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "M";
        } else {
            sGender = "F";
        }
        return sGender;
    }

    /**
     * 根据身份编号获取户籍省份
     *
     * @param idCard 身份编码
     * @return 省级编码。
     */
    public static String getProvinceByIdCard(String idCard) {
        int len = idCard.length();
        String sProvince = null;
        String sProvinNum = "";
        if (len == CHINA_ID_MIN_LENGTH || len == CHINA_ID_MAX_LENGTH) {
            sProvinNum = idCard.substring(0, 2);
        }
        sProvince = CITYCODES.get(sProvinNum);
        return sProvince;
    }

    /**
     * 数字验证
     *
     * @param val
     * @return 提取的数字。
     */
    public static boolean isNum(String val) {
        return val != null && !"".equals(val) && val.matches("^[0-9]*$");
    }

    /**
     * 验证小于当前日期 是否有效
     *
     * @param iYear  待验证日期(年)
     * @param iMonth 待验证日期(月 1-12)
     * @param iDate  待验证日期(日)
     * @return 是否有效
     */
    public static boolean valiDate(int iYear, int iMonth, int iDate) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int datePerMonth;
        if (iYear < MIN || iYear >= year) {
            return false;
        }
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }
        switch (iMonth) {
            case 4:
            case 6:
            case 9:
            case 11:
                datePerMonth = 30;
                break;
            case 2:
                boolean dm = ((iYear % 4 == 0 && iYear % 100 != 0) || (iYear % 400 == 0))
                        && (iYear > MIN && iYear < year);
                datePerMonth = dm ? 29 : 28;
                break;
            default:
                datePerMonth = 31;
        }
        return (iDate >= 1) && (iDate <= datePerMonth);
    }
}