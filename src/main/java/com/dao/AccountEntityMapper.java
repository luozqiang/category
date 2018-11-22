package com.dao;

import com.model.AccountEntity;
import com.model.AccountEntityKey;

public interface AccountEntityMapper {
    int deleteByPrimaryKey(AccountEntityKey key);

    int insert(AccountEntity record);

    int insertSelective(AccountEntity record);

    AccountEntity selectByPrimaryKey(AccountEntityKey key);

    int updateByPrimaryKeySelective(AccountEntity record);

    int updateByPrimaryKey(AccountEntity record);
}