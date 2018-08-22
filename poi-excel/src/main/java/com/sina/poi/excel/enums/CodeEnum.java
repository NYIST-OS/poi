package com.sina.poi.excel.enums;

/**
 * @ClassName CodeEnum
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:29
 * @Version 1.0
 */
public interface CodeEnum {

    /**
     * @return 用于持久化到数据库中的 code
     */
    int code();

    /**
     * 枚举的 name
     */
    String name();

    /**
     * @return 用于展示到前端的名称
     */
    String display();

    default int compare(CodeEnum codeEnum){
        return compare( this,codeEnum );
    }

    static int compare(CodeEnum codeEnum1,CodeEnum codeEnum2){
        return codeEnum1.code() - codeEnum2.code();
    }

}