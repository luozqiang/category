package com.action;

import com.alibaba.fastjson.JSONObject;
import com.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class UserAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IUserService userService;
	

	
	@RequestMapping("/category.action")
	public void dealCategory(HttpServletRequest request,HttpServletResponse response) throws Exception{
		long start = System.currentTimeMillis();
		String msg = null;
		String code = null;
		try {
			userService.saveCategoryAndPropertyRecord();
			code = "0000";
			msg = "成功，耗时："+(System.currentTimeMillis()-start)+"ms";
		} catch (Exception e) {
			code = "10000";
			msg = e.getMessage();
			log.error("数据初始化异常【e:{},message:{}】，检查后重新导入！！！",e,e.getMessage());
		}
		JSONObject json = new JSONObject();
		json.put("recode", code);
		json.put("rsmsg", msg);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(json.toString());
		writer.close();
	}
	

}
