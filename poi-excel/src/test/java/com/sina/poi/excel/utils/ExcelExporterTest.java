package com.sina.poi.excel.utils;

import com.sina.poi.excel.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.util.FileCopyUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporterTest {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws Exception {

        List<UserDto> list = new ArrayList<>();
        UserDto userDto;
        for (int i = 0; i < 50; i++) {
            userDto = new UserDto();
            userDto.setName("sina" + i);
            userDto.setEmail(i + "@staff.sina.com.cn");
            userDto.setPhone(Integer.toString(i));
            list.add(userDto);
        }

        ExcelExporter excelExporter = new ExcelExporter(UserDto.class);
        excelExporter.append(list, 0);
        HttpEntity<Resource> entity = excelExporter.exportToHttpEntity();
        Resource resource = entity.getBody();
        File file = resource.getFile();
        System.out.println(file.getAbsolutePath());
        LOG.info("file path:{}", file.getAbsolutePath());
        FileCopyUtils.copy(file, ResourceUtils.getOrCreateFile("classpath:UserDto.xlsx", getClass()));
        LOG.info("list:{}", list);
    }
}