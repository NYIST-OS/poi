package com.sina.poi.excel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName ResourceUtils
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 14:20
 * @Version 1.0
 */
public abstract class ResourceUtils extends org.springframework.util.ResourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceUtils.class);


    /**
     * 提取 resourcePattern 指定下的文件的文本内容
     *
     * @param resourcePattern classpath: 为前缀
     * @param contextClass    调用此方法的类(也可以是其它类),用于获取 {@link ClassLoader}
     * @return 文件内容
     */
    public static String extractFileText(String resourcePattern, Class<?> contextClass) throws RuntimeException {


        try (FileInputStream in = new FileInputStream(getFile(resourcePattern, contextClass))) {

            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File getFile(String resourcePattern, Class<?> contextClass) throws RuntimeException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                contextClass.getClassLoader());
        try {
            Resource[] resources = resolver.getResources(resourcePattern);
            return resources[0].getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static File getOrCreateFile(String resourcePattern, Class<?> contextClass) throws IOException, URISyntaxException {
        File classpath = getFile(CLASSPATH_URL_PREFIX);
        File file;
        if (!CLASSPATH_URL_PREFIX.equals(resourcePattern) && resourcePattern.startsWith(CLASSPATH_URL_PREFIX)) {
            file = new File(classpath, resourcePattern.substring(CLASSPATH_URL_PREFIX.length()));
        } else if (resourcePattern.startsWith(FILE_URL_PREFIX)) {
            file = new File(resourcePattern.substring(FILE_URL_PREFIX.length()));
        } else {
            file = new File(toURI(resourcePattern));
        }
        FileUtils.createIfNotExists(file);
        return file;
    }
}
