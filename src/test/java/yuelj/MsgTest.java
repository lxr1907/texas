package yuelj;

import java.util.HashMap;
import java.util.Map;

import yuelj.utils.HttpTool;
import yuelj.utils.logs.SystemLog;

/**
 * 短信发送测试
 * 
 * @author lixiaoran
 *
 */
public class MsgTest {
	public static void main(String[] args) {
		loginTest();
	}

	public static void loginTest() {
		Map<String, String> parmmap = new HashMap<String, String>();
		parmmap.put("entityJson", "{account:153,password:153}");
		String returnStr = HttpTool.doPostHttp("/yuelijiang/login.action", parmmap);
		SystemLog.printlog(returnStr);
	}
}
