package yuelj.utils;

import java.util.HashMap;
import java.util.Map;

import yuelj.entity.QQInfoEntity;
import yuelj.utils.logs.SystemLog;
import yuelj.utils.serialize.JsonUtils;

/**
 * oauth2服务端验证
 * 
 * @author lxr
 *
 */
public class OAuth2Verify {
	private static final String qqOauthUrl = "https://graph.qq.com/oauth2.0/me";
	private static final String qqInfoUrl = "https://graph.qq.com/user/get_user_info";

	// access_token:BE0F86CF17EA7756B045D7129057DE84
	// openid：ED08366FB450BB34C52763A20570E44F
	/**
	 * 根据acctoken获取openid
	 * 
	 * @param access_token
	 * @return
	 */
	public static String getQQopenId(String access_token) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("access_token", access_token);
		String ret = HttpTool.doPostHttps(qqOauthUrl, parameters);
		SystemLog.printlog(ret);
		return ret;
	}

	/**
	 * 获取qq信息
	 * 
	 * @param access_token
	 * @param openid
	 * @return
	 */
	public static QQInfoEntity getQQinfo(String access_token, String openid) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("access_token", access_token);
		parameters.put("oauth_consumer_key", "100371282");
		parameters.put("openid", openid);
		String ret = HttpTool.doPostHttps(qqInfoUrl, parameters);
		QQInfoEntity info = null;
		if (ret != null && ret.length() != 0) {
			info = JsonUtils.fromJson(ret, QQInfoEntity.class);
		}
		return info;
	}

	// {
	// "ret": 0,
	// "msg": "",
	// "is_lost": 0,
	// "nickname": "Bash",
	// "gender": "男",
	// "province": "",
	// "city": "",
	// "year": "1995",
	// "figureurl":
	// "http://qzapp.qlogo.cn/qzapp/100371282/ED08366FB450BB34C52763A20570E44F/30",
	// "figureurl_1":
	// "http://qzapp.qlogo.cn/qzapp/100371282/ED08366FB450BB34C52763A20570E44F/50",
	// "figureurl_2":
	// "http://qzapp.qlogo.cn/qzapp/100371282/ED08366FB450BB34C52763A20570E44F/100",
	// "figureurl_qq_1":
	// "http://q.qlogo.cn/qqapp/100371282/ED08366FB450BB34C52763A20570E44F/40",
	// "figureurl_qq_2":
	// "http://q.qlogo.cn/qqapp/100371282/ED08366FB450BB34C52763A20570E44F/100",
	// "is_yellow_vip": "0",
	// "vip": "0",
	// "yellow_vip_level": "0",
	// "level": "0",
	// "is_yellow_year_vip": "0"
	// }
	public static void main(String[] args) {
		// 测试代码
		String ret = HttpTool.doPostHttps("https://graph.qq.com/oauth2.0/me?access_token=YOUR_ACCESS_TOKEN", null);
		SystemLog.printlog(ret);

	}
}
