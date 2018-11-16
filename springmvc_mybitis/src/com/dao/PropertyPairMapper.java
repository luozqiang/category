package com.dao;

import com.model.PropertyPair;
import org.apache.ibatis.annotations.Param;

public interface PropertyPairMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PropertyPair record);

    int insertSelective(PropertyPair record);

    PropertyPair selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PropertyPair record);

    int updateByPrimaryKey(PropertyPair record);

    PropertyPair selectByCondition(@Param("property_name_id") Integer property_name_id, @Param("property_value_id") Integer property_value_id);

    int selectMaxOrderByName(Integer property_name_id);
}