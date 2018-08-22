package com.sina.poi.excel.utils;

import org.springframework.util.Assert;

import java.util.List;


/**
 * @ClassName StringUtils
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 14:21
 * @Version 1.0
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {

    protected StringUtils() {

    }

    /**
     * 从 Http {@code Content-Disposition} value 中获取文件名.
     *
     * @return 若未获取到则返回 {@code null}
     * @throws IllegalArgumentException - headerValue 不符合 http 规范
     * @see <a href="http://tools.ietf.org/html/rfc6266">RFC 6266</a>
     */
    public static String getFileNameFromDisposition(List<String> headerValue) {
        Assert.notNull( headerValue );
        Assert.isTrue( !headerValue.isEmpty() );

        String fileName = headerValue.get( 0 );

        String[] values = fileName.split( ";" );
        Assert.isTrue( values.length > 1 );
        String prefix;
        prefix = "filename=";

        if (values[1].length() > prefix.length()) {
            fileName = values[1].substring( prefix.length() );
        } else {
            fileName = null;
        }

        return fileName;
    }

    /**
     * Extract the filename prefix from the given path,
     * e.g. "mypath/myfile.txt" -> "myfile".
     * e.g. "myfile.txt" -> "myfile".
     *
     * @param pathOrFileName the file path (may be {@code null})
     * @return the extracted filename, or {@code null} if none
     */
    public static String getFileNamePrefix(String pathOrFileName) {
        String filename = getFilename( pathOrFileName );
        int index = filename.lastIndexOf( '.' );
        String filenamePrefix;
        if (index <= 0) {
            // 没有扩展名或为 linux 隐藏文件
            filenamePrefix = filename;
        } else {
            filenamePrefix = filename.substring( 0, index );
        }
        return filenamePrefix;
    }

    /**
     * {@code null} 最大子串.
     * <ul>
     * <li>当 text {@link #hasText(String)}  为 {@code false } 时,返回 空串.</li>
     * <li>当 text 长度 大于 len 返回长度为 len 的子串</li>
     * <li>否则返回 text 本身</li>
     * </ul>
     */
    public static String safeMaxSubstring(String text, int len) {
        String sub = "";
        if (hasText( text )) {
            if (text.length() > len) {
                sub = text.substring( 0, len );
            } else {
                sub = text;
            }
        }
        return sub;
    }

    /**
     * 若 target  不为 null 则 返回 target + text,否则返回 text
     *
     * @param target nullable
     * @param text   not null
     */
    public static String append(String target, String text) {
        if (target == null) {
            return text;
        } else {
            return target + text;
        }
    }

    /**
     * 若 target + text 的长度不大于于 length 则追加,否则 返回 text
     *
     * @param target nullable
     * @param text   not null
     */
    public static String appendOrSet(String target, String text, int length) {
        if (target != null && (target.length() + text.length()) <= length) {
            return target + text;
        } else {
            return text;
        }
    }

    /**
     * @return text 为 null 则返回空串,否则返回 text
     */
    public static String nullAsEmpty(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }
}
