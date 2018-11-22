package com.model;

import java.util.Date;

public class PropertyName {
    private Integer id;

    private String name;

    private Integer isKeyProperty;

    private Integer isSellProperty;

    private Integer isGoodsProperty;

    private Integer inputType;

    private Integer modifyType;

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

    public Integer getIsKeyProperty() {
        return isKeyProperty;
    }

    public void setIsKeyProperty(Integer isKeyProperty) {
        this.isKeyProperty = isKeyProperty;
    }

    public Integer getIsSellProperty() {
        return isSellProperty;
    }

    public void setIsSellProperty(Integer isSellProperty) {
        this.isSellProperty = isSellProperty;
    }

    public Integer getIsGoodsProperty() {
        return isGoodsProperty;
    }

    public void setIsGoodsProperty(Integer isGoodsProperty) {
        this.isGoodsProperty = isGoodsProperty;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getModifyType() {
        return modifyType;
    }

    public void setModifyType(Integer modifyType) {
        this.modifyType = modifyType;
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