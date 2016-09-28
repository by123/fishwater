/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import com.sjy.ttclub.common.PathManager;
import com.sjy.ttclub.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    public static final byte WRITE_POS_CURRENT_POS = 0;
    public static final byte WRITE_POS_BEGIN = 1;
    public static final byte WRITE_POS_END = 2;
    public static final byte WRITE_POS_SPECIFIED = 3;

    private final static String TAG = "FileUtils";
    public static final String DEFAULT_NAME = "index.html";

    public final static String EXT_BAK = ".bak";

    /**
     * 计算文件夹大小
     *
     * @param item
     * @param stack ：递归传值栈，用于临时记录文件夹大小
     * @return
     */
    public static long caculateFileSize(File item, long stack) {
        if (item == null || !item.exists()) {
            return stack;
        }
        if (item.isDirectory()) {
            long temp = 0;
            File[] fileList = item.listFiles();
            if (null != fileList) {
                for (File subitem : fileList) {
                    temp += caculateFileSize(subitem, 0);
                }
            }
            return stack + temp;
        } else {
            return stack + item.length();
        }
    }

    /**
     * Reads a text file.
     *
     * @param fileName the name of the text file
     * @return the lines of the text file
     * @throws FileNotFoundException when the file was not found
     * @throws IOException           when file could not be read.
     */
    public static String[] readTextFile(String fileName)
            throws FileNotFoundException, IOException {
        return readTextFile(new File(fileName));
    }

    /**
     * Reads a text file.
     *
     * @param file the text file
     * @return the lines of the text file
     * @throws FileNotFoundException when the file was not found
     * @throws IOException           when file could not be read.
     */
    public static String[] readTextFile(File file)
            throws FileNotFoundException, IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }
        in.close();
        return (String[]) lines.toArray(new String[lines.size()]);
    }

    /**
     * Reads a text file.
     *
     * @param file     the text file
     * @param encoding the encoding of the textfile
     * @return the lines of the text file
     * @throws FileNotFoundException when the file was not found
     * @throws IOException           when file could not be read.
     */
    public static String[] readTextFile(File file, String encoding)
            throws FileNotFoundException, IOException {
        return readTextFile(new FileInputStream(file), encoding);
    }

    /**
     * Reads the text from the given input stream in the default encoding.
     *
     * @param in the input stream
     * @return the text contained in the stream
     * @throws IOException when stream could not be read.
     */
    public static String[] readTextFile(InputStream in) throws IOException {
        return readTextFile(in, null);
    }

    /**
     * Reads the text from the given input stream in the default encoding.
     *
     * @param in       the input stream
     * @param encoding the encoding of the textfile
     * @return the text contained in the stream
     * @throws IOException when stream could not be read.
     */
    public static String[] readTextFile(InputStream in, String encoding)
            throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader bufferedIn;
        if (encoding != null) {
            bufferedIn = new BufferedReader(new InputStreamReader(in, encoding));
        } else {
            bufferedIn = new BufferedReader(new InputStreamReader(in));
        }
        String line;
        while ((line = bufferedIn.readLine()) != null) {
            lines.add(line);
        }
        bufferedIn.close();
        in.close();
        return (String[]) lines.toArray(new String[lines.size()]);
    }

    /**
     * Writes (and creates) a text file.
     *
     * @param file  the file to which the text should be written
     * @param lines the text lines of the file in a collection with String-values
     * @throws IOException when there is an input/output error during the saving
     */
    public static void writeTextFile(File file, Collection<String> lines)
            throws IOException {
        writeTextFile(file, (String[]) lines.toArray(new String[lines.size()]),
                false);
    }

    /**
     * Writes (and creates) a text file.
     *
     * @param file
     * @param lines
     * @param isAppend , use true to append lines to the end of the file, or false to
     *                 overrwrite the file
     * @throws IOException
     */
    public static void writeTextFile(File file, Collection<String> lines,
                                     boolean isAppend) throws IOException {
        writeTextFile(file, (String[]) lines.toArray(new String[lines.size()]),
                isAppend);
    }

    /**
     * Writes (and creates) a text file.
     *
     * @param file  the file to which the text should be written
     * @param lines the text lines of the file
     * @throws IOException when there is an input/output error during the saving
     */
    public static void writeTextFile(File file, String[] lines, boolean isAppend)
            throws IOException {
        File parentDir = file.getParentFile();
        if ((parentDir != null) && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        PrintWriter out = new PrintWriter(new FileWriter(file, isAppend));
        for (int i = 0; i < lines.length; i++) {
            out.println(lines[i]);
        }

        out.close();
    }

    /**
     * Copies the given files to the specified target directory.
     *
     * @param files     The files which should be copied, when an array element is
     *                  null, it will be ignored.
     * @param targetDir The directory to which the given files should be copied to.
     * @throws FileNotFoundException when the source file was not found
     * @throws IOException           when there is an error while copying the file.
     * @throws NullPointerException  when files or targetDir is null.
     */
    public static void copy(File[] files, File targetDir)
            throws FileNotFoundException, IOException {
        copy(files, targetDir, false);
    }

    /**
     * Copies the given files to the specified target directory.
     *
     * @param files     The files which should be copied, when an array element is
     *                  null, it will be ignored.
     * @param targetDir The directory to which the given files should be copied to.
     * @param overwrite true when existing target files should be overwritten even
     *                  when they are newer
     * @throws FileNotFoundException when the source file was not found
     * @throws IOException           when there is an error while copying the file.
     * @throws NullPointerException  when files or targetDir is null.
     */
    public static void copy(File[] files, File targetDir, boolean overwrite)
            throws FileNotFoundException, IOException {
        String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
        byte[] buffer = new byte[64 * 1024];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file != null) {
                File targetFile = new File(targetPath + file.getName());
                if (!overwrite && targetFile.exists()
                        && targetFile.lastModified() > file.lastModified()) {
                    continue;
                }
                copy(file, targetFile, buffer);
            }
        }
    }

    /**
     * Copies a file.
     *
     * @param source The file which should be copied
     * @param target The file or directory to which the source-file should be
     *               copied to.
     * @throws FileNotFoundException when the source file was not found
     * @throws IOException           when there is an error while copying the file.
     */
    public static void copy(File source, File target)
            throws FileNotFoundException, IOException {
        copy(source, target, new byte[64 * 1024]);
    }

    /**
     * Copies a file.
     *
     * @param source The file which should be copied
     * @param target The file or directory to which the source-file should be
     *               copied to.
     * @param buffer A buffer used for the copying.
     * @throws FileNotFoundException when the source file was not found
     * @throws IOException           when there is an error while copying the file.
     */
    private static void copy(File source, File target, byte[] buffer)
            throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(source);
        // create parent directory of target-file if necessary:
        File parent = target.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        OutputStream out = new FileOutputStream(target);
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
            out.close();
        }
    }

    /**
     * Writes the properties which are defined in the given HashMap into a
     * textfile. The notation in the file will be [name]=[value]\n for each
     * defined property.
     *
     * @param file       the file which should be created or overwritten
     * @param properties the properties which should be written.
     * @throws IOException when there is an input/output error during the saving
     */
    public static void writePropertiesFile(File file,
                                           Map<String, String> properties) throws IOException {
        writePropertiesFile(file, '=', properties);
    }

    /**
     * Writes the properties which are defined in the given HashMap into a
     * textfile. The notation in the file will be [name]=[value]\n for each
     * defined property.
     *
     * @param file       the file which should be created or overwritten
     * @param delimiter  the character that separates a property-name from a
     *                   property-value.
     * @param properties the properties which should be written.
     * @throws IOException when there is an input/output error during the saving
     */
    public static void writePropertiesFile(File file, char delimiter,
                                           Map<String, String> properties) throws IOException {
        Object[] keys = properties.keySet().toArray();
        Arrays.sort(keys);
        String[] lines = new String[keys.length];
        for (int i = 0; i < lines.length; i++) {
            Object key = keys[i];
            Object value = properties.get(key);
            lines[i] = key.toString() + delimiter + value.toString();
        }
        writeTextFile(file, lines, false);
    }

    /**
     * Copies the contents of a directory to the specified target directory.
     *
     * @param directory     the directory containing files
     * @param targetDirName the directory to which the files should be copied to
     * @param update        is true when files should be only copied when the source files
     *                      are newer compared to the target files.
     * @throws IOException              when a file could not be copied
     * @throws IllegalArgumentException when the directory is not a directory.
     */
    public static void copyDirectoryContents(File directory,
                                             String targetDirName, boolean update) throws IOException {
        if (targetDirName == null || targetDirName.length() == 0) {
            return;
        }
        copyDirectoryContents(directory, new File(targetDirName), update);
    }

    /**
     * Copies the contents of a directory to the specified target directory.
     *
     * @param directory the directory containing files
     * @param targetDir the directory to which the files should be copied to
     * @param update    set to true when files should be only copied when the source
     *                  files are newer compared to the target files.
     * @throws IOException              when a file could not be copied
     * @throws IllegalArgumentException when the directory is not a directory.
     */
    public static void copyDirectoryContents(File directory, File targetDir,
                                             boolean update) throws IOException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Cannot copy contents of the file ["
                            + directory.getAbsolutePath()
                            + "]: specify a directory instead.");
        }
        String[] fileNames = directory.list();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            File file = new File(directory.getAbsolutePath(), fileName);
            if (file.isDirectory()) {
                copyDirectoryContents(file, targetDir.getAbsolutePath()
                        + File.separatorChar + fileName, update);
            } else {
                File targetFile = new File(targetDir, fileName);
                if (update) {
                    // update only when the source file is newer:
                    if ((!targetFile.exists())
                            || (file.lastModified() > targetFile.lastModified())) {
                        copy(file, targetFile);
                    }
                } else {
                    // copy the file in all cases:
                    copy(file, targetFile);
                }
            }
        }
    }

    public static boolean delete(File file, boolean includeSelf) {
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = delete(new File(file, children[i]), true);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        boolean success = true;
        if (includeSelf) {
            success = file.delete();
        }
        return success;
    }

    /**
     * Deletes a file or a directory.
     *
     * @param file the file or directory which should be deleted.
     * @return true when the file could be deleted
     */
    public static boolean delete(File file) {
        return delete(file, true);
    }

    /**
     * Writes the given textlines into the specified file.
     *
     * @param file     the file to which the text should be written
     * @param lines    the text lines of the file
     * @param encoding the encoding, e.g. "UTF8", null when the default encoding
     *                 should be used
     * @throws IOException when there is an input/output error during the saving
     */
    public static void writeTextFile(File file, String[] lines, String encoding)
            throws IOException {

        File parentDir = file.getParentFile();
        if ((parentDir != null) && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        PrintWriter out;
        if (encoding != null) {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    file), encoding));
        } else {
            out = new PrintWriter(new FileWriter(file));
        }

        for (int i = 0; i < lines.length; i++) {
            out.println(lines[i]);
        }
        out.close();
    }

    /**
     * Retrieves all files from the given directory
     *
     * @param dir       the directory
     * @param extension the file extension, when the extension is null, all files are
     *                  included
     * @param recursive true when subdirectories should also be read.
     * @return an String array with the file-names relative to the given
     * directory that do have the given extension
     */
    public static String[] filterDirectory(File dir, String extension,
                                           boolean recursive) {
        return filterDirectory(dir, extension, null, recursive);
    }

    public static String[] filterDirectory(File dir, String extension,
                                           FilenameFilter filenameFilter, boolean recursive) {
        if (dir == null || !dir.exists()) {
            return new String[0];
        }
        ArrayList<String> fileNamesList = new ArrayList<String>();
        filterDirectory("", dir, extension, recursive, filenameFilter,
                fileNamesList);
        return (String[]) fileNamesList
                .toArray(new String[fileNamesList.size()]);
    }

    /**
     * Retrieves all files from the given directory
     *
     * @param path      the start path taken from the base directory towards the
     *                  current one
     * @param dir       the directory
     * @param extension the file extension
     * @param recursive true when subdirectories should also be read.
     */
    private static void filterDirectory(String path, File dir,
                                        String extension, boolean recursive, FilenameFilter fileNameFilter,
                                        List<String> fileNamesList) {
        String[] names = dir.list(fileNameFilter);
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            File file = new File(dir, name);
            if (file.isDirectory()) {
                if (recursive) {
                    filterDirectory(path + name + File.separatorChar, file,
                            extension, recursive, null, fileNamesList);
                }
            } else if (extension == null || name.endsWith(extension)) {
                fileNamesList.add(path + name);
            }
        }
    }

    /**
     * Extracts the bytes from a file.
     *
     * @param file the file from which the bytes should be extracted from
     * @return a byte arry corresponding to the file. Is never null.
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            byte[] buffer = new byte[4096];
            inputStream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            byte[] byteArray = null;
            byteArray = outputStream.toByteArray();
            return byteArray;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static String genBackupFilePath(String filePath) {
        return filePath + EXT_BAK;
    }

    public static boolean writeBytes(String filePath, String fileName,
                                     byte[] headData, byte[] bodyData) {
        if (bodyData == null) {
            return false;
        }
        return writeBytes(filePath, fileName, headData, bodyData, 0,
                bodyData.length, false);
    }

    public static boolean writeBytes(String filePath, String fileName,
                                     byte[] data) {
        if (data == null) {
            return false;
        }
        return writeBytes(filePath, fileName, data, 0, data.length);
    }

    /**
     * 请尽量将这个函数放入ThreadManager的后台线程执行，防止引入严重卡顿
     *
     * @param filePath
     * @param fileName
     * @param headData
     * @param bodyData
     * @param bodyOffset
     * @param bodyLen
     * @param forceFlush 请慎重使用这个参数，如果设置为true可能导致严重卡顿，甚至ANR，如果不是极为重要的数据，请设置为false。
     * @return
     */
    public static boolean writeBytes(String filePath, String fileName,
                                     byte[] headData, byte[] bodyData, int bodyOffset, int bodyLen,
                                     boolean forceFlush) {
        if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(fileName)
                || bodyData == null) {
            return false;
        }

        String tempFileName = System.currentTimeMillis() + fileName;

        File tempFile = createNewFile(filePath + tempFileName);

        boolean result = writeBytes(tempFile, headData, bodyData, bodyOffset,
                bodyLen, forceFlush);
        if (!result) {
            return false;
        }

        String srcPath = filePath + fileName;
        if (!rename(tempFile, srcPath)) {
            // rename srcPath到bakPath后再 delete bakPath，替代直接 delete srcPath
            String bakPath = genBackupFilePath(srcPath);
            delete(bakPath);
            rename(new File(srcPath), bakPath);

            result = rename(tempFile, srcPath);
            if (!result) {
                return false;
            }

            delete(bakPath);
        }

        return true;
    }

    /**
     * 请尽量将这个函数放入ThreadManager的后台线程执行，防止引入严重卡顿
     *
     * @param raf          , RandomAccessFile object
     * @param mode
     * @param specifiedPos , the position start to write
     * @param data         , byte[] type
     * @return
     */
    public static boolean writeBytes(RandomAccessFile raf, byte mode,
                                     int specifiedPos, byte[] data) {
        if (null == raf || data == null || data.length == 0) {
            return false;
        }

        try {
            switch (mode) {
                case WRITE_POS_CURRENT_POS:
                    break;
                case WRITE_POS_BEGIN:
                    raf.seek(0);
                    break;
                case WRITE_POS_END: {
                    long len = raf.length();
                    raf.seek(len);
                    break;
                }
                case WRITE_POS_SPECIFIED: {
                    raf.seek(specifiedPos);
                    break;
                }
                default:
                    break;
            }

            raf.write(data);
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public static boolean writeBytes(String path, byte mode, int specifiedPos,
                                     byte[] data) {
        RandomAccessFile raf = openFile(path, false);
        if (null == raf) {
            return false;
        }

        boolean ret = writeBytes(raf, mode, specifiedPos, data);
        IOUtil.safeClose(raf);

        return ret;
    }

    public static boolean writeBytes(String filePath, String fileName,
                                     byte[] data, int offset, int len) {
        return writeBytes(filePath, fileName, null, data, offset, len, false);
    }

    /**
     * @param file
     * @param headData
     * @param bodyData
     * @param bodyOffset
     * @param bodyLen
     * @param forceFlush 请慎重使用这个参数，如果设置为true可能导致严重卡顿，甚至ANR，如果不是极为重要的数据，请设置为false
     * @return
     */
    public static boolean writeBytes(File file, byte[] headData,
                                     byte[] bodyData, int bodyOffset, int bodyLen, boolean forceFlush) {
        FileOutputStream fileOutput = null;
        try {
            fileOutput = new FileOutputStream(file);
            if (headData != null) {
                fileOutput.write(headData);
            }
            fileOutput.write(bodyData, bodyOffset, bodyLen);
            fileOutput.flush();
            if (forceFlush) {
                FileDescriptor fd = fileOutput.getFD();
                if (fd != null) {
                    fd.sync(); // 立刻刷新，保证文件可以正常写入;
                }
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Some errors", e);
        } finally {
            IOUtil.safeClose(fileOutput);
        }
        return false;
    }

    public static boolean writeBytes(File file, byte[] data, int offset, int len) {
        return writeBytes(file, null, data, offset, len, false);
    }

    public static File createNewFile(String path) {
        return createNewFile(path, false);
    }

    public static RandomAccessFile openFile(String path, boolean append) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(path, "rw");
            if (append) {
                long len = file.length();
                if (len > 0) {
                    file.seek(len);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return file;
    }

    public static boolean rename(File file, String newName) {
        return file.renameTo(new File(newName));
    }

    public static boolean delete(String path) {
        return delete(new File(path));
    }

    /**
     * @param path   ：文件路径
     * @param append ：若存在是否插入原文件
     * @return
     */
    public static File createNewFile(String path, boolean append) {
        File newFile = new File(path);
        if (!append) {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        }
        return newFile;
    }

    public static byte[] readBytes(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        return readBytes(new File(filePath));
    }

    public static byte[] readBytes(File file) {
        FileInputStream fileInput = null;
        try {
            if (file.exists()) {
                fileInput = new FileInputStream(file);
                return IOUtil.readByteArrayFromInputStream(fileInput);
            }
        } catch (Exception e) {
            Log.d(TAG, "Some errors", e);
        } finally {
            IOUtil.safeClose(fileInput);
            fileInput = null;
        }
        return null;
    }

    public final static long getSDCardTotalSize() {
        if (isSDCardExist()) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            if (sdcardDir == null || !sdcardDir.exists()) {
                return 0;
            }

            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            return blockSize * blockCount;
        }
        return 0;
    }

    public final static long getSDCardTotalSize(String path) {
        if (isPathExist(path)) {
            try {
                StatFs sf = new StatFs(path);
                long blockSize = sf.getBlockSize();
                long blockCount = sf.getBlockCount();
                return blockSize * blockCount;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public final static long getSDCardAvailableSize() {
        if (isSDCardExist()) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            if (sdcardDir == null || !sdcardDir.exists()) {
                return 0;
            }

            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            return blockSize * availCount;
        }

        return 0;
    }

    public final static long getSDCardAvailableSize(String path) {
        long size = -1;
        if (isPathExist(path)) {
            StatFs sf = new StatFs(path);
            size = 1L * sf.getBlockSize() * sf.getAvailableBlocks();
        }
        return size;
    }

    public final static long getSystemTotalSize() {
        File root = Environment.getRootDirectory();
        if (root == null || !root.exists()) {
            return 0;
        }

        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        return blockSize * blockCount;
    }

    public final static long getSystemAvailable() {
        File root = Environment.getRootDirectory();
        if (root == null || !root.exists()) {
            return 0;
        }

        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getAvailableBlocks();
        return blockSize * blockCount;
    }

    /**
     * 是否存在SDCard
     *
     * @return
     */
    public static boolean isSDCardExist() {
        try {
            return Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPathExist(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        return file.exists();
    }

    public static String getLeaf(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        int lastSlashPos = path.lastIndexOf("/");
        if (-1 == lastSlashPos) {
            lastSlashPos = path.lastIndexOf("\\");
        }

        return path.substring(lastSlashPos + 1);

    }

    /**
     * 把file://开头的local uri字符串的file://前缀去掉
     */
    public static String convertLocalFileUri2FileName(String uriStr) {
        if (StringUtils.isEmpty(uriStr)) {
            return uriStr;
        }

        String preFix = "file://";
        if (uriStr.length() > preFix.length()) {
            if (uriStr.startsWith("file://")) {
                uriStr = uriStr.substring(preFix.length());
            }
        }

        return uriStr;
    }

    /**
     * 通过Uri获取图片真正路径:比如 uri content://media/external/images/media/70020
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getImageRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }


    public static final byte SUCCESS = 0;
    public static final byte NULL_FILE = 1;
    public static final byte ALREADY_FILE = 2;
    public static final byte MKDIR_ERROR = 3;
    public static final byte NO_SPACE = 4;
    public static final byte UNKONW_ERROR = 5;
    public static final byte NOT_ALL = 6;
    public static final byte FAILED = 7;

    public static byte makeDirs(String path) {
        if (StringUtils.isEmpty(path)) {
            return NULL_FILE;
        }
        File file = new File(path);
        if (file.exists()) {
            return ALREADY_FILE;
        }
        if (file.mkdirs()) {
            return SUCCESS;
        }
        return UNKONW_ERROR;
    }

    /**
     * 复制文件/文件夹
     *
     * @param item
     * @param destdir
     * @return
     */
    public static byte copyFile(File item, String destdir) {
        if (item == null || !item.exists()) {
            return NULL_FILE;
        }
        String newFileName = destdir + "/" + item.getName();
        File destFile = new File(newFileName);
        int pos = 1;
        if (destFile.exists()) {
            String itemName = item.getName();
            String itemExtention;
            if (-1 != itemName.lastIndexOf('.')) {
                itemExtention = itemName.substring(itemName.lastIndexOf('.'));
                itemName = itemName.substring(0, itemName.lastIndexOf('.'));
            } else {
                itemExtention = itemName;
                itemName = "";
            }
            StringBuffer sb = new StringBuffer();
            while (destFile.exists()) {
                sb.delete(0, sb.length());
                sb.append(destdir).append("/").append(itemName).append("(")
                        .append(pos).append(")").append(itemExtention);
                newFileName = sb.toString();
                destFile = new File(newFileName);
                pos++;
            }
        }
        if (item.isDirectory()) {
            if (!destFile.mkdir()) {
                return MKDIR_ERROR;
            }
            if (null != item.listFiles()) {
                for (File subitem : item.listFiles()) {
                    copyFile(subitem, destFile.getPath());
                }
            }
        } else {
            try {
                copyOneFile(item, destFile);
            } catch (Exception e) { // 空间不足
                return NO_SPACE;
            }
        }
        return SUCCESS;
    }

    /**
     * 复制一个文件
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyOneFile(File sourceFile, File destFile)
            throws IOException {
        copyOneFile(sourceFile, destFile, true);
    }

    /**
     * @param sourceFile
     * @param destFile
     * @param isAppend
     * @throws IOException
     */
    public static void copyOneFile(File sourceFile, File destFile,
                                   boolean isAppend) throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(destFile, isAppend);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);
        byte[] b = new byte[1024 * 8];
        int len;
        try {
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
        } finally {
            outBuff.flush();
            // 关闭流
            inBuff.close();
            input.close();
            outBuff.close();
            output.close();
        }

    }

    public static String getExtension(String fileName) {
        if (null == fileName) {
            return null;
        }

        int dotIndex = fileName.lastIndexOf('.');
        // 如果.在第一位或者最后一位，则返回NULL
        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(dotIndex + 1);
    }

    public static String getFileNameFromPath(String filePath) {
        return getFileNameFromPath(filePath, false);
    }

    public static String getFileNameFromPath(String filePath, boolean includeExtension) {
        if (null == filePath) {
            return null;
        }

        int splitIndex = filePath.lastIndexOf(File.separator);
        if (splitIndex >= 0 && splitIndex < filePath.length()) {
            String fileName = filePath.substring(splitIndex + 1);
            int dotIndex = fileName.lastIndexOf('.');
            // 如果.在第一位或者最后一位，则返回NULL
            if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
                return null;
            }

            return fileName.substring(0, dotIndex);
        }
        return filePath;
    }

    public static boolean isFileExists(String strFile) {
        if (StringUtils.isEmpty(strFile)) {
            return false;
        }
        try {
            File file = new File(strFile);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝asset目录下文件到指定文件
     *
     * @param fromFilePath 相对路径
     * @param toFilePath   绝对路径
     */
    public static void copyAssetFile(Context context, String fromFilePath,
                                     String toFilePath) {
        InputStream in = null;
        OutputStream out = null;

        try {
            AssetManager mAssetMgr = context.getAssets();
            in = mAssetMgr.open(fromFilePath);

            String newPath = toFilePath;

            out = new FileOutputStream(newPath);

            byte[] buffer = new byte[8096];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAssetsFile(String path) {
        boolean result = true;
        if (StringUtils.isEmpty(path)) {
            result = false;
            return result;
        }
        if (path.startsWith("file://") || path.startsWith("content://")) {
            result = false;
            return result;
        }
        if (path.startsWith("/sdcard/")) {
            result = false;
            return result;
        } else {
            String sdPath = StorageHelper.getSdcardPath();
            String sdSecondPath = StorageHelper.getSdcardSecondPath();
            if (path.startsWith(sdPath) || path.startsWith(sdSecondPath)) {
                result = false;
                return result;
            }
            if (path.startsWith(PathManager.getInstance().getDataPath())) {
                result = false;
                return result;
            }
        }

        return result;
    }

    public static byte[] zipData(byte[] data) {
        if (null == data || 0 == data.length) {
            return null;
        }

        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(data);

            gos.finish();
            gos.flush();
            gos.close();

            baos.flush();
            baos.close();

            result = baos.toByteArray();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] unZipData(byte[] data) {
        if (null == data || 0 == data.length) {
            return null;
        }

        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gis = new GZIPInputStream(bais);

            byte[] tmpBuf = new byte[4096];
            int readlen = 0;
            while ((readlen = gis.read(tmpBuf)) != -1) {
                baos.write(tmpBuf, 0, readlen);
            }

            gis.close();
            result = baos.toByteArray();

            bais.close();
            baos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
