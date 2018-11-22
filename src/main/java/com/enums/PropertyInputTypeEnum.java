package com.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PropertyInputTypeEnum {

	SINGLE_SELECT_NO_DEFINE("单选不允许自定义",1),
    SINGLE_SELECT_ALLOW_DEFINE("单选允许自定义",2),
    MUTIL_SELECT_NO_DEFINE("多选不允许自定义",3),
    MUTIL_SELECT_ALLOW_DEFINE("多选允许自定义",4),
    INPUT_TEXT("文本输入框",5),
    DATE_PICKER("日期选择器",6),
    TIME_PICKER("时间选择器",7);

    private String desc;
    private int val;

    public String getDesc()
    {
        return desc;
    }

    public int getVal()
    {
        return val;
    }

    PropertyInputTypeEnum(String desc, int val)
    {
        this.desc = desc;
        this.val = val;
    }

    private static final Map<String, PropertyInputTypeEnum> map = new ConcurrentHashMap<>();

    static
    {
        for (PropertyInputTypeEnum propertyInputTypeEnum : PropertyInputTypeEnum.values())
        {
            map.put(propertyInputTypeEnum.getDesc(), propertyInputTypeEnum);
        }
    }

    public static PropertyInputTypeEnum getEnumByName(String name)
    {
        return map.get(name);
    }
    
    //输入类型的值是否必填
    public static Boolean requiredValueFlag(PropertyInputTypeEnum property)
    {
    	if(null==property) return false; 
    	String name = property.getDesc();
    	if(name.equals(INPUT_TEXT.getDesc()) || name.equals(DATE_PICKER.getDesc()) || name.equals(TIME_PICKER.getDesc())){
    		return false;
    	}
        return true;
    }
}
