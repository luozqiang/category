package com.dao;

import com.model.PropertyPairTmp;

public interface PropertyPairTmpMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PropertyPairTmp record);

    int insertSelective(PropertyPairTmp record);

    PropertyPairTmp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PropertyPairTmp record);

    int updateByPrimaryKey(PropertyPairTmp record);
}