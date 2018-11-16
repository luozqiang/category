package com.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.model.Student;

public class FunctionX {

	public static void main(String[] args) {
        //1.分组计数
        List<Student> list1= Arrays.asList(
                new Student(1,"one","zhao"),new Student(2,"one","qian"),new Student(3,"two","sun"),new Student(4,"one","zhao"));
        //1.1根据某个属性分组计数
//        Map<String,Long> result1=list1.stream().collect(Collectors.groupingBy(Student::getGroupId,Collectors.counting()));
        
        Map<String,Long> result1=list1.stream().collect(Collectors.groupingBy(Student::getGroupId,Collectors.counting()));

        
        //list过滤
        List<Student> listFilter = list1.stream().filter(s -> "zhao".equals(s.getName())).collect(Collectors.toList());
        System.out.println("listFilter:"+listFilter.size());
        //打印出有问题的类目及相应的大小
        Map<String,Long> result11=Maps.filterValues(result1, r->r>2);
        if(null!=result11 && !result11.isEmpty()){
        	result11.forEach((k,v)->{
        		System.out.println(k+":"+v);
        	});
        }
        
        
        Map<String, List<Student>> groupBy = list1.stream().collect(Collectors.groupingBy(Student::getGroupId));

        
        
        System.out.println(result1);
        System.out.println(result11);

        System.out.println(groupBy);
        //1.2根据整个实体对象分组计数,当其为String时常使用
        Map<Student,Long> result2=list1.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println(result2);
        //1.3根据分组的key值对结果进行排序、放进另一个map中并输出
        Map<String,Long> xMap=new HashMap<>();
        result1.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByKey().reversed()) //reversed不生效
                .forEachOrdered(x->xMap.put(x.getKey(),x.getValue()));
        System.out.println(xMap);

        //2.分组，并统计其中一个属性值得sum或者avg:id总和
        Map<String,Integer> result3=list1.stream().collect(
                Collectors.groupingBy(Student::getGroupId,Collectors.summingInt(Student::getId))
        );
        System.out.println(result3);

    }
	
	
}
