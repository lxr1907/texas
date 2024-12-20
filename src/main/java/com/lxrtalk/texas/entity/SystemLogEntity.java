package com.lxrtalk.texas.entity;

/**
 * 操作日志
 * 
 * @author lixiaoran
 *
 */
public class SystemLogEntity extends BaseEntity {
	private static final long serialVersionUID = -7834723623609511634L;
	/**
	 * 用户id
	 */
	private String userid;
	/**
	 * 操作类型
	 */
	private String type;
	/**
	 * 操作
	 */
	private String operation;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 操作内容
	 */
	private String content;
	/**
	 * 操作时间
	 */
	private String datetime;

	/**
	 * 操作时间开始
	 */
	private String datetimeStart;

	/**
	 * 操作时间结束
	 */
	private String datetimeEnd;
	/**
	 * 机器码
	 */
	private String machine;
	/**
	 * 设备类型ios1，安卓2
	 */
	private String clienttype;
	private String token;
	/**
	 * app版本
	 */
	private String appversion;

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getClienttype() {
		return clienttype;
	}

	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public String getDatetimeStart() {
		return datetimeStart;
	}

	public void setDatetimeStart(String datetimeStart) {
		this.datetimeStart = datetimeStart;
	}

	public String getDatetimeEnd() {
		return datetimeEnd;
	}

	public void setDatetimeEnd(String datetimeEnd) {
		this.datetimeEnd = datetimeEnd;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
