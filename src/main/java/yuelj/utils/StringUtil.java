package yuelj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yuelj.utils.logs.SystemLog;

/**
 * 字符串工具类
 * 
 * @author lxr 2015年6月25日 下午3:31:03
 */
public class StringUtil {
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str || str.equals("")) {
			return true;
		}
		return false;
	}

	public static String toView(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	public static String trim(String str) {
		if (null == str) {
			str = "";
		}

		return str.trim();
	}

	public static String trim(Object str) {
		if (null == str) {
			str = "";
		}

		return trim(str.toString());
	}

	/**
	 * 判断字符是否匹配多个字符串中的任意一个
	 * 
	 * @param src
	 * @param matchs
	 * @return
	 */
	public static boolean matchs(String src, String[] matchs) {
		if (isEmpty(src) || null == matchs || matchs.length == 0) {
			return false;
		} else if (Arrays.asList(matchs).contains(src)) {
			return true;
		}
		return false;
	}

	/**
	 * 字符串转换为整形值,异常时使用默认值
	 * 
	 * @param src
	 * @param defaultValue
	 * @return
	 */
	public static int toIntDefValue(String src, int defaultValue) {
		int intValue = 0;
		try {
			intValue = Integer.parseInt(src);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		return intValue;
	}

	/**
	 * 字符串转换为长整形值,异常时使用默认值
	 * 
	 * @param src
	 * @param defaultValue
	 * @return
	 */
	public static Long toLongDefValue(String src, Long defaultValue) {
		Long intValue = 0l;
		try {
			intValue = Long.parseLong(src);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		return intValue;
	}

	public static Double toDoubleDefValue(String src, Double defaultValue) {
		Double dvalue = 0d;
		try {
			dvalue = Double.parseDouble(src);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		return dvalue;
	}

	public static Double toDoubleWithExeption(String src) {
		Double dvalue = 0d;
		try {
			dvalue = Double.parseDouble(src);
		} catch (NumberFormatException e) {
			throw e;
		}
		return dvalue;
	}

	/**
	 * 字符串转换为整形值,异常时抛出异常
	 * 
	 * @param src
	 * @param defaultValue
	 * @return
	 */
	public static int toIntWithException(String src) {
		int intValue = 0;
		try {
			intValue = Integer.parseInt(src);
		} catch (NumberFormatException e) {
			throw e;
		}
		return intValue;
	}

	/**
	 * 字符串转换为长整形值,异常时抛出异常
	 * 
	 * @param src
	 * @param defaultValue
	 * @return
	 */
	public static Long toLongWithException(String src) {
		Long intValue = 0l;
		try {
			intValue = Long.parseLong(src);

		} catch (NumberFormatException e) {
			throw e;
		}
		return intValue;
	}

	/**
	 * 正则校验
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static boolean regix(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 比较整形数值与字符串是否相等
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEq(int a, String b) {
		if (null == b) {
			return false;
		}
		return String.valueOf(a).equals(b);
	}

	/**
	 * 比较两个字符串是否相等
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEq(String a, String b) {
		if (null == a || null == b) {
			return false;
		}
		return a.equals(b);
	}

	public static String subStr(String str, int length, int isEnd) {
		String resultStr = "";
		char[] ch = str.toCharArray();
		int count = ch.length;
		for (int i = 0; i < count; i++) {
			resultStr += ch[i];
			if (isEnd == 1) {
				if (resultStr.getBytes().length >= length - 3) {
					resultStr += "...";
					break;
				}
			} else {
				if (resultStr.getBytes().length >= length) {
					break;
				}
			}

		}
		return resultStr;
	}

	/**
	 * 十六进制字符串到字节数组的转换
	 * 
	 * @param s
	 *            十六进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexStringToByteArray(String s) {
		byte[] buf = new byte[s.length() / 2];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) (chr2hex(s.substring(i * 2, i * 2 + 1)) * 0x10
					+ chr2hex(s.substring(i * 2 + 1, i * 2 + 2)));
		}
		return buf;
	}

	/**
	 * 十六进制字符到字节类型数据的转换
	 * 
	 * @param chr
	 *            十六进制字符
	 * @return 字节类型数据
	 */
	private static byte chr2hex(String chr) {
		if ("0".equals(chr)) {
			return 0x00;
		} else if ("1".equals(chr)) {
			return 0x01;
		} else if ("2".equals(chr)) {
			return 0x02;
		} else if ("3".equals(chr)) {
			return 0x03;
		} else if ("4".equals(chr)) {
			return 0x04;
		} else if ("5".equals(chr)) {
			return 0x05;
		} else if ("6".equals(chr)) {
			return 0x06;
		} else if ("7".equals(chr)) {
			return 0x07;
		} else if ("8".equals(chr)) {
			return 0x08;
		} else if ("9".equals(chr)) {
			return 0x09;
		} else if ("A".equals(chr)) {
			return 0x0a;
		} else if ("B".equals(chr)) {
			return 0x0b;
		} else if ("C".equals(chr)) {
			return 0x0c;
		} else if ("D".equals(chr)) {
			return 0x0d;
		} else if ("E".equals(chr)) {
			return 0x0e;
		} else if ("F".equals(chr)) {
			return 0x0f;
		} else {
			return 0x00;
		}
	}

	public static byte[] ivGeneration(String ivString) {
		byte[] ivBytes = new byte[ivString.length()];
		for (int i = 0; i < ivString.length(); i++) {
			ivBytes[i] = Byte.parseByte(ivString.substring(i, i + 1));
		}
		return ivBytes;
	}

	/**
	 * 格式化HTML文本
	 * 
	 * @param content
	 * @return
	 */
	public static String htmlToStr(String content) {
		if (content == null)
			return "";
		content = content.replaceAll("'", "&apos;");
		content = content.replaceAll("\"", "&quot;");
		content = content.replaceAll("\t", "&nbsp;&nbsp;");// 替换跳格
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		return content;
	}

	/**
	 * 
	 * 
	 * @param url
	 * @param paramName
	 * @author Administrator
	 * @time May 31, 2013 9:28:58 AM
	 */
	public static String parseUrl(String url, String paramName) {
		if (isEmpty(url) || isEmpty(paramName)) {
			return "";
		}
		String[] array = url.split("\\?");
		String[] array2 = array[1].split("\\&");
		for (String arr : array2) {
			String[] array3 = arr.split("=");
			if (isEq(array3[0], paramName)) {
				return array3[1];
			}
		}
		return "";
	}

	/**
	 * inputstream convert to string
	 * 
	 * @param is
	 * @return
	 * @author Administrator
	 * @time May 31, 2013 9:48:28 AM
	 */
	public static String streamToStr(InputStream is) {
		String str = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String tempBf = null;
			while (null != (tempBf = reader.readLine())) {
				str += tempBf;
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return str;
	}

	public static String unescapeUnicode(String str) {

		try {
			return java.net.URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	

	public static void main(String[] args) {
		SystemLog.printlog(unescapeUnicode("%E7%9A%84%E7%9A%84"));
	}
}
