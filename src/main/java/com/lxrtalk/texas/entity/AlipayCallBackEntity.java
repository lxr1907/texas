package com.lxrtalk.texas.entity;

public class AlipayCallBackEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * 商户订单号
	 */
	private String out_trade_no;
	/**
	 * 支付宝交易号
	 */
	private String trade_no;
	/**
	 * 交易状态
	 */
	private String trade_status;
	/**
	 * 买家支付宝用户号
	 */
	private String buyer_id;
	/**
	 * 买家支付宝账号
	 */
	private String buyer_email;
	/**
	 * 交易金额
	 */
	private String total_fee;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getBuyer_email() {
		return buyer_email;
	}

	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
}
