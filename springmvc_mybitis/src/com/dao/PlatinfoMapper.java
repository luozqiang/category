package com.dao;

import com.model.Platinfo;

public interface PlatinfoMapper {
    int deleteByPrimaryKey(String platNo);

    int insert(Platinfo record);

    int insertSelective(Platinfo record);

    Platinfo selectByPrimaryKey(String platNo);

    int updateByPrimaryKeySelective(Platinfo record);

    int updateByPrimaryKey(Platinfo record);
}