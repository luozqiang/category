package com.listener;

import com.util.RedisCacheUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/*
 * 监听器，用于项目启动的时候初始化信息
 */
@Service
public class StartAddCacheListener implements ApplicationListener<ContextRefreshedEvent>
{
	@Resource
	private RedisCacheUtil<Object> redisCache;
	  
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//spring 启动的时候缓存...
//		if(event.getApplicationContext().getDisplayName().equals("WebApplicationContext for namespace 'springmvc-servlet'")){
//			//初始化bean后...
//			Map<String,Object> cityMap = new HashMap<String, Object>();
//			cityMap.put("01", "杭州市");
//			redisCache.setCacheMap("cityMap", cityMap);
//		}
	}
  
}

