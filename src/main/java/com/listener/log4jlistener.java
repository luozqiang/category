package com.listener;

import com.util.PropertiesUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class log4jlistener implements ServletContextListener {

	public static final String log4jdirkey = "log4jdir";
	public static final String log4jlevelkey = "log4jlevel";


	public void contextDestroyed(ServletContextEvent servletcontextevent) {
		System.getProperties().remove(log4jdirkey);

	}

	public void contextInitialized(ServletContextEvent servletcontextevent) {
		String log4jdir = "/Users/";//默认路径
		String logLevel = "INFO";//默认级别
		try {
			String logPath = PropertiesUtil.newInstance().getProperty("logPath");//日志路径
			String log_Level = PropertiesUtil.newInstance().getProperty("logLevel");//日志级别
			//先查看是否设置日志路径
			if(logPath != null && logPath.trim().length() != 0) { 
				log4jdir = logPath;
			}else{
				//获取根路径
				String class_path = this.getClass().getClassLoader().getResource("/").getPath();
				log4jdir = class_path.substring(1,3);
			}
			//查看日志级别
			if(log_Level != null && log_Level.trim().length() != 0) { 
				logLevel = log_Level;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.setProperty(log4jdirkey, log4jdir);
			System.setProperty(log4jlevelkey, logLevel);
		}
	}

}