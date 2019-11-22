package yuelj.entity;

/**
 * 用户
 * 
 * @author lixiaoran
 *
 */
public class UserEntity extends BaseEntity {

	private static final long serialVersionUID = 8711509967340105161L;
	/**
	 * 第三方openid
	 */
	private String oauthid;
	/**
	 * 用户昵称
	 */
	private String name;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 电话号码
	 */
	private String phone;
	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 头像
	 */
	private String pic;
	/**
	 * 图片url
	 */
	private String picurl;
	/**
	 * 1普通，2草根高手，3后台管理员
	 */
	private String usertype;
	/**
	 * 登录类型，1phone，2qq，3weixin，4sina等
	 */
	private String logintype;

	/**
	 * 用户状态 0审核中，1正常，2冻结
	 */
	private String status;
	/**
	 * 用于app登录验证的token
	 */
	private String token;
	/**
	 * 短信验证码
	 */
	private String validateCode;
	/**
	 * 是否在本平台开户
	 */
	private String isopenhere;
	/**
	 * vip等级
	 */
	private String viplevel;
	/**
	 * 积分
	 */
	private String score;

	/**
	 * 描述
	 */
	private String description;
	/**
	 * 注册时间
	 */
	private String regdate;
	/**
	 * 用于第三方登录
	 */
	private String accesstoken;
	/**
	 * 证件类型 1身份证，2军官证
	 */
	private String cardtype;
	/**
	 * 证件号
	 */
	private String cardid;
	/**
	 * 真实姓名
	 */
	private String realname;
	/**
	 * 微博被点赞数量，缓存存放
	 */
	private String likecount;
	/**
	 * 评论数，缓存存放
	 */
	private String commentcount;
	/**
	 * 发帖数，缓存存放
	 */
	private String microblogcount;

	/**
	 * 被关注数，缓存存放
	 */
	private String followcount;
	/**
	 * 已关注
	 */
	private String isfollow;
	/**
	 * 已拉黑
	 */
	private String isblack;

	public String getIsfollow() {
		return isfollow;
	}

	public void setIsfollow(String isfollow) {
		this.isfollow = isfollow;
	}

	public String getIsblack() {
		return isblack;
	}

	public void setIsblack(String isblack) {
		this.isblack = isblack;
	}

	public String getMicroblogcount() {
		return microblogcount;
	}

	public void setMicroblogcount(String microblogcount) {
		this.microblogcount = microblogcount;
	}

	public String getFollowcount() {
		return followcount;
	}

	public void setFollowcount(String followcount) {
		this.followcount = followcount;
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public String getCardtype() {
		return cardtype;
	}

	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getLikecount() {
		return likecount;
	}

	public void setLikecount(String likecount) {
		this.likecount = likecount;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getLogintype() {
		return logintype;
	}

	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public String getIsopenhere() {
		return isopenhere;
	}

	public void setIsopenhere(String isopenhere) {
		this.isopenhere = isopenhere;
	}

	public String getViplevel() {
		return viplevel;
	}

	public void setViplevel(String viplevel) {
		this.viplevel = viplevel;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOauthid() {
		return oauthid;
	}

	public void setOauthid(String oauthid) {
		this.oauthid = oauthid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
