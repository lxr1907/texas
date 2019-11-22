package yuelj;

import java.util.HashMap;
import java.util.Map;

public class UserTest {

	public static void main(String[] args) {
		loginTest();
	}

	/**
	 * 登录测试
	 */
	public static void loginTest() {
		Map<String, String> parmmap = new HashMap<String, String>();
		parmmap.put("entityJson", "{phone:18667102107,password:153,logintype:1}");
		TestUtil.postTest(parmmap, "login");
	}

	/**
	 * 注册测试
	 */
	public static void registTest() {
		Map<String, String> parmmap = new HashMap<String, String>();
		parmmap.put("entityJson", "{phone:18667102107,password:153,validateCode:2405}");
		TestUtil.postTest(parmmap, "regist");
	}

	/**
	 * 获取验证码测试
	 */
	public static void getRegistCodeTest() {
		Map<String, String> parmmap = new HashMap<String, String>();
		parmmap.put("entityJson", "{phone:18667102107}");
		TestUtil.postTest(parmmap, "getRegistCode");
	}

}
