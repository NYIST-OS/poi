package com.sina.poi.excel.utils;

import com.sina.poi.excel.annotation.HeaderName;
import com.sina.poi.excel.dto.AbstractDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName ExcelExporter
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 14:04
 * @Version 1.0
 */
public class ImportExcel<T extends AbstractDto> {

    private final List<T> resultList;

    private final Class<T> dtoClass;

    private final List<String> propNameList;

    public ImportExcel(Class<T> clazz) {
        this.dtoClass = clazz;
        this.resultList = new ArrayList<>();
        this.propNameList = initPropName();
    }

    private List<String> initPropName() {
        List<String> list = new ArrayList<>();
        ReflectionUtils.doWithFields(dtoClass, fc -> {
            HeaderName headerName = AnnotationUtils.findAnnotation(fc, HeaderName.class);
            list.add(fc.getName()
            );
        });
        return Collections.unmodifiableList(list);
    }

    public List<T> convert(final MultipartFile file) throws Exception {
        if (file == null) return resultList;

        InputStream in = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(in);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) return resultList;

        Row row = sheet.getRow(1);
        if (row == null) return resultList;

        List<String> sort = sort(row);
        if (CollectionUtils.isEmpty(sort)) return resultList;

        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row textRow = sheet.getRow(i);
            T t = dtoClass.newInstance();

            for (int j = 0; j <= sort.size(); j++) {
                Cell cell = textRow.getCell(j);
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();
                Field field = dtoClass.getDeclaredField(sort.get(j));
                field.setAccessible(true);
                field.set(t, value);
            }
            resultList.add(t);
        }
        return resultList;
    }

    private List<String> sort(Row row) {
        List<String> sortList = new ArrayList<>();
        for (int i = 1; ; i++) {
            String value = row.getCell(i).getStringCellValue();
            if (StringUtils.isEmpty(value)) {
                break;
            }
            for (String str : propNameList) {
                if (str.equals(value)) {
                    sortList.add(str);
                }
            }
        }
        return sortList;
    }
}
