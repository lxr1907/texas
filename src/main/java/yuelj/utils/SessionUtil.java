package yuelj.utils;

import javax.servlet.http.HttpServletRequest;

import yuelj.entity.UserEntity;

public class SessionUtil {

	/**
	 * 用户信息
	 */
	public static final String SESSION_LOGIN_KEY = "SESSION_LOGIN_INFO";


	private SessionUtil() {

	}

	public static void setUser(HttpServletRequest req, UserEntity user) {
		req.getSession().setAttribute(SESSION_LOGIN_KEY, user);
	}

	public static UserEntity getUser(HttpServletRequest req) {

		Object object = req.getSession().getAttribute(SESSION_LOGIN_KEY);
		if (null == object) {
			return null;
		}
		return (UserEntity) object;
	}


	public static boolean invalidSession(HttpServletRequest req) {
		if (null != req) {
			req.getSession().invalidate();
			if (null == getUser(req)) {
				return true;
			}
			return false;
		}
		return false;
	}

}
