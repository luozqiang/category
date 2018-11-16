package com.dao;

import com.model.Notify;

public interface NotifyMapper {
    int deleteByPrimaryKey(String jobName);

    int insert(Notify record);

    int insertSelective(Notify record);

    Notify selectByPrimaryKey(String jobName);

    int updateByPrimaryKeySelective(Notify record);

    int updateByPrimaryKey(Notify record);
}