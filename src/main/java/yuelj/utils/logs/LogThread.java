package yuelj.utils.logs;

import yuelj.entity.SystemLogEntity;
import yuelj.service.SystemLogService;

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
