/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: chi_yj[chi_yj@suixingpay.com]
 * @date: 2019年04月11日 18时17分
 * @Copyright 2019 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.it.treasurebox.util.text;

import org.apache.commons.lang3.StringUtils;


public class DesensitizeUtils {

    private static final String DESENSITIZE_CODE = "*";

    /**
     * 根据value长度取值(切分)
     *
     * @param value
     * @return 返回值长度等于入参长度
     */
    public static String desensitizeByLength(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        int length = value.length();
        StringBuilder str = new StringBuilder(length);
        if (length == 2) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE);
        } else if (length == 3) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE).append(value, length - 1, length);
        } else if (length > 3 && length <= 5) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(value, length - 2, length);
        } else if (length > 5 && length <= 7) {
            str.append(value, 0, 2).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(value, length - 2, length);
        } else if (length > 7) {
            str.append(value, 0, 3);
            for (int i = 0; i < length - 6; i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(value, length - 3, length);
        }
        return str.toString();
    }

    /**
     * 根据value长度取值(切分)
     * 过长字符串中间用4个*
     *
     * @param value
     * @return
     */
    public static String desensitizeByLengthFour(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        int length = value.length();
        StringBuilder str = new StringBuilder(length);
        if (length == 2) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE);
        } else if (length == 3) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE).append(value, length - 1, length);
        } else if (length > 3 && length <= 5) {
            str.append(value, 0, 1).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(value, length - 2, length);
        } else if (length > 5 && length <= 7) {
            str.append(value, 0, 2).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(DESENSITIZE_CODE).append(value, length - 2, length);
        } else if (length > 7) {
            str.append(value, 0, 3);
            for (int i = 0; i < 4; i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(value, length - 3, length);
        }
        return str.toString();
    }


    /**
     * 中文名称脱敏策略：
     * 0. 少于等于1个字 直接返回
     * 1. 两个字 隐藏姓
     * 2. 三个及其以上 只保留第一个和最后一个 其他用星号代替
     *
     * @param fullName
     * @return
     */
    public static String desensitizeChineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return StringUtils.EMPTY;
        }
        int length = fullName.length();
        if (length <= 1) {
            return fullName;
        } else if (length == 2) {
            StringBuilder str = new StringBuilder(2);
            return str.append(DESENSITIZE_CODE).append(fullName, length - 1, length).toString();
        } else {
            StringBuilder str = new StringBuilder(length);
            str.append(fullName, 0, 1);
            for (int i = 0; i < length - 2; i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(fullName, length - 1, length);
            return str.toString();
        }
    }


    /**
     * 二代身份证号脱敏加密：
     * XXXXXX XXXXXXXX XXXX
     * 脱敏规则：出生年月脱敏
     * 区分18位和15位
     *
     * @param id
     * @return
     */
    public static String desensitizeIdCardNum(final String id) {
        if (StringUtils.isBlank(id)) {
            return StringUtils.EMPTY;
        }
        StringBuilder str = new StringBuilder(id.length());
        int length = id.length();
        str.append(id, 0, 6);
        if (length == 15) {
            for (int i = 0; i < 6; i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(id, length - 3, length);
        } else if (length == 18) {
            for (int i = 0; i < 8; i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(id, length - 4, length);
        } else {
            throw new IllegalArgumentException("身份证号必须是15或18位");
        }
        return str.toString();
    }

    /**
     * [固定电话] 后四位，其他隐藏<例子：****1234>
     *
     * @param num
     * @return
     */
    public static String desensitizePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        int length = num.length();
        if (length < 8) {
            throw new IllegalArgumentException("固定电话长度不能小于8位");
        }
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length - 4; i++) {
            str.append(DESENSITIZE_CODE);
        }
        return str.append(num, length - 4, length).toString();
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138******1234>
     *
     * @param num
     * @return
     */
    public static String desensitizeMobilePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        int length = num.length();
        if (length < 11) {
            throw new IllegalArgumentException("手机号长度不能小于11位");
        }
        StringBuilder str = new StringBuilder(length);
        str.append(num, 0, 3);
        for (int i = 0; i < length - 7; i++) {
            str.append(DESENSITIZE_CODE);
        }
        return str.append(num, length - 4, length).toString();

    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
     *
     * @param address
     * @param sensitiveSize
     * @return
     */
    public static String desensitizeAddress(final String address, final int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return StringUtils.EMPTY;
        }
        final int length = StringUtils.length(address);
        StringBuilder str = new StringBuilder(length);
        str.append(address, 0, length - sensitiveSize);
        for (int i = 0; i < sensitiveSize; i++) {
            str.append(DESENSITIZE_CODE);
        }
        return str.toString();
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     *
     * @param email
     * @return
     */
    public static String desensitizeEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return StringUtils.EMPTY;
        }
        final int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            int length = email.length();
            StringBuilder str = new StringBuilder(length);
            str.append(email, 0, 1);
            for (int i = 0; i < length - 1 - (length - index); i++) {
                str.append(DESENSITIZE_CODE);
            }
            str.append(email, index, length);
            return str.toString();
        }
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String desensitizeBankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return StringUtils.EMPTY;
        }
        int length = cardNum.length();
        if (length < 10) {
            throw new IllegalArgumentException("银行卡号不能小于10位");
        }
        StringBuilder str = new StringBuilder(length);
        str.append(cardNum, 0, 6);
        for (int i = 0; i < length - 10; i++) {
            str.append(DESENSITIZE_CODE);
        }
        return str.append(cardNum, length - 4, length).toString();
    }

    /**
     * [企业开户银行联号] 企业开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     *
     * @param code
     * @return
     */
    public static String desensitizeCnapsCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return StringUtils.EMPTY;
        }
        int length = code.length();
        if (length < 12) {
            throw new IllegalArgumentException("企业开户银行联号不能小于12位");
        }
        StringBuilder str = new StringBuilder(length);
        str.append(code, 0, 2);
        for (int i = 0; i < length - 2; i++) {
            str.append(DESENSITIZE_CODE);
        }
        return str.toString();
    }

}

