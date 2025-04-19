package com.lxrtalk.texas.utils.filters;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.lxrtalk.texas.entity.SystemLogEntity;
import com.lxrtalk.texas.entity.UserMachineEntity;
import com.lxrtalk.texas.service.SystemLogService;
import com.lxrtalk.texas.service.UserMachineService;
import com.lxrtalk.texas.utils.dateTime.DateUtil;
import com.lxrtalk.texas.utils.logs.LogThread;

/**
 * 日志记录过滤器
 * 
 * @author lxr
 *
 */
@Controller
public class LogFilter implements Filter, Serializable {
	@Serial
	private static final long serialVersionUID = -4220585371840995250L;
	@Autowired
	SystemLogService logservice;
	@Autowired
	UserMachineService userMachineS;

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		chain.doFilter(request, response);
		HttpServletRequest req = (HttpServletRequest) request;
		// uri
		String uri = req.getRequestURI();

		String message = "";
		SystemLogEntity log = new SystemLogEntity();
		String parm = "";
		Map<String, String[]> properties = request.getParameterMap();
		// 返回值Map
		Iterator entries = properties.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		String uid = null;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			parm = parm + name + ":" + value + ",";
		}
		uid = req.getParameter("uid");
		
			if (uid != null && !uid.isEmpty()) {
				log.setUserid(uid);
			}
			String token = req.getParameter("token");
			if (token != null && !token.isEmpty()) {
				log.setToken(token);
			}
			message = "匿名访问：" + req.getRequestURI() + ",参数：" + parm;
		String machine = req.getParameter("machineid");
		if (machine != null && machine.length() != 0) {
			log.setMachine(machine);
		}
		String devicetype = req.getParameter("devicetype");
		if (devicetype != null && !devicetype.isEmpty()) {
			log.setClienttype(devicetype);
		}
		String appversion = req.getParameter("appversion");
		if (appversion != null && !appversion.isEmpty()) {
			log.setAppversion(appversion);
		}

		log.setContent(message);
		log.setIp(req.getRemoteAddr());
		// nginx转发获取ip
		if (req.getHeader("X-real-ip") != null) {
			log.setIp(req.getHeader("X-real-ip").toString());
		}
		log.setOperation(req.getRequestURI());
		log.setDatetime(DateUtil.getDateTimeString(new Date()));

		if (!uri.contains("uploadImg") && !uri.contains("selectStock")) {// 加入系统访问日志
			LogThread logt = new LogThread(log, logservice);
			Thread logtt = new Thread(logt);
			logtt.start();
		}
		// 记录用户的machine,用于推送等
		if (machine != null && !machine.isEmpty()) {
			UserMachineEntity machineinfo = new UserMachineEntity();
			machineinfo.setMachine(machine);
			List<UserMachineEntity> mlist = userMachineS.queryUserMachine(machineinfo);
			if (uid != null && !uid.isEmpty()) {
				machineinfo.setUid(uid);
			} else {
				machineinfo.setUid("0");
			}
			String xiaomiid = req.getParameter("xiaomiid");
			if (xiaomiid != null && !xiaomiid.isEmpty()) {
				machineinfo.setXiaomiid(xiaomiid.replace(" ", "+"));
			}
			String otherid1 = req.getParameter("otherid1");
			if (otherid1 != null && !otherid1.isEmpty()) {
				machineinfo.setOtherid1(otherid1);
			}
			if (devicetype != null && devicetype.length() != 0) {
				machineinfo.setDevicetype(devicetype);
			}
			if (mlist.isEmpty()) {
				userMachineS.addUserMachine(machineinfo);
			} else {
				userMachineS.updateUserMachine(machineinfo);
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}
}
