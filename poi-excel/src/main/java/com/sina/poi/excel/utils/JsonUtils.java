package com.sina.poi.excel.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sina.poi.excel.enums.CodeEnum;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static com.sina.poi.excel.utils.TimeUtils.DATETIME_FORMATTER;
import static com.sina.poi.excel.utils.TimeUtils.DATE_FORMATTER;
import static com.sina.poi.excel.utils.TimeUtils.TIME_FORMATTER;

/**
 * @ClassName JsonUtils
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:48
 * @Version 1.0
 */
public abstract class JsonUtils {

    /**
     * 公用的 ObjectMapper 请不要在运行中修改 配置,若要修改配置请使用
     */
    private static final ObjectMapper OBJECT_MAPPER;

    public static final LocalDateTimeDeserializer LOCALDATETIME_DESERIALIZER = new LocalDateTimeDeserializer(DATETIME_FORMATTER);

    public static final LocalDateTimeSerializer LOCALDATETIME_SERIALIZER = new LocalDateTimeSerializer(DATETIME_FORMATTER);

    public static final LocalDateDeserializer LOCALDATE_DESERIALIZER = new LocalDateDeserializer(DATE_FORMATTER);

    public static final LocalDateSerializer LOCALDATE_SERIALIZER = new LocalDateSerializer(DATE_FORMATTER);

    public static final LocalTimeDeserializer LOCAL_TIME_DESERIALIZER = new LocalTimeDeserializer(TIME_FORMATTER);

    public static final LocalDateTimeSerializer LOCAL_TIME_SERIALIZER = new LocalDateTimeSerializer(TIME_FORMATTER);


