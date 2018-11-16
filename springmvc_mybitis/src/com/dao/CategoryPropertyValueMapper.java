package com.dao;

import com.model.CategoryPropertyValue;
import org.apache.ibatis.annotations.Param;

public interface CategoryPropertyValueMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryPropertyValue record);

    int insertSelective(CategoryPropertyValue record);

    CategoryPropertyValue selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CategoryPropertyValue record);

    int updateByPrimaryKey(CategoryPropertyValue record);

    CategoryPropertyValue selectByCondition(@Param("category_property_name_id") Integer category_property_name_id, @Param("property_pair_id") Integer property_pair_id);
}