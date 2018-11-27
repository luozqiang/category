package com.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.dao.*;
import com.enums.PropertyIsRequiredEnum;
import com.enums.PropertyModTypeEnum;
import com.enums.PropertyTypeEnum;
import com.google.common.collect.Maps;
import com.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.service.interfaces.IUserService;
import com.util.ExcelUtil;
import com.util.ParamUtil;

@Service
public class UserService implements IUserService{

	@Resource
	private AccountEntityMapper IAccount;
	@Resource
	private CategoryMapper ICategoryDao;
	@Resource
	private PropertyNameMapper IPropertyNameDao;
	@Resource
	private CategoryPropertyNameMapper ICategoryPropertyNameDao;
	@Resource
	private PropertyValueMapper IPropertyValueDao;
	@Resource
	private PropertyPairMapper IProperPairDao;
	@Resource
	private CategoryPropertyValueMapper ICategoryPropertyValueDao;
	@Resource
	private CategoryPropertySkuImageMapper ICategoryPropertySkuImageDao;

	public static Integer ORDER_INCREMENT = 10;

	private Logger log = LoggerFactory.getLogger(UserService.class);


	@Override
	public Map<String, Object> getUserInfo(Map<String, Object> params)
			throws Exception {

		AccountEntityKey account = new AccountEntityKey();
		account.setAccount(params.get("account").toString());
		account.setPlatNo(params.get("plat_no").toString());
		AccountEntity ae = IAccount.selectByPrimaryKey(account);

		return ParamUtil.transBean2Map(ae);
	}

