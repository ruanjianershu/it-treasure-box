/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: matieli<ma_tl   @   suixingpay.com>
 * @date: 2017年3月8日 下午1:25:06
 * @Copyright ©2017 Suixingpay. All rights reserved. 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.it.treasurebox.util.net;

import com.it.treasurebox.util.text.StringMoreUtils;
import com.it.treasurebox.util.number.NumberMoreUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * InetAddress工具类，基于Guava的InetAddresses. 主要包含int, String/IPV4String,
 * InetAdress/Inet4Address之间的互相转换
 * 先将字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
 * InetAddress与String的转换其实消耗不小，如果是有限的地址，建议进行缓存.
 *
 */
@Slf4j
public abstract class IPUtils {
    /**
     * 获取本地IP
     *
     * @return
     */
    public static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            int lowest = Integer.MAX_VALUE;
            for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces(); nics
                    .hasMoreElements(); ) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    log.trace("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex();
                    } else if (result != null) {
                        continue;
                    }

                    for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements(); ) {
                        InetAddress address = addrs.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            log.trace("Found non-loopback interface: " + ifc.getDisplayName());
                            result = address;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Cannot get first non-loopback address", ex);
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.warn("Unable to retrieve localhost");
        }

        return null;
    }


    /**
     * 从IPv4String转换为InetAddress. IpString如果确定ipv4, 使用本方法减少字符分析消耗 .
     * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
     *
     * @param address
     * @return
     */
    public static Inet4Address fromIpv4String(String address) {
        byte[] bytes = ip4StringToBytes(address);
        if (bytes == null) {
            return null;
        } else {
            try {
                return (Inet4Address) Inet4Address.getByAddress(bytes);
            } catch (UnknownHostException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * int转换到IPV4 String, from Netty NetUtil
     *
     * @param i
     * @return
     */
    public static String intToIpv4String(int i) {
        return new StringBuilder(15).append(i >> 24 & 0xff).append('.').append(i >> 16 & 0xff).append('.')
                .append(i >> 8 & 0xff).append('.').append(i & 0xff).toString();
    }

    /**
     * Ipv4 String 转换到int
     *
     * @param ipv4Str
     * @return
     */
    public static int ipv4StringToInt(String ipv4Str) {
        byte[] byteAddress = ip4StringToBytes(ipv4Str);
        if (byteAddress == null) {
            return 0;
        } else {
            return NumberMoreUtils.toInt(byteAddress);
        }
    }

    /**
     * Ipv4 String 转换到byte[]
     *
     * @param ipv4Str
     * @return
     */
    private static byte[] ip4StringToBytes(String ipv4Str) {
        if (ipv4Str == null) {
            return null;
        }

        List<String> it = StringMoreUtils.split(ipv4Str, '.', 4);
        if (it.size() != 4) {
            return null;
        }

        byte[] byteAddress = new byte[4];
        for (int i = 0; i < 4; i++) {
            int tempInt = Integer.parseInt(it.get(i));
            if (tempInt > 255) {
                return null;
            }
            byteAddress[i] = (byte) tempInt;
        }
        return byteAddress;
    }

}
