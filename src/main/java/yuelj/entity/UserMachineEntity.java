package yuelj.entity;

public class UserMachineEntity extends BaseEntity {
	private static final long serialVersionUID = 4713222624507729776L;
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 用户设备号
	 */
	private String machine;
	/**
	 * 用户系统版本
	 */
	private String systemversion;
	/**
	 * 用户设备
	 */
	private String deviceversion;
	/**
	 * ios或android
	 */
	private String devicetype;
	/**
	 * 个推id
	 */
	private String getuiid;
	/**
	 * 小米id
	 */
	private String xiaomiid;
	/**
	 * 其他id1
	 */
	private String otherid1;
	/**
	 * 其他id2
	 */
	private String otherid2;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getSystemversion() {
		return systemversion;
	}

	public void setSystemversion(String systemversion) {
		this.systemversion = systemversion;
	}

	public String getDeviceversion() {
		return deviceversion;
	}

	public void setDeviceversion(String deviceversion) {
		this.deviceversion = deviceversion;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getGetuiid() {
		return getuiid;
	}

	public void setGetuiid(String getuiid) {
		this.getuiid = getuiid;
	}

	public String getXiaomiid() {
		return xiaomiid;
	}

	public void setXiaomiid(String xiaomiid) {
		this.xiaomiid = xiaomiid;
	}

	public String getOtherid1() {
		return otherid1;
	}

	public void setOtherid1(String otherid1) {
		this.otherid1 = otherid1;
	}

	public String getOtherid2() {
		return otherid2;
	}

	public void setOtherid2(String otherid2) {
		this.otherid2 = otherid2;
	}
}
