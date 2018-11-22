package com.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PropertyTypeEnum {

	KEY_PROPERTY("关键属性",1),
    SELL_PROPERTY("销售属性",2),
    GOODS_PROPERTY("商品属性",3);

    private String desc;
    private int val;

    PropertyTypeEnum(String desc, int val)
    {
        this.desc = desc;
        this.val = val;
    }

    public String getDesc()
    {
        return desc;
    }

    public int getVal()
    {
        return val;
    }

    private static final Map<String, PropertyTypeEnum> map = new ConcurrentHashMap<>();

    static
    {
        for (PropertyTypeEnum propertyTypeEnum : PropertyTypeEnum.values())
        {
            map.put(propertyTypeEnum.getDesc(), propertyTypeEnum);
        }
    }

    public static PropertyTypeEnum getEnumByName(String name)
    {
        return map.get(name);
    }

    public static String getNameByValue(int value)
    {
        for (PropertyTypeEnum propertyTypeEnum : PropertyTypeEnum.values())
        {
            if(propertyTypeEnum.getVal()==value){
                return propertyTypeEnum.getDesc();
            }
        }
        return null;
    }
}
