package com.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PropertyIsRequiredEnum {

	    //是否必填, 0否，1是
	    REQUIRED_YES("必填",1),
	    REQUIRED_NO("非必填",0);

	    private String desc;
	    private Integer val;

	    public String getDesc()
	    {
	        return desc;
	    }

	    public Integer getVal()
	    {
	        return val;
	    }

	    PropertyIsRequiredEnum(String desc, int val)
	    {
	        this.desc = desc;
	        this.val = val;
	    }

	    private static final Map<String,PropertyIsRequiredEnum> map = new ConcurrentHashMap<>();

	    static {
	        for (PropertyIsRequiredEnum propertyModTypeEnum: PropertyIsRequiredEnum.values()){
	            map.put(propertyModTypeEnum.getDesc(),propertyModTypeEnum);
	        }
	    }

	    public static PropertyIsRequiredEnum getEnumByName(String name){
	        return map.get(name);
	    }
}
