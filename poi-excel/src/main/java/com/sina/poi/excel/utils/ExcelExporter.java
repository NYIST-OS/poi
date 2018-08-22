package com.sina.poi.excel.utils;

import com.sina.poi.excel.annotation.ExcelMeta;
import com.sina.poi.excel.annotation.HeaderName;
import com.sina.poi.excel.dto.AbstractDto;
import com.sina.poi.excel.enums.CodeEnum;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName ExcelExporter
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:24
 * @Version 1.0
 */
public class ExcelExporter implements AutoCloseable {

    public static final String AUTO_SIZE_COLUMN = "jelly.checkingBusiness.excel.exporter.autoSizeColumn";

    public static final String PAGE_ROW_COUNT = "jelly.checkingBusiness.excel.exporter.rowCount";

    private static final Logger LOG = LoggerFactory.getLogger(ExcelExporter.class);


    private final Workbook workbook = new XSSFWorkbook();

    private final Sheet sheet;

    private final Class<? extends AbstractDto> dtoClass;

    private final boolean autoSizeColumn;

    private final List<Header> headerList;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\$\\{date\\}");


    class Header {

        final String name;

        final int index;

        final String propName;

        Header(String name, int index, String propName) {
            this.name = name;
            this.index = index;
            this.propName = propName;
        }
    }


    public ExcelExporter(Class<? extends AbstractDto> dtoClass) throws Exception {
        this(dtoClass, false);
    }

    public ExcelExporter(Class<? extends AbstractDto> dtoClass, boolean autoSizeColumn) throws Exception {
        this.dtoClass = dtoClass;
        sheet = createSheet();
        headerList = initHeader();
        this.autoSizeColumn = autoSizeColumn;
    }


    private Sheet createSheet() {
        ExcelMeta meta = AnnotationUtils.findAnnotation(dtoClass, ExcelMeta.class);
        String name;
        if (meta == null) {
            name = LocalDate.now().format(TimeUtils.DATE_FORMATTER);
        } else {
            name = meta.sheet();
            if (!StringUtils.hasText(name)) {
                name = DATE_PATTERN.matcher(meta.name()).replaceAll("");
            }
        }
        return workbook.createSheet(name);

    }

    private List<Header> initHeader() {
        final Set<Integer> indexSet = new HashSet<>();
        List<Header> list = new ArrayList<>();
        ReflectionUtils.doWithFields(dtoClass, fc -> {
            HeaderName headerName = AnnotationUtils.findAnnotation(fc, HeaderName.class);
            if (headerName == null) {
                return;
            }

            if (indexSet.contains(headerName.index())) {
                throw new RuntimeException(String.format("%s.index() duplicate", HeaderName.class.getName()));
            }
            list.add(
                    new Header(headerName.value(), headerName.index(), fc.getName())
            );
            indexSet.add(headerName.index());
        });

        return Collections.unmodifiableList(list);
    }

    private void writeHeader() {
        Row row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
        }
        Cell cell;
        for (Header header : headerList) {
            cell = row.createCell(header.index);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(header.name);
            if (autoSizeColumn) {
                sheet.autoSizeColumn(header.index);
            }
        }
    }


    /**
     * @param list   数据
     * @param offset 从 0 开始,即
     */
    public ExcelExporter append(final List<? extends AbstractDto> list, final int offset) throws IOException {

        int rowIndex = offset + 1;
        BeanWrapper wrapper;
        Object value;
        final Sheet sheet = this.sheet;
        Row row;
        Cell cell;
        List<Header> headerList = this.headerList;

        for (AbstractDto dto : list) {
            wrapper = new BeanWrapperImpl(dto);
            row = sheet.createRow(rowIndex);

            for (Header header : headerList) {
                cell = row.createCell(header.index);
                cell.setCellType(CellType.STRING);

                value = wrapper.getPropertyValue(header.propName);
                cell.setCellValue(convertToString(value));
            }
            rowIndex++;
        }
        return this;
    }


    public HttpEntity<Resource> exportToHttpEntity() throws Exception {
        writeHeader();
        FileSystemResource resource = new FileSystemResource(getExportExcelFile());
        try (OutputStream out = resource.getOutputStream(); Workbook w = workbook) {
            w.write(out);
            return new HttpEntity<>(resource, createHeader());
        }
    }


    private HttpHeaders createHeader() {
        String excelName, dateStr;
        ExcelMeta meta = AnnotationUtils.findAnnotation(dtoClass, ExcelMeta.class);
        dateStr = LocalDate.now().format(TimeUtils.DATE_FORMATTER);
        if (meta == null) {
            excelName = dateStr;
        } else {
            excelName = DATE_PATTERN.matcher(meta.name()).replaceAll(dateStr);
        }

        ZonedDateTime now = ZonedDateTime.now(TimeUtils.ZONE8);
        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .creationDate(now)
                .modificationDate(now)
                .filename(excelName + "." + XSSFWorkbookType.XLSX.getExtension()
                        , StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.valueOf("application/vnd.ms-excel"));
        return headers;
    }

    private File getExportExcelFile() throws IOException {
        String fileName = UUID.randomUUID().toString() + "." + XSSFWorkbookType.XLSX.getExtension();
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        if (!file.exists() && file.createNewFile()) {
            LOG.info("创建临时文件:{}", file.getAbsolutePath());
        }
        return file;
    }


    private String convertToString(Object value) {
        String text;
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal) {
            BigDecimal decimal = ((BigDecimal) value);
            if (decimal.scale() < 2) {
                decimal = decimal.setScale(2, RoundingMode.HALF_UP);
            }
            text = decimal.toPlainString();
        } else if (value instanceof LocalDate) {
            text = ((LocalDate) value).format(TimeUtils.DATE_FORMATTER);
        } else if (value instanceof LocalDateTime) {
            text = ((LocalDateTime) value).format(TimeUtils.DATETIME_FORMATTER);
        } else if (value instanceof CodeEnum) {
            text = ((CodeEnum) value).display();
        } else {
            text = value.toString();
        }
        return text;
    }

    @Override
    public void close() throws Exception {

    }

    public Workbook getWorkbook() {
        writeHeader();
        createHeader();
        return workbook;
    }
}
