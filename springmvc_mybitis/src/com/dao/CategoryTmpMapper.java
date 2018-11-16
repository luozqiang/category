package com.dao;

import com.model.CategoryTmp;

public interface CategoryTmpMapper {
    int deleteByPrimaryKey(Integer sourceId);

    int insert(CategoryTmp record);

    int insertSelective(CategoryTmp record);

    CategoryTmp selectByPrimaryKey(Integer sourceId);

    int updateByPrimaryKeySelective(CategoryTmp record);

    int updateByPrimaryKey(CategoryTmp record);
}