package yuelj.utils.md5encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/*
 * MD5 算法
 */
public class Md5 {

	// 全局数组
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	public Md5() {
	}

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// SystemLog.printlog("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	/**
	 * 普通的md5加密
	 * 
	 * @param strObj
	 * @return
	 */
	public static String GetMD5Code(String strObj) {
		String resultString = null;
		try {
			resultString = new String(strObj);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	/**
	 * 普通的md5加密,利用guava包
	 * 
	 * @param strObj
	 * @return
	 */
	public static String GetMD5Guava(String pwd) {
		String md = Hashing.md5().newHasher().putString(pwd, Charsets.UTF_8).hash().toString();
		return md;
	}

	/**
	 * md5加盐 md5(md5($pass).$salt); VB;DZ
	 * 用于dz,vB等论坛程序，discuz的salt长度是6位，vBulletin的salt长度是3位或30位。
	 * 
	 * @param upwd
	 * @param salt
	 * @return
	 */
	public static String getDiscuzMd5(String upwd, String salt) {
		String pwd = GetMD5Guava(upwd) + salt;//
		pwd = GetMD5Guava(pwd);
		return pwd;
	}


	/**
	 * 传入加密后的密码和盐
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	public static String getPasswordStr(String password, String salt) {
		String pwdnum = "";
		Date time = new Date();
		for (int i = 0; i < 1000000; i++) {
			int pi = i;
			if (i == 0) {
				pi = 123456;
			}
			String pwd = getDiscuzMd5(pi + "", salt);
			if (password.equals(pwd)) {
				pwdnum = pi + "";
				break;
			}
		}
		Date endtime = new Date();
		float t = (float) (endtime.getTime() - time.getTime()) / 1000;
		System.out.println("耗时" + t + "秒");
		if ("".equals(pwdnum)) {
			System.out.println("没有得到结果");
		} else {
			System.out.println("密码是：" + pwdnum);
		}
		return pwdnum;
	}

	public static Boolean isPasswordRight(String pwd, String salt, String pwdEncrypt) {
		String mypwdEncrypt = getDiscuzMd5(pwd + "", salt);
		if (pwdEncrypt.equals(mypwdEncrypt)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String pwd = "2ac4780bfb6c4d55edb037a162d54066";
		String salt = "w0XnYY";
	}
}