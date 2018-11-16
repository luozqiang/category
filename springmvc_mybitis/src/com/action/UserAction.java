package com.action;

import java.io.PrintWriter;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.service.interfaces.IUserService;
import com.util.ParamUtil;

@Controller
public class UserAction {

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
			System.out.println(e+","+msg);
			e.printStackTrace();
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
