package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enums.PropertyInputTypeEnum;
import com.enums.PropertyIsRequiredEnum;
import com.enums.PropertyTypeEnum;
import com.google.common.collect.Maps;
import com.model.CategoryTmp;
import com.model.PropertyTmp;

public class ExcelUtil {
	
	private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);
	public static final String CATEGORY_KEY = "category_key";
	public static final String PROPERTY_KEY = "property_key";
	
	
    /**   
     * 读取Excel,直接用workbookFactory,无须判断03或07版本
     * @param response   
     * @param excelPath
     */ 
	public static ArrayList<ArrayList<String>> readExcel(String excelPath)
			throws InvalidFormatException, FileNotFoundException, IOException {
		ArrayList<ArrayList<String>> aal = new ArrayList<ArrayList<String>>();
		org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(excelPath)));
		Sheet sheet = workbook.getSheetAt(0);
		int startRowNum = sheet.getFirstRowNum();
		int endRowNum = sheet.getLastRowNum();
		for (int rowNum = startRowNum + 1; rowNum <= endRowNum; rowNum++) {
			ArrayList<String> al = new ArrayList<String>();
			Row row = sheet.getRow(rowNum);
			if (row == null)
				continue;
			int startCellNum = row.getFirstCellNum();
			int endCellNum = 9;//row.getLastCellNum();
			for (int cellNum = startCellNum; cellNum <= endCellNum; cellNum++) {
				Cell cell = row.getCell(cellNum);
				if (cell == null)
				{
					al.add("");
					continue;
				}
				int type = cell.getCellType();
		        String cellValue = "";  
		        DecimalFormat df = new DecimalFormat("#");  
				switch (type) {
				case Cell.CELL_TYPE_NUMERIC:// 数值、日期类型
					cellValue = df.format(cell.getNumericCellValue()).toString();  
					break;
				case Cell.CELL_TYPE_BLANK:// 空白单元格
					cellValue = "";
					break;
				case Cell.CELL_TYPE_STRING:// 字符类型
					cellValue = cell.getRichStringCellValue().getString().trim();  
					break;
				case Cell.CELL_TYPE_BOOLEAN:// 布尔类型
					cellValue = String.valueOf(cell.getBooleanCellValue()).trim();  
					break;
				case HSSFCell.CELL_TYPE_ERROR: // 故障
					System.err.println("非法字符");// 非法字符;
					break;
		        case XSSFCell.CELL_TYPE_FORMULA:  
		            cellValue = cell.getCellFormula(); 
				default:
//					cellValue = "";
					cellValue = cell.getStringCellValue();
					break;
				}
				al.add(cellValue.trim());
			}
			aal.add(al);
		}
		return aal;
	}
	
    /**   
     * Excel内容解析校验
     */ 
    public static Map<String,Object> getExcelInfo(String excelUrl){
    	if(StringUtils.isBlank(excelUrl)){
    		excelUrl = "/Users/lzq/Documents/456.xlsx";//默认excel地址
    	}
    	try {
			int i = 0;
			List<CategoryTmp> categoryList = new ArrayList<>();
			List<PropertyTmp> propertyList = new ArrayList<>();
			for(ArrayList<String> array : readExcel(excelUrl)){
				i++;
				String category1 = array.get(0);
				String property = array.get(4);
				String inputType = array.get(6);
				String requiredType = array.get(7);
				String propertyType = array.get(8);
				if(StringUtils.isBlank(category1) || StringUtils.isBlank(property)  || StringUtils.isBlank(inputType)
						 || StringUtils.isBlank(requiredType) || StringUtils.isBlank(propertyType)) {
					log.error("第{}行必填信息为空",i);
					return null;
				}
				String propertyValue = array.get(5);
				PropertyInputTypeEnum propertyInputTypeEnum = PropertyInputTypeEnum.getEnumByName(inputType);
				PropertyIsRequiredEnum propertyIsRequiredEnum = PropertyIsRequiredEnum.getEnumByName(requiredType);
				PropertyTypeEnum propertyTypeEnum = PropertyTypeEnum.getEnumByName(propertyType);
				if(null==propertyInputTypeEnum || null==propertyIsRequiredEnum
						|| null==propertyTypeEnum){
					log.error("第{}行输入类型或是否必填或属性类型信息不满足枚举限制",i);
					return null;
				}
				if(PropertyInputTypeEnum.requiredValueFlag(propertyInputTypeEnum)){
					if(StringUtils.isBlank(propertyValue)){
						log.error("第{}行属性值不可为空！！！",i);
						return null;
					}
				}

				String category2 = array.get(1);
				String category3 = array.get(2);
				if(StringUtils.isNotBlank(category3) && StringUtils.isBlank(category2)){
					log.error("第{}行类目信息异常！！！",i);
					return null;
				}
				String category4 = array.get(3);
				if(StringUtils.isNotBlank(category4) && (StringUtils.isBlank(category3) || StringUtils.isBlank(category2))){
					log.error("第{}行类目信息异常！！！",i);
					return null;
				}
				CategoryTmp categoryTmp = new CategoryTmp();
				categoryTmp.setSourceId(i);
				categoryTmp.setFirstCategory(category1);
				categoryTmp.setSecondCategory(category2);
				categoryTmp.setThirdCategory(category3);
				categoryTmp.setFourthCategory(category4);
				categoryTmp.setProperty(property);
				categoryTmp.setPropertyType(PropertyTypeEnum.getEnumByName(propertyType).getVal());

				//校验sku主图属性必须为销售属性
				String skuType = array.get(9);
				if("0".equals(skuType)){
				}else if("1".equals(skuType)){
					if(PropertyTypeEnum.SELL_PROPERTY.getVal()!=categoryTmp.getPropertyType()){
						log.error("第{}行主图属性非销售属性！！！",i);
						return null;
					}
				}else{
					skuType = "0";
//					log.error("第{}行主图属性[{}]非法，必须1或0！！！",i,skuType);
//					return null;
				}
				categoryTmp.setSkuType(Integer.parseInt(skuType));
				categoryList.add(categoryTmp);

				PropertyTmp propertyTmp = new PropertyTmp();
				propertyTmp.setSkuType(categoryTmp.getSkuType());
				propertyTmp.setSourceId(i);
				propertyTmp.setProperty(property);
				propertyTmp.setPropertyValue(propertyValue);
				propertyTmp.setInputType(propertyInputTypeEnum.getVal());
				propertyTmp.setIsRequire(propertyIsRequiredEnum.getVal());
				propertyTmp.setPropertyType(propertyTypeEnum.getVal());
				propertyList.add(propertyTmp);
			}
			//主图
			Map<String,Long> groupSkuResult = categoryList.stream().filter(s -> 1==s.getSkuType()).collect(Collectors.groupingBy(CategoryTmp::getKeyId,Collectors.counting()));
			Map<String,Long> skuProblemMap = Maps.filterValues(groupSkuResult, r->r>1);
			if(null!=skuProblemMap && !skuProblemMap.isEmpty()){
				skuProblemMap.forEach((k,v)->{
					log.error("类目【{}】,sku主图属性数量{}！",k,v);
				});
				return null;
			}

			//校验销售属性
	        Map<String,Long> groupResult = categoryList.stream().filter(s -> 2==s.getPropertyType()).collect(Collectors.groupingBy(CategoryTmp::getKeyId,Collectors.counting()));
	        Map<String,Long> propertyProblemMap = Maps.filterValues(groupResult, r->r>2);
	        if(null!=propertyProblemMap && !propertyProblemMap.isEmpty()){
	        	propertyProblemMap.forEach((k,v)->{
	        		log.error("类目【{}】,销售属性数量{}！",k,v);
	        	});
	        	return null;
	        }
	        Map<String,Object> map = new HashMap<>();
	        map.put(CATEGORY_KEY, categoryList);
	        map.put(PROPERTY_KEY, propertyList);
	        return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }

	/*public static Map<String,Object> getExcelInfo1(String excelUrl){
		if(StringUtils.isBlank(excelUrl)){
			excelUrl = "/Users/lzq/Documents/123.xlsx";//默认excel地址
		}
		try {
			int i = 0;
			List<CategoryTmp> categoryList = new ArrayList<>();
			List<PropertyTmp> propertyList = new ArrayList<>();
			for(ArrayList<String> array : readExcel(excelUrl)){
				i++;
				//一级类目非空
				String category1 = array.get(0);
				String property = array.get(4);
				String inputType = array.get(6);
				String requiredType = array.get(7);
				String propertyType = array.get(8);
				//一级类目 商品属性  输入类型、是否必填 属性类型，必填; 输入类型 是否必填、属性类型 ,满足枚举限制
				if(StringUtils.isBlank(category1) || StringUtils.isBlank(property)  || StringUtils.isBlank(inputType)
						|| StringUtils.isBlank(requiredType) || StringUtils.isBlank(propertyType))
				{
					log.error("第{}行一级类目或商品属性或输入类型或是否必填或属性类型信息为空",i);
					return null;
				}
				String propertyValue = array.get(5);
				PropertyInputTypeEnum propertyInputTypeEnum = PropertyInputTypeEnum.getEnumByName(inputType);
				PropertyIsRequiredEnum propertyIsRequiredEnum = PropertyIsRequiredEnum.getEnumByName(requiredType);
				PropertyTypeEnum propertyTypeEnum = PropertyTypeEnum.getEnumByName(propertyType);
				if(null==propertyInputTypeEnum || null==propertyIsRequiredEnum
						|| null==propertyTypeEnum){
					log.error("第{}行输入类型或是否必填或属性类型信息不满足枚举限制",i);
					return null;
				}
				if(PropertyInputTypeEnum.requiredValueFlag(propertyInputTypeEnum)){
					if(StringUtils.isBlank(propertyValue)){
						log.error("第{}行属性值不可为空！！！",i);
						return null;
					}
				}
				//校验类目中间可能为空情况
				String category2 = array.get(1);
				String category3 = array.get(2);
				if(StringUtils.isNotBlank(category3) && StringUtils.isBlank(category2)){
					log.error("第{}行类目信息异常！！！",i);
					return null;
				}
				String category4 = array.get(3);
				if(StringUtils.isNotBlank(category4) && (StringUtils.isBlank(category3) || StringUtils.isBlank(category2))){
					log.error("第{}行类目信息异常！！！",i);
					return null;
				}

				//类目临时表
				CategoryTmp categoryTmp = new CategoryTmp();
				categoryTmp.setSourceId(i);
				categoryTmp.setFirstCategory(category1);
				categoryTmp.setSecondCategory(category2);
				categoryTmp.setThirdCategory(category3);
				categoryTmp.setFourthCategory(category4);
				categoryTmp.setProperty(property);
				categoryTmp.setPropertyType(PropertyTypeEnum.getEnumByName(propertyType).getVal());
				categoryList.add(categoryTmp);

				//属性临时表
				PropertyTmp propertyTmp = new PropertyTmp();
				propertyTmp.setSourceId(i);
				propertyTmp.setProperty(property);
				propertyTmp.setPropertyValue(propertyValue);
				propertyTmp.setInputType(propertyInputTypeEnum.getVal());
				propertyTmp.setIsRequire(propertyIsRequiredEnum.getVal());
				propertyTmp.setPropertyType(propertyTypeEnum.getVal());
				propertyList.add(propertyTmp);
			}
			//对销售属性进行分组处理
			Map<String,Long> groupResult = categoryList.stream().filter(s -> 2==s.getPropertyType()).collect(Collectors.groupingBy(CategoryTmp::getKeyId,Collectors.counting()));
			//打印出有问题的类目及相应的大小，销售属性大于2
			Map<String,Long> propertyProblemMap = Maps.filterValues(groupResult, r->r>2);
			if(null!=propertyProblemMap && !propertyProblemMap.isEmpty()){
				//输入有问题记录
				propertyProblemMap.forEach((k,v)->{
					log.error("类目【{}】,销售属性数量{}！",k,v);
				});
				return null;
			}
			Map<String,Object> map = new HashMap<>();
			map.put(CATEGORY_KEY, categoryList);
			map.put(PROPERTY_KEY, propertyList);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}*/
	
	public static void main(String[] args) {
		try {
			int i = 0;
			List<CategoryTmp> categoryList = new ArrayList<>();
			List<PropertyTmp> propertyList = new ArrayList<>();
			for(ArrayList<String> array : readExcel("/Users/lzq/Documents/123.xlsx")){
				i++;
				//一级类目非空
				String category1 = array.get(0);
				String property = array.get(4);
				String inputType = array.get(6);
				String requiredType = array.get(7);
				String propertyType = array.get(8);
				//一级类目 商品属性  输入类型、是否必填 属性类型，必填; 输入类型 是否必填、属性类型 ,满足枚举限制
				if(StringUtils.isBlank(category1) || StringUtils.isBlank(property)  || StringUtils.isBlank(inputType)
						 || StringUtils.isBlank(requiredType) || StringUtils.isBlank(propertyType))
				{
					log.error("第{}行一级类目或商品属性或输入类型或是否必填或属性类型信息为空",i);
					break;
				}
				String propertyValue = array.get(5);
				PropertyInputTypeEnum propertyInputTypeEnum = PropertyInputTypeEnum.getEnumByName(inputType);
				PropertyIsRequiredEnum propertyIsRequiredEnum = PropertyIsRequiredEnum.getEnumByName(requiredType);
				PropertyTypeEnum propertyTypeEnum = PropertyTypeEnum.getEnumByName(propertyType);
				if(null==propertyInputTypeEnum || null==propertyIsRequiredEnum
						|| null==propertyTypeEnum){
					log.error("第{}行输入类型或是否必填或属性类型信息不满足枚举限制",i);
					break;
				}
				if(PropertyInputTypeEnum.requiredValueFlag(propertyInputTypeEnum)){
					if(StringUtils.isBlank(propertyValue)){
						log.error("第{}行属性值不可为空！！！",i);
						break;
					}
				}
				//类目临时表
				CategoryTmp categoryTmp = new CategoryTmp();
				categoryTmp.setSourceId(i);
				categoryTmp.setFirstCategory(category1);
				categoryTmp.setSecondCategory(array.get(1));
				categoryTmp.setThirdCategory(array.get(2));
				categoryTmp.setFourthCategory(array.get(3));
				categoryTmp.setProperty(property);
				categoryTmp.setPropertyType(PropertyTypeEnum.getEnumByName(propertyType).getVal());
				categoryList.add(categoryTmp);
				
				//属性临时表
				PropertyTmp propertyTmp = new PropertyTmp();
				propertyTmp.setSourceId(i);
				propertyTmp.setProperty(property);
				propertyTmp.setPropertyValue(propertyValue);
				propertyTmp.setInputType(propertyInputTypeEnum.getVal());
				propertyTmp.setIsRequire(propertyIsRequiredEnum.getVal());
				propertyTmp.setPropertyType(propertyTypeEnum.getVal());
				propertyList.add(propertyTmp);
				
//				System.out.println(array);
			}
	        //对销售属性进行分组处理
	        Map<String,Long> groupResult = categoryList.stream().filter(s -> 2==s.getPropertyType()).collect(Collectors.groupingBy(CategoryTmp::getKeyId,Collectors.counting()));
	        //打印出有问题的类目及相应的大小，销售属性大于2
	        Map<String,Long> propertyProblemMap = Maps.filterValues(groupResult, r->r>2);
	        if(null!=propertyProblemMap && !propertyProblemMap.isEmpty()){
	        	propertyProblemMap.forEach((k,v)->{
	        		log.error("类目【{}】,销售属性数量{}！",k,v);
	        	});
	        }
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