	//类目、属性入库
	public void saveCategoryAndPropertyRecord() throws Exception{
		//文件信息
		Map<String,Object> recordMap = ExcelUtil.getExcelInfo(null);
		if(null!=recordMap && !recordMap.isEmpty()){
			//类目信息
			List<CategoryTmp> categoryList = (List<CategoryTmp>)recordMap.get(ExcelUtil.CATEGORY_KEY);
			//一级类目处理
			Map<String,Long> firstCategoryMap = categoryList.stream().collect(Collectors.groupingBy(CategoryTmp::getFirstCategory,Collectors.counting()));
			firstCategoryMap.forEach((k,v)->{
				Category ca = new Category();
				ca.setName(k);
				Category info = ICategoryDao.selectCategoryInfo(ca);
				if(null==info){
					Category category = new Category();
					category.setIsLeaf(0);
					category.setLevel(1);
					category.setName(k);
					category.setParentId(0);
					category.setSortOrder(0);
					category.setPathName(k);
					category.setPath(null);
					category.setStatus(1);
					ICategoryDao.insertSelective(category);
				}
			});
			//排序从1开始
			int sortCount = 1;
			int sortCount2 = 1;
			for(CategoryTmp categoryTmp : categoryList){
				Category ca = new Category();
				ca.setName(categoryTmp.getFirstCategory());
				Category info = ICategoryDao.selectCategoryInfo(ca);
				if(info.getSortOrder()==0){
					Integer fOrder = ICategoryDao.selectMaxOrderByLevel(1);
					info.setSortOrder(getOrderNext(fOrder));
//					info.setSortOrder(sortCount);
					info.setPath("/"+info.getId()+"/");
					//更新一级类目信息
					ICategoryDao.updateByPrimaryKeySelective(info);
					sortCount += ORDER_INCREMENT;
				}
				//查看二级类目
				String secName = categoryTmp.getSecondCategory();
				if(StringUtils.isBlank(secName)) continue;
				ca.setParentId(info.getId());
				ca.setName(secName);
				Category info2 = ICategoryDao.selectCategoryInfo(ca);
				if(null==info2){
					Category category = new Category();
					category.setIsLeaf(StringUtils.isBlank(categoryTmp.getThirdCategory())?1:0);
					category.setLevel(2);
					category.setName(secName);
					category.setParentId(info.getId());
					Integer sOrder = ICategoryDao.selectMaxOrderByLevel(2);
					category.setSortOrder(getOrderNext(sOrder));
//					category.setSortOrder(sortCount2);
					category.setPathName(info.getPathName()+","+secName);
					category.setPath("/"+info.getId()+"/");
					category.setStatus(1);
					//二级入库
					ICategoryDao.insertSelective(category);
					int secId = category.getId();
					categoryTmp.setSecondCategoryId(secId);
					categoryTmp.setSecondCategoryPath(category.getPathName());
					categoryTmp.setSecondCategoryFullId(category.getPath()+secId+"/");
					if(StringUtils.isBlank(categoryTmp.getThirdCategory())){
						categoryTmp.setLeafCategoryId(secId);
					}
					sortCount2+=ORDER_INCREMENT;
				}else{
					categoryTmp.setSecondCategoryId(info2.getId());
					categoryTmp.setSecondCategoryPath(info2.getPathName());
					categoryTmp.setSecondCategoryFullId(info2.getPath()+info2.getId()+"/");
					if(info2.getIsLeaf()==1){
						categoryTmp.setLeafCategoryId(info2.getId());
					}
				}
			}
			int sortCount3 = 1;
			for(CategoryTmp categoryTmp : categoryList){
				//查找三级类目
				String name = categoryTmp.getThirdCategory();
				if(StringUtils.isBlank(name)) continue;
				Category ca = new Category();
				Integer parentId = categoryTmp.getSecondCategoryId();
				String parentPath = categoryTmp.getSecondCategoryPath();
				ca.setParentId(parentId);
				ca.setName(name);
				Category info3 = ICategoryDao.selectCategoryInfo(ca);
				if(null==info3){
					Category category = new Category();
					category.setIsLeaf(StringUtils.isBlank(categoryTmp.getFourthCategory())?1:0);
					category.setLevel(3);
					category.setName(name);
					category.setParentId(parentId);
					Integer tOrder = ICategoryDao.selectMaxOrderByLevel(3);
					category.setSortOrder(getOrderNext(tOrder));

//					category.setSortOrder(sortCount3);
					category.setPathName(parentPath+","+name);
					category.setPath(categoryTmp.getSecondCategoryFullId());
					category.setStatus(1);
					//三级入库
					ICategoryDao.insertSelective(category);
					int id = category.getId();
					categoryTmp.setThirdCategoryId(id);
					categoryTmp.setThirdCategoryPath(category.getPathName());
					categoryTmp.setThirdCategoryFullId(category.getPath()+id+"/");
					if(StringUtils.isBlank(categoryTmp.getFourthCategory())){
						categoryTmp.setLeafCategoryId(id);
					}
					sortCount3+=ORDER_INCREMENT;
				}else{
					categoryTmp.setThirdCategoryId(info3.getId());
					categoryTmp.setThirdCategoryPath(info3.getPathName());
					categoryTmp.setThirdCategoryFullId(info3.getPath()+info3.getId()+"/");
					if(info3.getIsLeaf()==1){
						categoryTmp.setLeafCategoryId(info3.getId());
					}
				}
			}
			int sortCount4 = 1;
			for(CategoryTmp categoryTmp : categoryList){
				//查找四级类目
				String name = categoryTmp.getFourthCategory();
				if(StringUtils.isBlank(name)) continue;
				Category ca = new Category();
				Integer parentId = categoryTmp.getThirdCategoryId();
				String parentPath = categoryTmp.getThirdCategoryPath();
				ca.setParentId(parentId);
				ca.setName(name);
				Category info4 = ICategoryDao.selectCategoryInfo(ca);
				if(null==info4){
					Category category = new Category();
					category.setIsLeaf(1);
					category.setLevel(4);
					category.setName(name);
					category.setParentId(parentId);
					Integer fOrder = ICategoryDao.selectMaxOrderByLevel(4);
					category.setSortOrder(getOrderNext(fOrder));

//					category.setSortOrder(sortCount4);
					category.setPathName(parentPath+","+name);
					category.setPath(categoryTmp.getThirdCategoryFullId());
					category.setStatus(1);
					//四级入库
					ICategoryDao.insertSelective(category);
					categoryTmp.setLeafCategoryId(category.getId());
					sortCount4+=ORDER_INCREMENT;
				}else{
					categoryTmp.setLeafCategoryId(info4.getId());
				}
			}
			//path路径更新
			ICategoryDao.updateCategoryRemoveOne();


			List<CategoryTmp> categoryListProblem = categoryList.stream().filter(s -> null==s.getLeafCategoryId()).collect(Collectors.toList());
			for(CategoryTmp ct : categoryListProblem){
				log.error("存储后异常类目【{}】！",JSONObject.toJSONString(ct));
			}


			//叶子类目信息
			Map<Integer,Integer> sourceCategoryMap = categoryList.stream().collect(Collectors.toMap(CategoryTmp::getSourceId,CategoryTmp::getLeafCategoryId));
			//属性相关信息
			List<PropertyTmp> propertyList = (List<PropertyTmp>)recordMap.get(ExcelUtil.PROPERTY_KEY);
			if(CollectionUtils.isEmpty(propertyList)) {
				log.info("所有属性信息为空");
				return;
			}
			//属性项记录
			Map<String,Long> propertyMap = propertyList.stream().collect(Collectors.groupingBy(PropertyTmp::getProperty,Collectors.counting()));
			propertyMap.forEach((k,v)->{
				PropertyName propertyNameInfo = IPropertyNameDao.selectPropertyNameByName(k);
				if(null==propertyNameInfo){
					PropertyName propertyName = new PropertyName();
					propertyName.setModifyType(0);
					propertyName.setName(k);
					//属性入库
					IPropertyNameDao.insertSelective(propertyName);
				}
			});
			Map<String,Integer> propertyValueMap = new HashMap<>();
			for(PropertyTmp propertyTmp : propertyList){
				PropertyName propertyName = IPropertyNameDao.selectPropertyNameByName(propertyTmp.getProperty());
				propertyTmp.setPropertyId(propertyName.getId());
				if(Objects.equals(0,propertyName.getModifyType())){
					propertyName.setModifyType(PropertyModTypeEnum.MODIFY_ALLOWED.getVal());
					propertyName.setInputType(propertyTmp.getInputType());
					if(PropertyTypeEnum.GOODS_PROPERTY.getVal()==propertyTmp.getPropertyType()){
						propertyName.setIsGoodsProperty(1);
					}else if(PropertyTypeEnum.SELL_PROPERTY.getVal()==propertyTmp.getPropertyType()){
						propertyName.setIsSellProperty(1);
					}else if(PropertyTypeEnum.KEY_PROPERTY.getVal()==propertyTmp.getPropertyType()){
						propertyName.setIsKeyProperty(1);
					}
					IPropertyNameDao.updateByPrimaryKeySelective(propertyName);
				}else if(0==propertyName.getIsGoodsProperty() && PropertyTypeEnum.GOODS_PROPERTY.getVal()==propertyTmp.getPropertyType()){
					//商品属性更新
					propertyName.setIsGoodsProperty(1);
					IPropertyNameDao.updateByPrimaryKeySelective(propertyName);
				}else if(0==propertyName.getIsSellProperty() && PropertyTypeEnum.SELL_PROPERTY.getVal()==propertyTmp.getPropertyType()){
					//销售属性更新
					propertyName.setIsSellProperty(1);
					IPropertyNameDao.updateByPrimaryKeySelective(propertyName);
				}
				for(Map.Entry<Integer,Integer> entry : sourceCategoryMap.entrySet()){
					if(Objects.equals(propertyTmp.getSourceId(),entry.getKey())){
						propertyTmp.setCategoryId(entry.getValue());
						break;
					}
				}
				CategoryPropertyName categoryPropertyName = ICategoryPropertyNameDao.selectByUniqueKey(propertyTmp.getCategoryId(),propertyTmp.getPropertyId());
				if(null==categoryPropertyName){
					//排序值
					int maxOrder = ICategoryPropertyNameDao.selectMaxOrderByCategory(propertyTmp.getCategoryId());
					int order = getOrderNext(maxOrder);
					CategoryPropertyName categoryPropertyNameAdd = new CategoryPropertyName();
					categoryPropertyNameAdd.setCategoryId(propertyTmp.getCategoryId());
					categoryPropertyNameAdd.setPropertyNameId(propertyTmp.getPropertyId());
					categoryPropertyNameAdd.setIsRequired(propertyTmp.getIsRequire());
					categoryPropertyNameAdd.setInputType(propertyTmp.getInputType());
					categoryPropertyNameAdd.setPropertyType(propertyTmp.getPropertyType());
					categoryPropertyNameAdd.setSortOrder(order);
					//类目属性入库
					ICategoryPropertyNameDao.insertSelective(categoryPropertyNameAdd);

					if(PropertyTypeEnum.SELL_PROPERTY.getVal()==propertyTmp.getPropertyType() && PropertyIsRequiredEnum.REQUIRED_YES.getVal()==propertyTmp.getSkuType()){
						//销售属性入库,只存储必填信息
						CategoryPropertySkuImage categoryPropertySkuImage = new CategoryPropertySkuImage();
						categoryPropertySkuImage.setPropertyNameId(propertyTmp.getPropertyId());
						categoryPropertySkuImage.setIsRequired(propertyTmp.getSkuType());
						categoryPropertySkuImage.setCategoryId(propertyTmp.getCategoryId());
						ICategoryPropertySkuImageDao.insertSelective(categoryPropertySkuImage);
					}
					propertyTmp.setPropertyNameId(categoryPropertyNameAdd.getId());
				}else{
					propertyTmp.setPropertyNameId(categoryPropertyName.getId());
				}
				String propertyValue = propertyTmp.getPropertyValue();
				if(StringUtils.isNotBlank(propertyValue)){
					//属性信息,中文或英文分割
					String[] strs = propertyValue.split("\\||丨");
					for(String str : strs){
						if(StringUtils.isBlank(str)) continue;
						str = str.trim();
						if(!propertyValueMap.containsKey(str.toUpperCase())){
							//查看数据库是否有记录
							PropertyValue propertyValue1 = IPropertyValueDao.selectByName(str.toUpperCase());
							if(null!=propertyValue1){
								propertyValueMap.put(str.toUpperCase(),propertyValue1.getId());//存放属性值信息
							}
						}
						if(!propertyValueMap.containsKey(str.toUpperCase())){
							//新增记录
							PropertyValue propertyValueModel = new PropertyValue();
							propertyValueModel.setName(str);
							propertyValueModel.setNameUpperMd5(str.toUpperCase());//存放大写属性值
							//属性值入库
							IPropertyValueDao.insertSelective(propertyValueModel);
							propertyValueMap.put(str.toUpperCase(),propertyValueModel.getId());//存放属性值信息
							int maxOrder = IProperPairDao.selectMaxOrderByName(propertyTmp.getPropertyId());
							int order = getOrderNext(maxOrder);
							PropertyPair propertyPair = new PropertyPair();
							propertyPair.setPropertyValueId(propertyValueModel.getId());
							propertyPair.setPropertyNameId(propertyTmp.getPropertyId());
							propertyPair.setOrder(order);
							//属性对入库
							IProperPairDao.insertSelective(propertyPair);
							propertyTmp.setPropertyPairId(propertyPair.getId());
						}else{
							//属性值已存储
							//获取属性值id
							Integer propertyValueId = propertyValueMap.get(str.toUpperCase());
							if(null==propertyValueId || propertyValueId<=0){
								throw new RuntimeException("属性值【"+str+"】存储异常！！！");
							}
							PropertyPair propertyPair = IProperPairDao.selectByCondition(propertyTmp.getPropertyId(),propertyValueId);
							if(null==propertyPair){
								//获取排序值
								int maxOrder = IProperPairDao.selectMaxOrderByName(propertyTmp.getPropertyId());
								int order = getOrderNext(maxOrder);
								PropertyPair propertyPairAdd = new PropertyPair();
								propertyPairAdd.setPropertyValueId(propertyValueId);
								propertyPairAdd.setPropertyNameId(propertyTmp.getPropertyId());
								propertyPairAdd.setOrder(order);
								IProperPairDao.insertSelective(propertyPairAdd);
								propertyTmp.setPropertyPairId(propertyPairAdd.getId());
							}else{
								propertyTmp.setPropertyPairId(propertyPair.getId());
							}
						}
						//类目属性对信息
						CategoryPropertyValue categoryPropertyValue = ICategoryPropertyValueDao.selectByCondition(propertyTmp.getPropertyNameId(),propertyTmp.getPropertyPairId());
						if(null==categoryPropertyValue){
							CategoryPropertyValue categoryPropertyValueAdd = new CategoryPropertyValue();
							categoryPropertyValueAdd.setCategoryPropertyNameId(propertyTmp.getPropertyNameId());
							categoryPropertyValueAdd.setPropertyPairId(propertyTmp.getPropertyPairId());
							ICategoryPropertyValueDao.insertSelective(categoryPropertyValueAdd);
						}
					}
				}
			}
		}else {
			throw new RuntimeException("数据校验异常！！！");
		}
	}

	private static Integer getOrderNext(Integer previousOrder){
		if(previousOrder>0){
			previousOrder += ORDER_INCREMENT;
		}else{
			previousOrder = 1;
		}
		return previousOrder;
	}


}
