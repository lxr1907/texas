package yuelj.utils.md5encrypt;

public class Md5PwdExhaustive {
	// 密码可能会包含的字符集合
	static char[] charSource = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	static int sLength = charSource.length; // 字符集长度

	public static void main(String[] args) {
		long beginMillis = System.currentTimeMillis();
		System.out.println("开始");
		 // 设置可能最长的密码长度
		int maxLength = 6;
		CrackPass(maxLength);
		long endMillis = System.currentTimeMillis();
		System.out.println("结束，耗时：" +(float) (endMillis - beginMillis) / 1000 + "秒");
	}

	// 得到密码长度从 1到maxLength的所有不同长的密码集合
	public static void CrackPass(int maxLength) {
		for (int i = 1; i <= maxLength; i++) {
			char[] list = new char[i];
			Crack(list, i);
		}

	}

	// 得到长度为len所有的密码组合，在字符集charSource中
	// 递归表达式：fn(n)=fn(n-1)*sLenght; 大致是这个意思吧
	private static void Crack(char[] list, int len) {
		if (len == 0) { // 递归出口，list char[] 转换为字符串，并打印
			//System.out.println(ArrayToString(list));
		} else {
			for (int i = 0; i < sLength; i++) {
				list[len - 1] = charSource[i];
				Crack(list, len - 1);
			}
		}
	}

	// list char[] 转换为字符串
	private static String ArrayToString(char[] list) {
		if (list == null || list.length == 0)
			return "";
		StringBuilder buider = new StringBuilder(list.length * 2);
		for (int i = 0; i < list.length; i++) {
			buider.append(list[i]);
		}
		return buider.toString();

	}
}
