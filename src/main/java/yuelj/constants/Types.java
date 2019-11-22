package yuelj.constants;

/**
 * 类型常量
 * 
 * @author lxr 2015年6月23日 下午5:17:40
 */
public class Types {

	/**
	 * 付款类型---支付宝
	 */
	public static final String INN_ACCOUT_TYPE_ALIPAY = "alipay";

	/**
	 * 成功
	 */
	public static final int SUCCESS = 1;
	/**
	 * 失败
	 */
	public static final int FAIL = 0;

	/**
	 * 0删除，1启用，2不启用
	 */
	public static final int ROOM_TYPE_STATE_OPEN = 1;

	public static final int ROOM_TYPE_STATE_CLOSED = 2;

	public static final int ROOM_TYPE_STATE_DEL = 0;


	/**
	 * 正常
	 */
	public static final int YLJ_NORMAL = 1;

	/**
	 * 失效
	 */
	public static final int YLJ_EXPIRE = 0;

	/**
	 * 渠道--默认
	 */
	public static final String CHANNEL_DEFAULT = "default";

	/**
	 * 黑名单
	 */
	public static final String GUEST_BLACK = "2";

}
