package yuelj.utils.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import yuelj.constants.CacheKeys;
import yuelj.utils.StringUtil;
import yuelj.utils.cache.MemCacheOcsUtils;

public class UserLoginFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		// String uri = req.getRequestURI();
		// 通过token值判断是否需要重新登录
		String uid = req.getParameter("uid");
		String token = req.getParameter("token");
		String servertoken = MemCacheOcsUtils
				.getData(CacheKeys.APP_TOKEN + uid);
		if (!StringUtil.isEmpty(servertoken) && !StringUtil.isEmpty(token)
				&& servertoken.equals(token)) {
			chain.doFilter(request, response);
			return;
		}

	}

	public void init(FilterConfig config) throws ServletException {

	}

}
