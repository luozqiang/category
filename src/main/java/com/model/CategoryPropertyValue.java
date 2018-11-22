package com.model;

import java.util.Date;

public class CategoryPropertyValue {
    private Integer id;

    private Integer categoryPropertyNameId;

    private Integer propertyPairId;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryPropertyNameId() {
        return categoryPropertyNameId;
    }

    public void setCategoryPropertyNameId(Integer categoryPropertyNameId) {
        this.categoryPropertyNameId = categoryPropertyNameId;
    }

    public Integer getPropertyPairId() {
        return propertyPairId;
    }

    public void setPropertyPairId(Integer propertyPairId) {
        this.propertyPairId = propertyPairId;
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