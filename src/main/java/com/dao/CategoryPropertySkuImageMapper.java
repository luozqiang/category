package com.dao;

import com.model.CategoryPropertySkuImage;

public interface CategoryPropertySkuImageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryPropertySkuImage record);

    int insertSelective(CategoryPropertySkuImage record);

    CategoryPropertySkuImage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CategoryPropertySkuImage record);

    int updateByPrimaryKey(CategoryPropertySkuImage record);
}