    /**
     * 数字序列化器, 这个序列化器会将 {@link BigDecimal} {@link BigInteger} {@link Long} 输出为字符串
     */
    public static final NumberSerializer NUMBER_SERIALIZER = new NumberSerializer(Number.class) {
        private static final long serialVersionUID = 328989124431014858L;

        @Override
        public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
            // should mostly come in as one of these two:
            if (value instanceof BigDecimal) {
                BigDecimal amount = (BigDecimal) value;
                if (amount.scale() == 0) {
                    amount = amount.setScale(2, RoundingMode.HALF_EVEN);
                }
                g.writeString(amount.toPlainString());
            } else if (value instanceof BigInteger) {
                g.writeString(value.toString());
            } else if (value instanceof Long) {
                g.writeString(value.toString());
            } else if (value instanceof Double) {
                g.writeNumber(value.doubleValue());
            } else if (value instanceof Float) {
                g.writeNumber(value.floatValue());
            } else if (value instanceof Integer || value instanceof Byte || value instanceof Short) {
                g.writeNumber(value.intValue()); // doesn't need to be cast to smaller numbers
            } else {
                g.writeNumber(value.toString());
            }
        }
    };


    private static class ResultCodeDeserializer extends StdScalarDeserializer<CodeEnum>
            implements ContextualDeserializer {

        private static final long serialVersionUID = -2344343670912720214L;

        public static final ResultCodeDeserializer CODE_DESERIALIZER = new ResultCodeDeserializer(true);

        public static final ResultCodeDeserializer NAME_DESERIALIZER = new ResultCodeDeserializer(false);

        private static final String CODE = "code";


        private final boolean useNum;

        public ResultCodeDeserializer(boolean useNum) {
            super(CodeEnum.class);
            this.useNum = useNum;
        }

        @Override
        public CodeEnum deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            CodeEnum code = null;
            if (useNum) {
                //code = CodeEnum.resolve( p.getValueAsInt() );
            } else {
                //code = CodeEnum.valueOf( p.getValueAsString() );
            }
            return code;
        }


        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            JsonFormat.Value format = findFormatOverrides(ctxt,
                    property, handledType());
            ResultCodeDeserializer deserializer;
            if (isUseName(format)) {
                deserializer = NAME_DESERIALIZER;
            } else {
                deserializer = CODE_DESERIALIZER;
            }
            return deserializer;
        }

        private boolean isUseName(JsonFormat.Value format) {
            return !format.getShape().isNumeric();
        }
    }

    public static class CodeEnumSerializer extends StdScalarSerializer<CodeEnum> implements ContextualSerializer {

        private static final long serialVersionUID = 2978032273225655788L;

        public static final CodeEnumSerializer CODE_SERIALIZER = new CodeEnumSerializer(false);

        public static final CodeEnumSerializer NAME_SERIALIZER = new CodeEnumSerializer(true);


        private final boolean useName;


        public CodeEnumSerializer(boolean useName) {
            super(CodeEnum.class, true);
            this.useName = useName;
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
            JsonFormat.Value format = findFormatOverrides(prov,
                    property, handledType());
            CodeEnumSerializer serializer;
            if (isUseName(format)) {
                serializer = NAME_SERIALIZER;
            } else {
                serializer = CODE_SERIALIZER;
            }
            return serializer;
        }

        @Override
        public void serialize(CodeEnum value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (useName) {
                if (value instanceof Enum) {
                    gen.writeString(value.name());
                } else {
                    throw new IOException(String.format("%s isn't Enum", value.getClass()));
                }

            } else {
                gen.writeNumber(value.code());
            }
        }

        private boolean isUseName(JsonFormat.Value format) {
            return !format.getShape().isNumeric();
        }
    }

    static {
        OBJECT_MAPPER = createDefaultObjectMapper();
    }

    private static Module createSinaCustomModule(String moduleName) {
        // 自定义模块,重写部分序列化器
        return new SimpleModule(moduleName == null ? "sinaFinanceCustomModule" : moduleName)
                .addSerializer(Number.class, NUMBER_SERIALIZER)
                .addSerializer(CodeEnum.class, CodeEnumSerializer.NAME_SERIALIZER)
                // .addDeserializer( ResultCode.class, ResultCodeDeserializer.CODE_DESERIALIZER)
                ;
    }

    /**
     * 添加 java 8 时间特性({@link LocalDateTime}),并指定时区为东八区
     */

    public static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(dateFormat);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\n"));
        mapper.setDefaultPrettyPrinter(prettyPrinter);

        mapper.registerModule(createSinaCustomModule(null));
        mapper.registerModule(createJavaTimeModule());
        return mapper;
    }

    private static Module createJavaTimeModule() {
        return new JavaTimeModule()
                .addDeserializer(LocalDateTime.class, LOCALDATETIME_DESERIALIZER)
                .addSerializer(LocalDateTime.class, LOCALDATETIME_SERIALIZER)

                .addDeserializer(LocalDate.class, LOCALDATE_DESERIALIZER)
                .addSerializer(LocalDate.class, LOCALDATE_SERIALIZER)
                ;
    }


    /**
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readFromJson(String json, Class<T> clazz) throws RuntimeException {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param in 当读取完成后,将自动关闭
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readFromJson(InputStream in, Class<T> clazz) throws IOException {
        try (InputStream i = in) {
            return OBJECT_MAPPER.readValue(i, clazz);
        }
    }

    public static <T> T readFromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromJson(InputStream in, TypeReference<T> typeReference) throws IOException {
        try (InputStream i = in) {
            return OBJECT_MAPPER.readValue(i, typeReference);
        }
    }

    /**
     * @param reader 当读取完成后,将自动关闭
     * @throws RuntimeException - 当 json 不能转换为目标对象时抛出
     */
    public static <T> T readFromJson(Reader reader, Class<T> clazz) throws IOException {
        try (Reader r = reader) {
            return OBJECT_MAPPER.readValue(r, clazz);
        }
    }


    /**
     * @throws RuntimeException - 当 value 不能转换为 json 时抛出,如 : value 回环
     */
    public static String writeToJson(Object value) throws RuntimeException {
        return writeToJson(value, false);
    }

    /**
     * @throws RuntimeException - 当 value 不能转换为 json 时抛出,如 : value 回环
     */
    public static String writeToJson(Object value, boolean pretty) throws RuntimeException {
        try {
            ObjectWriter objectWriter;
            if (pretty) {
                objectWriter = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
            } else {
                objectWriter = OBJECT_MAPPER.writer();
            }
            return objectWriter.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param writer 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeToJson(Object value, Writer writer) throws IOException {
        writeToJson(value, writer, false);
    }

    /**
     * @param writer 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeToJson(Object value, Writer writer, boolean pretty) throws IOException {
        try (Writer w = writer) {
            JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(w);
            if (pretty) {
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(generator, value);
            } else {
                OBJECT_MAPPER.writeValue(generator, value);
            }
        }
    }

    /**
     * @param out 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeToJson(Object value, OutputStream out) throws IOException {
        writeToJson(value, out, false);
    }

    /**
     * @param out 当写出完成后,将自动关闭
     * @throws IOException - 1.当 value 不能转换为 json 时抛出,如 : value 回环,2. 输出异常
     */
    public static void writeToJson(Object value, OutputStream out, boolean pretty) throws IOException {
        try (OutputStream o = out) {
            JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(o, JsonEncoding.UTF8);
            if (pretty) {
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(generator, value);
            } else {
                OBJECT_MAPPER.writeValue(generator, value);
            }
        }
    }

    public static JsonNode parseFromByte(byte[] content) throws IOException {
        return OBJECT_MAPPER.readTree(content);
    }

    public static JsonNode parseTree(String json) throws IOException {
        return OBJECT_MAPPER.readTree(json);
    }


}
