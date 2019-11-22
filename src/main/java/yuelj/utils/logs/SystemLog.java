package yuelj.utils.logs;

import yuelj.constants.ParamsAndURL;

public class SystemLog {

	public static void printlog(Object log) {
		if (ParamsAndURL.OPEN_LOG)
			System.out.println(log);
	}

	public static void printPerformance(Object log) {
		System.out.println(log);
	}
}
