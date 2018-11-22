package com.dao;

import com.model.PropertyTmp;

public interface PropertyTmpMapper {
    int deleteByPrimaryKey(Integer sourceId);

    int insert(PropertyTmp record);

    int insertSelective(PropertyTmp record);

    PropertyTmp selectByPrimaryKey(Integer sourceId);

    int updateByPrimaryKeySelective(PropertyTmp record);

    int updateByPrimaryKey(PropertyTmp record);
}