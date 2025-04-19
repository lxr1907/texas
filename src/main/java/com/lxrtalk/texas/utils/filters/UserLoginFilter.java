package com.lxrtalk.texas.utils.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.lxrtalk.texas.constants.CacheKeys;
import com.lxrtalk.texas.utils.StringUtil;
import com.lxrtalk.texas.utils.cache.MemCacheOcsUtils;

public class UserLoginFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// 通过token值判断是否需要重新登录
		String uid = req.getParameter("uid");
		String token = req.getParameter("token");
		String servertoken = MemCacheOcsUtils
				.getData(CacheKeys.APP_TOKEN + uid);
		if (!StringUtil.isEmpty(servertoken) && !StringUtil.isEmpty(token)
				&& servertoken.equals(token)) {
			chain.doFilter(request, response);
		}

	}

	public void init(FilterConfig config) throws ServletException {

	}

}
