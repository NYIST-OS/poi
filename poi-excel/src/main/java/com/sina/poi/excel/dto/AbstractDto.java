package com.sina.poi.excel.dto;

import com.sina.poi.excel.utils.JsonUtils;
import org.springframework.core.env.Environment;

import java.io.Serializable;

/**
 * @ClassName AbstractDto
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:25
 * @Version 1.0
 */
public abstract class AbstractDto implements Serializable {

    private static final long serialVersionUID = 7518348683227201454L;
    /**
     * 由上下文启动时设置
     */
    protected transient static Environment env;

    /**
     * 如果子类与其属性互相引用,则必须重写此方法否则会死循环,
     * 造成内存溢出
     */
    @Override
    public String toString() {
        return null;
        //return JsonUtils.writeToJson( this, true );
    }

    /**
     * 由上下文启动时设置
     */
    public static void setEnv(Environment env) {
        AbstractDto.env = env;
    }

}
