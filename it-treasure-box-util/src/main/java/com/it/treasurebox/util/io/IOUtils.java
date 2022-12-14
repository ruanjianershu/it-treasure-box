/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: matieli<ma_tl   @   suixingpay.com>
 * @date: 2017年3月8日 下午1:25:06
 * @Copyright ©2017 Suixingpay. All rights reserved. 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.it.treasurebox.util.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * IO Stream/Reader相关工具集 建议使用Apache Commons IO, 在未引入Commons IO时可以用本类做最基本的事情.
 * 代码基本从Apache Commmons IO中化简移植, 固定encoding为UTF8. 1. 安静关闭Closeable对象 2.
 * 读出InputStream/Reader内容到String 或 List<String>(from Commons IO) 3.
 * 将String写到OutputStream/Writer(from Commons IO) 4.
 * InputStream/Reader与OutputStream/Writer之间复制的copy(from Commons IO)
 *
 * @author: matieli[ma_tl@suixingpay.com]
 * @created: 2017-01-22 17:42
 */
public abstract class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    // private static final String CLOSE_ERROR_MESSAGE = "IOException thrown
    // while closing Closeable.";

    /**
     * 在final中安静的关闭, 不再往外抛出异常避免影响原有异常，最常用函数. 同时兼容Closeable为空未实际创建的情况.
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // logger.warn(CLOSE_ERROR_MESSAGE, e);
            }
        }
    }

    /**
     * For JDK6 which ZipFile is not Closeable.
     *
     * @param zipfile
     */
    public static void closeQuietly(ZipFile zipfile) {
        if (zipfile != null) {
            try {
                zipfile.close();
            } catch (IOException e) {
                // logger.warn(CLOSE_ERROR_MESSAGE, e);
            }
        }
    }

    public static void closeQuietly(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (IOException e) {
                // logger.warn(CLOSE_ERROR_MESSAGE, e);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    /**
     * For JDK6 which Socket is not Closeable.
     *
     * @param socket
     */
    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // logger.warn(CLOSE_ERROR_MESSAGE, e);
            }
        }
    }

    /**
     * 简单读取InputStream到String.
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static String toString(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        return toString(reader);
    }

    /**
     * 简单读取Reader到String
     */
    public static String toString(Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        StringWriter sw = new StringWriter();
        copy(reader, sw);
        return sw.toString();
    }

    /**
     * 简单读取Reader的每行内容到List
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static List<String> toLines(final InputStream input) throws IOException {
        return toLines(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    /**
     * 简单读取Reader的每行内容到List
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static List<String> toLines(final Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        final List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    /**
     * 简单写入String到OutputStream.
     *
     * @param data
     * @param output
     * @throws IOException
     */
    public static void write(final String data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 简单写入String到Writer.
     */
    public static void write(final String data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * 在Reader与Writer间复制内容
     *
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static long copy(final Reader input, final Writer output) throws IOException {
        final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 在InputStream与OutputStream间复制内容
     *
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static long copy(final InputStream input, final OutputStream output) throws IOException {

        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * toBufferedReader
     *
     * @param reader
     * @return
     */
    public static BufferedReader toBufferedReader(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }
}
