package com.lxrtalk.texas.utils.logs;

import com.lxrtalk.texas.entity.SystemLogEntity;
import com.lxrtalk.texas.service.SystemLogService;

public class LogThread implements Runnable {
	private SystemLogService logservice;
	private SystemLogEntity log;

	public LogThread(SystemLogEntity log, SystemLogService logservice) {
		this.log = log;
		this.logservice = logservice;
	}

	@Override
	public void run() {
		// 加入系统访问日志
		logservice.insertSystemLog(log);
	}

}
