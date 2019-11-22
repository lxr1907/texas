package yuelj.texas;

import java.lang.reflect.Method;

import javax.websocket.Session;

public class BeanUtil {
	/**
	 * 执行某对象方法
	 * 
	 * @param owner
	 *            对象
	 * @param methodName
	 *            方法名
	 * @param args
	 *            参数
	 * @return 方法返回值
	 * @throws Exception
	 */
	public static Object invokeMethod(Object owner, String methodName, Session session, String message)
			throws Exception {
		Class<? extends Object> ownerClass = owner.getClass();
		Class<? extends Object>[] argsClass = new Class<?>[2];
		argsClass[0] = Session.class;
		argsClass[1] = String.class;
		Method method = ownerClass.getMethod(methodName, argsClass);
		return method.invoke(owner,new Object[]{session,message});
	}

	/**
	 * 执行某类的静态方法
	 * 
	 * @param className
	 *            类名
	 * @param methodName
	 *            方法名
	 * @param args
	 *            参数数组
	 * @return 执行方法返回的结果
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
		Class<?> ownerClass = Class.forName(className);

		Class[] argsClass = new Class[args.length];

		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(null, args);
	}
}
