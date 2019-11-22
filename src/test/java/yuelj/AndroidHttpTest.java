package yuelj;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import yuelj.utils.logs.SystemLog;

public class AndroidHttpTest {

	public static void main(String[] args) {
		try {
			getHttpPostResponseString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getHttpPostResponseString() throws IOException {
		String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";

		OutputStream os;
		String url = "http://121.43.99.120/fdj/regist.action";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.connect();

		os = new BufferedOutputStream(conn.getOutputStream());
		String str = "account=18258265475&password=qqqqqq";
		os.write(str.getBytes("UTF-8"));
		os.flush();
		os.close();

		String response = "";
		try {
			response = getStringByInputStream(conn.getInputStream(), "utf-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		SystemLog.printlog(response);
		return response;
	}

	public static String getStringByInputStream(InputStream is, String charset) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is, charset));
		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}
}
