package yuelj;

import java.util.HashMap;
import java.util.Map;

public class ActivityRewardTest {
	public static void main(String[] args) {
		getActivityRewardTest();
	}

	public static void getActivityRewardTest() {
		for (int i = 0; i < 220; i++) {
			Map<String, String> parmmap = new HashMap<String, String>();
			parmmap.put("entityJson", "{name:'lxr'}");
			TestUtil.postTest(parmmap, "getActivityReward");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
