package yuelj.utils.interceptors;

import java.util.Properties;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import yuelj.utils.logs.SystemLog;

public class ExceptionInterceptor implements Interceptor {
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
			SystemLog.printlog("method:" + methodName + "，in class:" + className + ",exception :" + e);
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
