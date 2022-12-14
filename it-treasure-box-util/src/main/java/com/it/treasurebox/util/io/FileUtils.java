/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: matieli<ma_tl       @       suixingpay.com>
 * @date: 2017年3月8日 下午1:25:06
 * @Copyright ©2017 Suixingpay. All rights reserved. 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.it.treasurebox.util.io;

import com.google.common.base.Predicate;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * 关于文件的工具集 代码基本从调用Guava Files, 固定encoding为UTF8.
 * <p>
 * 1.文件读写
 * </p>
 * <p>
 * 2.文件及目录操作
 * </p>
 *
 * @author: matieli[ma_tl@suixingpay.com]
 * @created: 2017-01-22 17:32
 */
public abstract class FileUtils {

    //////// 文件读写//////

    /**
     * 读取文件到byte[].
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(final File file) throws IOException {
        return Files.toByteArray(file);
    }

    /**
     * 读取文件到String.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String toString(final File file) throws IOException {
        return Files.toString(file, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件的每行内容到List
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String> toLines(final File file) throws IOException {
        return Files.readLines(file, StandardCharsets.UTF_8);
    }

    /**
     * 简单写入String到File.
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public static void write(final CharSequence data, final File file) throws IOException {
        Files.write(data, file, StandardCharsets.UTF_8);
    }

    /**
     * 追加String到File.
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void append(final CharSequence from, final File to) throws IOException {
        Files.append(from, to, StandardCharsets.UTF_8);
    }

    /**
     * 打开文件为InputStream
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static InputStream asInputStream(String fileName) throws IOException {
        return new FileInputStream(getFileByPath(fileName));
    }

    /**
     * 打开文件为OutputStream
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static OutputStream asOututStream(String fileName) throws IOException {
        return new FileOutputStream(getFileByPath(fileName));
    }

    /**
     * 获取File的BufferedReader
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static BufferedReader asBufferedReader(String fileName) throws FileNotFoundException {
        return Files.newReader(getFileByPath(fileName), StandardCharsets.UTF_8);
    }

    /**
     * 获取File的BufferedWriter
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static BufferedWriter asBufferedWriter(String fileName) throws FileNotFoundException {
        return Files.newWriter(getFileByPath(fileName), StandardCharsets.UTF_8);
    }

    ///// 文件操作 /////

    /**
     * 复制文件或目录
     *
     * @param from 如果为null，或者是不存在的文件或目录，抛出异常.
     * @param to   如果为null，或者from是目录而to是已存在文件，或相反
     * @throws IOException
     */
    public static void copy(File from, File to) throws IOException {
        Validate.notNull(from);

        if (from.isDirectory()) {
            copyDir(from, to);
        } else {
            copyFile(from, to);
        }
    }

    /**
     * 文件复制.
     *
     * @param from 如果为nll，或文件不存在或者是目录，抛出异常
     * @param to   如果to为null，或文件存在但是一个目录，抛出异常
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
        Validate.notNull(to);
        Validate.isTrue(!FileUtils.isDirExists(to), to + " is exist but it is a dir");
        Files.copy(from, to);
    }

    /**
     * 复制目录
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copyDir(File from, File to) throws IOException {
        Validate.isTrue(isDirExists(from), from + " is not exist or not a dir");
        Validate.notNull(to);

        if (to.exists()) {
            Validate.isTrue(!to.isFile(), to + " is exist but it is a file");
        } else {
            to.mkdirs();
        }

        File[] files = from.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String name = files[i].getName();
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }
                copy(files[i], new File(to, name));
            }
        }
    }

    /**
     * 文件移动/重命名.
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void moveFile(File from, File to) throws IOException {
        Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
        Validate.notNull(to);
        Validate.isTrue(!isDirExists(to), to + " is  exist but it is a dir");

        Files.move(from, to);
    }

    /**
     * 目录移动/重命名
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void moveDir(File from, File to) throws IOException {
        Validate.isTrue(isDirExists(from), from + " is not exist or not a dir");
        Validate.notNull(to);
        Validate.isTrue(!isFileExists(to), to + " is exist but it is a file");

        final boolean rename = from.renameTo(to);
        if (!rename) {
            if (to.getCanonicalPath().startsWith(from.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + from + " to a subdirectory of itself: " + to);
            }
            copyDir(from, to);
            deleteDir(from);
            if (from.exists()) {
                throw new IOException("Failed to delete original directory '" + from + "' after copy to '" + to + "'");
            }
        }
    }

    /**
     * 创建文件或更新时间戳.
     *
     * @param filePath
     * @throws IOException
     */
    public static void touch(String filePath) throws IOException {
        Files.touch(getFileByPath(filePath));
    }

    /**
     * 创建文件或更新时间戳.
     *
     * @param file
     * @throws IOException
     */
    public static void touch(File file) throws IOException {
        Files.touch(file);
    }

