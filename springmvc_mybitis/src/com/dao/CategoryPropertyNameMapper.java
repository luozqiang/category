package com.dao;

import com.model.CategoryPropertyName;
import org.apache.ibatis.annotations.Param;

public interface CategoryPropertyNameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryPropertyName record);

    int insertSelective(CategoryPropertyName record);

    CategoryPropertyName selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CategoryPropertyName record);

    int updateByPrimaryKey(CategoryPropertyName record);

    CategoryPropertyName selectByUniqueKey(@Param("category_id")Integer category_id, @Param("property_name_id")Integer property_name_id);

    int selectMaxOrderByCategory(Integer category_id);
}