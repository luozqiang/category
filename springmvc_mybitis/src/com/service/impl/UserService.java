package com.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.dao.*;
import com.enums.PropertyInputTypeEnum;
import com.enums.PropertyTypeEnum;
import com.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service.interfaces.IUserService;
import com.util.ExcelUtil;
import com.util.ParamUtil;
import com.util.RedisCacheUtil;


@Service
//@Transactional
public class UserService implements IUserService{
	
	@Resource
	private AccountEntityMapper IAccount;
	@Resource
	private RedisCacheUtil<Object> redisCache;
	
	@Resource
	private CategoryMapper ICategoryDao;
	@Resource
	private CategoryTmpMapper ICategoryTmpDao;
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

    public static Integer ORDER_INCREMENT = 10;



	@Override
	public Map<String, Object> getUserInfo(Map<String, Object> params)
			throws Exception {
		
		AccountEntityKey account = new AccountEntityKey();
		account.setAccount(params.get("account").toString());
		account.setPlatNo(params.get("plat_no").toString());
		AccountEntity ae = IAccount.selectByPrimaryKey(account);
		
//		Map<String,Object> map = redisCache.getCacheMap("cityMap");
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
			});
			//排序从1开始
			int sortCount = 1;
			int sortCount2 = 1;
			for(CategoryTmp categoryTmp : categoryList){
				Category ca = new Category();
				ca.setName(categoryTmp.getFirstCategory());
				Category info = ICategoryDao.selectCategoryInfo(ca);
				if(info.getSortOrder()==0){
					info.setSortOrder(sortCount);
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
					category.setSortOrder(sortCount2);
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
					category.setSortOrder(sortCount3);
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
					category.setSortOrder(sortCount4);
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
			//叶子类目信息
            Map<Integer,Integer> sourceCategoryMap = categoryList.stream().collect(Collectors.toMap(CategoryTmp::getSourceId,CategoryTmp::getLeafCategoryId));
			//属性相关信息
			List<PropertyTmp> propertyList = (List<PropertyTmp>)recordMap.get(ExcelUtil.PROPERTY_KEY);
            //属性项记录
            Map<String,Long> propertyMap = propertyList.stream().collect(Collectors.groupingBy(PropertyTmp::getProperty,Collectors.counting()));
            propertyMap.forEach((k,v)->{
                PropertyName propertyName = new PropertyName();
                propertyName.setModifyType(0);
                propertyName.setName(k);
                //属性入库
                IPropertyNameDao.insertSelective(propertyName);
            });
            for(PropertyTmp propertyTmp : propertyList){
                PropertyName propertyName = IPropertyNameDao.selectPropertyNameByName(propertyTmp.getProperty());
                propertyTmp.setPropertyId(propertyName.getId());
                if(Objects.equals(0,propertyName.getModifyType())){
                    propertyName.setModifyType(1);
                    propertyName.setInputType(propertyTmp.getInputType());
                    if(PropertyTypeEnum.GOODS_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsGoodsProperty(1);
                    }else if(PropertyTypeEnum.SELL_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsSellProperty(1);
                    }else if(PropertyTypeEnum.KEY_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsKeyProperty(1);
                    }
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
                    propertyTmp.setPropertyNameId(categoryPropertyNameAdd.getId());
                }else{
                    propertyTmp.setPropertyNameId(categoryPropertyName.getId());
                }
                String propertyValue = propertyTmp.getPropertyValue();
                if(StringUtils.isNotBlank(propertyValue)){
                    //属性信息
                    String[] strs = propertyValue.split("\\|");
                    for(String str : strs){
                        PropertyValue propertyValueQuery = IPropertyValueDao.selectByName(str);
                        if(null==propertyValueQuery){
                            PropertyValue propertyValueModel = new PropertyValue();
                            propertyValueModel.setName(str);
                            //属性值入库
                            IPropertyValueDao.insertSelective(propertyValueModel);
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
                            //获取属性值id,
                            PropertyPair propertyPair = IProperPairDao.selectByCondition(propertyTmp.getPropertyId(),propertyValueQuery.getId());
                            if(null==propertyPair){
                                //获取排序值
                                int maxOrder = IProperPairDao.selectMaxOrderByName(propertyTmp.getPropertyId());
                                int order = getOrderNext(maxOrder);
                                PropertyPair propertyPairAdd = new PropertyPair();
                                propertyPairAdd.setPropertyValueId(propertyValueQuery.getId());
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


    //处理类目以及属性信息
    /*public void saveCategoryAndPropertyRecordExt() throws Exception{
        //获取处理的记录信息
        Map<String,Object> recordMap = ExcelUtil.getExcelInfo(null);
        if(null!=recordMap && !recordMap.isEmpty()){
            //获取类目相关信息
            List<CategoryTmp> categoryList = (List<CategoryTmp>)recordMap.get(ExcelUtil.CATEGORY_KEY);
            //一级类目入库
            Map<String,Long> firstCategoryMap = categoryList.stream().collect(Collectors.groupingBy(CategoryTmp::getFirstCategory,Collectors.counting()));
            firstCategoryMap.forEach((k,v)->{
                //获取类目名称
                Category category = new Category();
                category.setIsLeaf(0);//1是，0否
                category.setLevel(1);//层级，1一级类目，2二级类目，3三级类目，4四级类目
                category.setName(k);
                category.setParentId(0);
                category.setSortOrder(0);//先设置为0，根据遍历顺序,再进行更新
                category.setPathName(k);//"零食/坚果/特产,饼干/膨化",多级之间逗号分隔
                category.setPath(null);//根据主键id更新，如“/20000/”
                category.setStatus(1);//状态 1可用，0不可用
                //数据存储
                ICategoryDao.insertSelective(category);
            });
            //排序从1开始
            int sortCount = 1;
            int sortCount2 = 1;
            for(CategoryTmp categoryTmp : categoryList){
                //根据名称，先查找父类目
                Category ca = new Category();
                ca.setName(categoryTmp.getFirstCategory());
                Category info = ICategoryDao.selectCategoryInfo(ca);
                if(info.getSortOrder()==0){
                    //更新sort
                    info.setSortOrder(sortCount);
                    sortCount += ORDER_INCREMENT;
                    info.setPath("/"+info.getId()+"/");
                    //更新一级类目信息
                    ICategoryDao.updateByPrimaryKeySelective(info);
                }
                //查找二级类目是否存在
                String secName = categoryTmp.getSecondCategory();
                if(StringUtils.isBlank(secName)) continue;
                ca.setParentId(info.getId());
                ca.setName(secName);
                Category info2 = ICategoryDao.selectCategoryInfo(ca);
                if(null==info2){
                    //新增记录
                    Category category = new Category();
                    category.setIsLeaf(StringUtils.isBlank(categoryTmp.getThirdCategory())?1:0);//1是，0否
                    category.setLevel(2);//层级，1一级类目，2二级类目，3三级类目，4四级类目
                    category.setName(secName);
                    category.setParentId(info.getId());
                    category.setSortOrder(sortCount2);
                    category.setPathName(info.getPathName()+","+secName);//"零食/坚果/特产,饼干/膨化",多级之间逗号分隔
                    category.setPath("/"+info.getId()+"/");//根据主键id更新，如“/20000/”
                    category.setStatus(1);//状态 1可用，0不可用
                    //数据存储
                    ICategoryDao.insertSelective(category);
                    int secId = category.getId();
                    //更新二级类目id信息
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
                    //判断叶子节点
                    if(info2.getIsLeaf()==1){
                        categoryTmp.setLeafCategoryId(info2.getId());
                    }
                }
            }
            int sortCount3 = 1;
            for(CategoryTmp categoryTmp : categoryList){
                //查找三级类目是否存在
                String name = categoryTmp.getThirdCategory();
                if(StringUtils.isBlank(name)) continue;
                Category ca = new Category();
                Integer parentId = categoryTmp.getSecondCategoryId();
                String parentPath = categoryTmp.getSecondCategoryPath();
                ca.setParentId(parentId);
                ca.setName(name);
                Category info3 = ICategoryDao.selectCategoryInfo(ca);
                if(null==info3){
                    //新增记录
                    Category category = new Category();
                    category.setIsLeaf(StringUtils.isBlank(categoryTmp.getFourthCategory())?1:0);//1是，0否
                    category.setLevel(3);//层级，1一级类目，2二级类目，3三级类目，4四级类目
                    category.setName(name);
                    category.setParentId(parentId);
                    category.setSortOrder(sortCount3);
                    category.setPathName(parentPath+","+name);//"零食/坚果/特产,饼干/膨化",多级之间逗号分隔
                    category.setPath(categoryTmp.getSecondCategoryFullId());//根据主键id更新，如“/20000/”
                    category.setStatus(1);//状态 1可用，0不可用
                    //数据存储
                    ICategoryDao.insertSelective(category);
                    int id = category.getId();
                    //更新三级类目id信息
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
                    //判断叶子节点
                    if(info3.getIsLeaf()==1){
                        categoryTmp.setLeafCategoryId(info3.getId());
                    }
                }
            }
            int sortCount4 = 1;
            for(CategoryTmp categoryTmp : categoryList){
                //查找四级类目是否存在
                String name = categoryTmp.getFourthCategory();
                if(StringUtils.isBlank(name)) continue;
                Category ca = new Category();
                Integer parentId = categoryTmp.getThirdCategoryId();
                String parentPath = categoryTmp.getThirdCategoryPath();
                ca.setParentId(parentId);
                ca.setName(name);
                Category info4 = ICategoryDao.selectCategoryInfo(ca);
                if(null==info4){
                    //新增记录
                    Category category = new Category();
                    category.setIsLeaf(1);//1是，0否
                    category.setLevel(4);//层级，1一级类目，2二级类目，3三级类目，4四级类目
                    category.setName(name);
                    category.setParentId(parentId);
                    category.setSortOrder(sortCount4);
                    category.setPathName(parentPath+","+name);//"零食/坚果/特产,饼干/膨化",多级之间逗号分隔
                    category.setPath(categoryTmp.getThirdCategoryFullId());//根据主键id更新，如“/20000/”
                    category.setStatus(1);//状态 1可用，0不可用
                    //数据存储
                    ICategoryDao.insertSelective(category);
                    int id = category.getId();
                    //更新三级类目id信息
                    categoryTmp.setLeafCategoryId(id);
                    sortCount4+=ORDER_INCREMENT;
                }else{
                    //判断叶子节点
                    if(info4.getIsLeaf()==1){
                        categoryTmp.setLeafCategoryId(info4.getId());
                    }
                }
            }
            //更新path信息
            ICategoryDao.updateCategoryRemoveOne();
            System.out.println(categoryList);


            //source_id,leaf_category
            Map<Integer,Integer> sourceCategoryMap = categoryList.stream().collect(Collectors.toMap(CategoryTmp::getSourceId,CategoryTmp::getLeafCategoryId));

            //获取属性相关信息
            List<PropertyTmp> propertyList = (List<PropertyTmp>)recordMap.get(ExcelUtil.PROPERTY_KEY);

            //属性项记录
            Map<String,Long> propertyMap = propertyList.stream().collect(Collectors.groupingBy(PropertyTmp::getProperty,Collectors.counting()));
            propertyMap.forEach((k,v)->{
                //获取类目名称
                PropertyName propertyName = new PropertyName();
                propertyName.setModifyType(0);//暂存0，后面修改掉
                propertyName.setName(k);
                //数据存储
                IPropertyNameDao.insertSelective(propertyName);
            });

            //遍历更新属性项相关信息
            for(PropertyTmp propertyTmp : propertyList){
                //属性项信息更新
                PropertyName propertyName = IPropertyNameDao.selectPropertyNameByName(propertyTmp.getProperty());
                propertyTmp.setPropertyId(propertyName.getId());//属性项编号
                if(Objects.equals(0,propertyName.getModifyType())){
                    propertyName.setModifyType(1);
                    propertyName.setInputType(propertyTmp.getInputType());
                    if(PropertyTypeEnum.GOODS_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsGoodsProperty(1);
                    }else if(PropertyTypeEnum.SELL_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsSellProperty(1);
                    }else if(PropertyTypeEnum.KEY_PROPERTY.getVal()==propertyTmp.getPropertyType()){
                        propertyName.setIsKeyProperty(1);
                    }
                    IPropertyNameDao.updateByPrimaryKeySelective(propertyName);//修改信息
                }
                //查询类目属性表，没有则进行新增操作,注意sort
                //获取叶子类目信息
                for(Map.Entry<Integer,Integer> entry : sourceCategoryMap.entrySet()){
                    if(Objects.equals(propertyTmp.getSourceId(),entry.getKey())){
                        propertyTmp.setCategoryId(entry.getValue());
                        break;
                    }
                }
                CategoryPropertyName categoryPropertyName = ICategoryPropertyNameDao.selectByUniqueKey(propertyTmp.getCategoryId(),propertyTmp.getPropertyId());
                if(null==categoryPropertyName){
                    //获取排序值
                    int maxOrder = ICategoryPropertyNameDao.selectMaxOrderByCategory(propertyTmp.getCategoryId());
                    int order = getOrderNext(maxOrder);
                    CategoryPropertyName categoryPropertyNameAdd = new CategoryPropertyName();
                    categoryPropertyNameAdd.setCategoryId(propertyTmp.getCategoryId());
                    categoryPropertyNameAdd.setPropertyNameId(propertyTmp.getPropertyId());
                    categoryPropertyNameAdd.setIsRequired(propertyTmp.getIsRequire());
                    categoryPropertyNameAdd.setInputType(propertyTmp.getInputType());
                    categoryPropertyNameAdd.setPropertyType(propertyTmp.getPropertyType());
                    categoryPropertyNameAdd.setSortOrder(order);
                    ICategoryPropertyNameDao.insertSelective(categoryPropertyNameAdd);
                    propertyTmp.setPropertyNameId(categoryPropertyNameAdd.getId());
                }else{
                    propertyTmp.setPropertyNameId(categoryPropertyName.getId());
                }
                //属性值信息入库
                String propertyValue = propertyTmp.getPropertyValue();
                if(StringUtils.isNotBlank(propertyValue)){
                    String[] strs = propertyValue.split("\\|");
                    for(String str : strs){
                        //根据属性值查询
                        PropertyValue propertyValueQuery = IPropertyValueDao.selectByName(str);
                        if(null==propertyValueQuery){
                            PropertyValue propertyValueModel = new PropertyValue();
                            propertyValueModel.setName(str);
                            IPropertyValueDao.insertSelective(propertyValueModel);

                            //获取排序值
                            int maxOrder = IProperPairDao.selectMaxOrderByName(propertyTmp.getPropertyId());
                            int order = getOrderNext(maxOrder);
                            //插入属性对信息
                            PropertyPair propertyPair = new PropertyPair();
                            propertyPair.setPropertyValueId(propertyValueModel.getId());
                            propertyPair.setPropertyNameId(propertyTmp.getPropertyId());
                            propertyPair.setOrder(order);
                            IProperPairDao.insertSelective(propertyPair);
                            propertyTmp.setPropertyPairId(propertyPair.getId());
                        }else{
                            //获取属性值id,
                            PropertyPair propertyPair = IProperPairDao.selectByCondition(propertyTmp.getPropertyId(),propertyValueQuery.getId());
                            if(null==propertyPair){
                                //获取排序值
                                int maxOrder = IProperPairDao.selectMaxOrderByName(propertyTmp.getPropertyId());
                                int order = getOrderNext(maxOrder);
                                PropertyPair propertyPairAdd = new PropertyPair();
                                propertyPairAdd.setPropertyValueId(propertyValueQuery.getId());
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
        }
    }*/

    public static void main(String[] args) {
	    Integer i = 300;
	    i+=200;
        System.out.println(i);
	    double dd = 300.123458800;
        System.out.println(dd+"");
        System.out.println(String.valueOf(dd));

        System.out.println(300==i);
        System.out.println(Objects.equals(300,i));
        System.out.println(new DecimalFormat("#.##%").format(dd));//314.16%

        String str = "其他|S|M|L|XS|XL|2XL|3XL|均码|4XL";
        String[] strs = str.split("\\|");
        for(String s : strs){
            System.out.println(s);
        }

    }


}
