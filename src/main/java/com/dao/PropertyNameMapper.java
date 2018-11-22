package com.dao;

import com.model.PropertyName;

public interface PropertyNameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PropertyName record);

    int insertSelective(PropertyName record);

    PropertyName selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PropertyName record);

    int updateByPrimaryKey(PropertyName record);

    PropertyName selectPropertyNameByName(String name);
}