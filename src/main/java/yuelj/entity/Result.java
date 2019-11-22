package yuelj.entity;

public class Result extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int state;// 1：表示成功；0：表示失败
	private String message;
	private String uri;
	private Object data;
	private String datastr;

	public String getDatastr() {
		return datastr;
	}

	public void setDatastr(String datastr) {
		this.datastr = datastr;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Result() {
	}

	/**
	 * 结果对象
	 * 
	 * @param state
	 *            1 成功, 0 失败
	 * @param message
	 *            提示信息
	 */
	public Result(int state, String message) {
		this.state = state;
		this.message = message;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
