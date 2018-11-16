package com.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PropertyModTypeEnum {

	    MODIFY_ALLOWED("可修改",1),
	    MODIFY_BANNED("不可修改",2);

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

	    PropertyModTypeEnum(String desc, int val)
	    {
	        this.desc = desc;
	        this.val = val;
	    }

	    private static final Map<String,PropertyModTypeEnum> map = new ConcurrentHashMap<>();

	    static {
	        for (PropertyModTypeEnum propertyModTypeEnum: PropertyModTypeEnum.values()){
	            map.put(propertyModTypeEnum.getDesc(),propertyModTypeEnum);
	        }
	    }

	    public static PropertyModTypeEnum getEnumByName(String name){
	        return map.get(name);
	    }
}
