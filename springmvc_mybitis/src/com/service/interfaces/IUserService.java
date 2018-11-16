package com.service.interfaces;

import java.util.Map;

public interface IUserService {

	public Map<String, Object> getUserInfo(Map<String, Object> params) throws Exception;
	
	
	public void saveCategoryAndPropertyRecord() throws Exception;
}
