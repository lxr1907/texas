package com.lxrtalk.texas.utils.interceptors;

import java.util.Properties;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionInterceptor implements Interceptor {
	private static Logger logger = LogManager.getLogger(ExceptionInterceptor.class);
	private static final long serialVersionUID = -4654850207254592726L;

	public void destroy() {

	}

	public void init() {

	}

	@Override
	public Object intercept(Invocation actionInvocation) throws Throwable {
		String methodName = actionInvocation.getMethod().getName();
		String className = actionInvocation.getClass().getName();
		String result = "";
		try {
			if (actionInvocation != null) {
				actionInvocation.proceed();
			}
		} catch (Exception e) {
			logger.info("method:" + methodName + "，in class:" + className + ",exception :" + e);
		}
		return result;
	}

	@Override
	public Object plugin(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}
}
