package com.sina.poi.excel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @ClassName FileUtils
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 14:21
 * @Version 1.0
 */
public abstract class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private static final Pattern FILE_PREFIX_PATTERN = Pattern.compile("^.+(\\(\\d+\\))$");


    protected FileUtils() {

    }

    public static File createTempFile() throws IOException {
        File directory, file;
        directory = new File(System.getProperty("java.io.tmpdir"), "e_finance_temp_i");
        createIfNotExists(directory);

        file = new File(directory, UUID.randomUUID().toString());
        createIfNotExists(file);
        return file;
    }

    public static void createIfNotExists(File file) throws IOException {

        if (file.exists()) {
            return;
        }
        File dir = file.getParentFile();
        if (!dir.exists() && dir.mkdirs()) {
            LOG.trace("file[{}]create success", dir.getAbsolutePath());

        }
        if (!file.exists() && file.createNewFile()) {
            LOG.trace("file[{}]create success", file.getAbsolutePath());
        }
    }

    /**
     * 若文件名存在 则 追加序号作为文件名(如:temp.txt 追加序号为 temp(1).txt),直到不存在才创建文件
     *
     * @return 若文件名已存在则返回新的 {@link File} ,若不存在则返回原 {@link File}
     */
    public static File createNewNamedFileIfExists(final File file) throws IOException {

        final String fileNamePrefix = StringUtils.getFileNamePrefix(file.getName());
        final String actualPrefix = parseFileNamePrefix(fileNamePrefix);
        final String extension = StringUtils.getFilenameExtension(file.getName());
        String prefix, newName;

        File newFile = file;
        for (int i = getFileNameNumber(fileNamePrefix); ; i++) {
            if (newFile.exists()) {
                prefix = String.format("%s(%s)", actualPrefix, i);
                newName = String.format("%s.%s", prefix, extension);
                newFile = new File(file.getParent(), newName);
            } else {
                createIfNotExists(newFile);
                break;
            }

        }
        return newFile;
    }

    private static String parseFileNamePrefix(String fileName) {
        String prefix = fileName;
        if (FILE_PREFIX_PATTERN.matcher(fileName).matches()) {
            int index;
            index = fileName.lastIndexOf('(');
            if (index > 0) {
                prefix = fileName.substring(0, index);
            }
        }
        return prefix;
    }

    private static int getFileNameNumber(String fileNamePrefix) {
        if (!fileNamePrefix.endsWith(")")) {
            return 1;
        }
        Integer num, index;
        index = fileNamePrefix.lastIndexOf('(');
        if (index <= 0) {
            return 1;
        }
        try {
            num = Integer.parseInt(
                    fileNamePrefix.substring(index + 1, fileNamePrefix.length() - 1)
            );
            num++;
        } catch (NumberFormatException e) {
            num = 1;
        }

        return num;
    }
}