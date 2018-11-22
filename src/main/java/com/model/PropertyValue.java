package com.model;

import java.util.Date;

public class PropertyValue {
    private Integer id;

    private String name;

    private String nameUpperMd5;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNameUpperMd5() {
        return nameUpperMd5;
    }

    public void setNameUpperMd5(String nameUpperMd5) {
        this.nameUpperMd5 = nameUpperMd5 == null ? null : nameUpperMd5.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}