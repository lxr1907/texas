package yuelj;

import java.util.Map;

import yuelj.utils.HttpTool;
import yuelj.utils.logs.SystemLog;

public class TestUtil {
	public static String ENTITY_JSON = "entityJson";
	public static String ip121 = "http://121.43.99.120/fdj/";
	public static String ip = "http://114.55.130.198/fdj/";
	public static String localip = "http://localhost:8080/LxrTexas/";

	public static String postTest(Map<String, String> parmmap, String action) {
		String returnStr = HttpTool.doPostHttp(localip + action + ".action", parmmap);
		SystemLog.printlog(returnStr);
		return returnStr;
	}

	public static String postTest121(Map<String, String> parmmap, String action) {
		String returnStr = HttpTool.doPostHttp(ip121 + action + ".action", parmmap);
		SystemLog.printlog(returnStr);
		return returnStr;
	}
}