    /**
     * 删除文件. 如果文件不存在或者是目录，则不做修改
     *
     * @param file
     * @throws IOException
     */
    public static void deleteFile(File file) throws IOException {
        Validate.isTrue(isFileExists(file), file + " is not exist or not a file");
        file.delete();
    }

    /**
     * 删除目录及所有子目录/文件
     *
     * @param dir
     */
    public static void deleteDir(File dir) {
        Validate.isTrue(isDirExists(dir), dir + " is not exist or not a dir");

        // 后序遍历，先删掉子目录中的文件/目录
        Iterator<File> iterator = Files.fileTreeTraverser().postOrderTraversal(dir).iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }

    /**
     * 前序递归列出所有文件, 包含文件与目录，及根目录本身. 前序即先列出父目录，在列出子目录. 如要后序遍历,
     * 直接使用Files.fileTreeTraverser()
     *
     * @param rootDir
     * @return
     */
    public static List<File> listAll(File rootDir) {
        return Files.fileTreeTraverser().preOrderTraversal(rootDir).toList();
    }

    /**
     * 前序递归列出所有文件, 只包含文件.
     *
     * @param rootDir
     * @return
     */
    public static List<File> listFile(File rootDir) {
        return Files.fileTreeTraverser().preOrderTraversal(rootDir).filter(Files.isFile()).toList();
    }

    /**
     * 前序递归列出所有文件, 只包含后缀名匹配的文件. （后缀名不包含.）
     *
     * @param rootDir
     * @param extension
     * @return
     */
    public static List<File> listFileWithExtension(final File rootDir, final String extension) {
        return Files.fileTreeTraverser().preOrderTraversal(rootDir).filter(new FileExtensionFilter(extension)).toList();
    }

    /**
     * 直接使用Guava的TreeTraverser，获得更大的灵活度, 比如加入各类filter，前序/后序的选择，一边遍历一边操作
     *
     * <pre>
     * FileUtil.fileTreeTraverser().preOrderTraversal(root).iterator();
     * </pre>
     *
     * @return
     */
    public static TreeTraverser<File> fileTreeTraverser() {
        return Files.fileTreeTraverser();
    }

    /**
     * 判断目录是否存在, from Jodd
     *
     * @param dirPath
     * @return
     */
    public static boolean isDirExists(String dirPath) {
        return isDirExists(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在, from Jodd
     *
     * @param dir
     * @return
     */
    public static boolean isDirExists(File dir) {
        if (dir == null) {
            return false;
        }
        return dir.exists() && dir.isDirectory();
    }

    /**
     * 确保目录存在, 如不存在则创建
     *
     * @param dirPath
     * @throws IOException
     */
    public static void makeSureDirExists(String dirPath) throws IOException {
        File file = getFileByPath(dirPath);
        if (null != file) {
            makeSureDirExists(file);
        }
    }

    /**
     * 确保目录存在, 如不存在则创建
     *
     * @param file
     * @throws IOException
     */
    public static void makeSureDirExists(File file) throws IOException {
        Validate.notNull(file);
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException("There is a file exists " + file);
            }
        } else {
            file.mkdirs();
        }
    }

    /**
     * 确保父目录及其父目录直到根目录都已经创建.
     *
     * @param file
     * @throws IOException
     * @see Files#createParentDirs(File)
     */
    public static void createParentDirs(File file) throws IOException {
        Files.createParentDirs(file);
    }

    /**
     * 判断文件是否存在, from Jodd
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String fileName) {
        return isFileExists(getFileByPath(fileName));
    }

    /**
     * 判断文件是否存在, from Jodd
     *
     * @param file
     * @return
     */
    public static boolean isFileExists(File file) {
        if (file == null) {
            return false;
        }
        return file.exists() && file.isFile();
    }

    /**
     * 在临时目录创建临时目录，命名为${毫秒级时间戳}-${同一毫秒内的计数器}, from guava
     *
     * @return
     * @see Files#createTempDir()
     */
    public static File createTempDir() {
        return Files.createTempDir();
    }

    /**
     * 在临时目录创建临时文件，命名为tmp-${random.nextLong()}.tmp
     *
     * @return
     * @throws IOException
     */
    public static File createTempFile() throws IOException {
        return File.createTempFile("tmp-", ".tmp");
    }

    /**
     * 在临时目录创建临时文件，命名为${prefix}${random.nextLong()}${suffix}
     *
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }

    /**
     * getFileByPath
     *
     * @param filePath
     * @return
     */
    private static File getFileByPath(String filePath) {
        return StringUtils.isBlank(filePath) ? null : new File(filePath);
    }

    /**
     * 以文件名后缀做filter，配合fileTreeTraverser使用
     *
     * @author: matieli<ma_tl   @   suixingpay.com>
     * @date: 2017年3月14日 下午6:31:18
     * @version: V1.0
     */
    public static final class FileExtensionFilter implements Predicate<File> {
        private final String extension;

        private FileExtensionFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean apply(File input) {
            return input.isFile() && extension.equals(FilePathUtils.getFileExtension(input));
        }
    }
}
