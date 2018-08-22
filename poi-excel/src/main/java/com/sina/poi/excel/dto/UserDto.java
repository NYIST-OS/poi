package com.sina.poi.excel.dto;

import com.sina.poi.excel.annotation.ExcelMeta;
import com.sina.poi.excel.annotation.HeaderName;

/**
 * @ClassName UserDto
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:47
 * @Version 1.0
 */
@ExcelMeta(name = "用户列表${date}")
public class UserDto extends AbstractDto {


    @HeaderName(value = "姓名", index = 0)
    private String name;

    @HeaderName(value = "邮箱", index = 1)
    private String email;

    @HeaderName(value = "手机号", index = 2)
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
