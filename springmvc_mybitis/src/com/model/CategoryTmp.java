package com.model;

public class CategoryTmp {
    private Integer sourceId;

    private String firstCategory;

    private String secondCategory;

    private String thirdCategory;

    private String fourthCategory;

    private String property;

    private Integer propertyType;

    private Integer secondCategoryId;

    private Integer thirdCategoryId;

    private String secondCategoryPath;

    private String thirdCategoryPath;

    private Integer leafCategoryId;

    private String secondCategoryFullId;

    private Integer skuType;

    public Integer getSkuType() {
        return skuType;
    }

    public void setSkuType(Integer skuType) {
        this.skuType = skuType;
    }


    public String getSecondCategoryFullId() {
        return secondCategoryFullId;
    }

    public void setSecondCategoryFullId(String secondCategoryFullId) {
        this.secondCategoryFullId = secondCategoryFullId;
    }

    public String getThirdCategoryFullId() {
        return thirdCategoryFullId;
    }

    public void setThirdCategoryFullId(String thirdCategoryFullId) {
        this.thirdCategoryFullId = thirdCategoryFullId;
    }

    private String thirdCategoryFullId;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(String firstCategory) {
        this.firstCategory = firstCategory == null ? null : firstCategory.trim();
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory == null ? null : secondCategory.trim();
    }

    public String getThirdCategory() {
        return thirdCategory;
    }

    public void setThirdCategory(String thirdCategory) {
        this.thirdCategory = thirdCategory == null ? null : thirdCategory.trim();
    }

    public String getFourthCategory() {
        return fourthCategory;
    }

    public void setFourthCategory(String fourthCategory) {
        this.fourthCategory = fourthCategory == null ? null : fourthCategory.trim();
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property == null ? null : property.trim();
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Integer secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public Integer getThirdCategoryId() {
        return thirdCategoryId;
    }

    public void setThirdCategoryId(Integer thirdCategoryId) {
        this.thirdCategoryId = thirdCategoryId;
    }

    public String getSecondCategoryPath() {
        return secondCategoryPath;
    }

    public void setSecondCategoryPath(String secondCategoryPath) {
        this.secondCategoryPath = secondCategoryPath == null ? null : secondCategoryPath.trim();
    }

    public String getThirdCategoryPath() {
        return thirdCategoryPath;
    }

    public void setThirdCategoryPath(String thirdCategoryPath) {
        this.thirdCategoryPath = thirdCategoryPath == null ? null : thirdCategoryPath.trim();
    }

    public Integer getLeafCategoryId() {
        return leafCategoryId;
    }

    public void setLeafCategoryId(Integer leafCategoryId) {
        this.leafCategoryId = leafCategoryId;
    }
    
    public String getKeyId(){
    	return this.firstCategory+"_"+this.secondCategory+"_"+this.thirdCategory+"_"+this.fourthCategory;
    }
}