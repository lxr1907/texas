package yuelj.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import yuelj.utils.imgdetect.ImageHelper;
import yuelj.utils.logs.SystemLog;

public final class HttpTool {

	public static String doPostHttp(String reqUrl, Map<String, String> parameters) {
		HttpURLConnection urlConn = null;
		try {
			urlConn = sendPostHttp(reqUrl, parameters);
			String responseContent = getContentHttp(urlConn);
			return responseContent.trim();
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
		}
	}

	public static String doPostHttps(String reqUrl, Map<String, String> parameters) {
		HttpsURLConnection urlConn = null;
		try {
			urlConn = sendPostHttps(reqUrl, parameters);
			String responseContent = getContentHttps(urlConn);
			return responseContent.trim();
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
		}
	}

	public static String doUploadFile(String reqUrl, Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			urlConn = sendFormdata(reqUrl, parameters, fileParamName, filename, contentType, data);
			String responseContent = new String(getBytes(urlConn));
			return responseContent.trim();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	private static HttpURLConnection sendFormdata(String reqUrl, Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			URL url = new URL(reqUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);

			urlConn.setRequestProperty("connection", "keep-alive");

			String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			boundary = "--" + boundary;
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(boundary + "\r\n");
					params.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
					// params.append(URLEncoder.encode(value, "UTF-8"));
					params.append(value);
					params.append("\r\n");
				}
			}

			StringBuilder sb = new StringBuilder();
			// sb.append("\r\n");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + filename
					+ "\"\r\n");
			sb.append("Content-Type: " + contentType + "\r\n\r\n");
			byte[] fileDiv = sb.toString().getBytes("UTF-8");
			byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes("UTF-8");
			byte[] ps = params.toString().getBytes("UTF-8");

			OutputStream os = urlConn.getOutputStream();
			os.write(ps);
			os.write(fileDiv);
			os.write(data);
			os.write(endData);

			os.flush();
			os.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	private static String getContentHttps(HttpsURLConnection urlConn) {
		try {
			String responseContent = null;
			InputStream in = urlConn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
			return responseContent;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static String getContentHttp(HttpURLConnection urlConn) {
		try {
			String responseContent = null;
			InputStream in = urlConn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
			return responseContent;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static byte[] getBytes(String reqUrl) {
		return getBytes(reqUrl, null);
	}

	public static byte[] getBytes(String reqUrl, Map<String, String> parameters) {
		HttpURLConnection conn = sendGet(reqUrl, parameters);
		return getBytes(conn);
	}

	private static byte[] getBytes(HttpURLConnection urlConn) {
		try {
			InputStream in = urlConn.getInputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;)
				os.write(buf, 0, i);
			in.close();
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static HttpURLConnection sendPostHttp(String reqUrl, Map<String, String> parameters) {
		HttpURLConnection urlConn = null;
		try {
			String params = generatorParamString(parameters);
			URL url = new URL(reqUrl);

			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
			urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);
			byte[] b = params.getBytes("UTF-8");
			urlConn.getOutputStream().write(b, 0, b.length);
			urlConn.getOutputStream().flush();
			urlConn.getOutputStream().close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	/**
	 * 忽视证书HostName
	 */
	private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
		public boolean verify(String s, SSLSession sslsession) {
			SystemLog.printlog("WARNING: Hostname is not matched for cert.");
			return true;
		}
	};

	/**
	 * Ignore Certification
	 */
	private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

		private X509Certificate[] certificates;

		@Override
		public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
			}

		}

		@Override
		public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = ax509certificate;
			}
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	};

	/**
	 * 无视证书 参考地址
	 * http://li3huo.com/2009/09/https-certificates-are-ignoring-the-right-java-http-client/
	 * 
	 * @param reqUrl
	 * @param parameters
	 * @return
	 */
	private static HttpsURLConnection sendPostHttps(String reqUrl, Map<String, String> parameters) {
		HttpsURLConnection urlConn = null;
		try {
			String params = generatorParamString(parameters);
			URL url = new URL(reqUrl);

			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			urlConn = (HttpsURLConnection) url.openConnection();

			// Prepare SSL Context
			TrustManager[] tm = { ignoreCertificationTrustManger };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			((HttpsURLConnection) urlConn).setSSLSocketFactory(ssf);

			urlConn.setRequestMethod("POST");
			// urlConn
			// .setRequestProperty(
			// "User-Agent",
			// "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.3)
			// Gecko/20100401 Firefox/3.6.3");
			urlConn.setConnectTimeout(50000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(50000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);
			byte[] b = params.getBytes("UTF-8");
			urlConn.getOutputStream().write(b, 0, b.length);
			urlConn.getOutputStream().flush();
			urlConn.getOutputStream().close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	private static HttpURLConnection sendGet(String reqUrl, Map<String, String> parameters) {
		HttpURLConnection urlConn = null;
		try {
			String params = generatorParamString(parameters);
			URL url = new URL(reqUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			// urlConn
			// .setRequestProperty(
			// "User-Agent",
			// "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.3)
			// Gecko/20100401 Firefox/3.6.3");
			urlConn.setConnectTimeout(5000);// （单位：毫秒）jdk
			urlConn.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			urlConn.setDoOutput(true);
			if (params != null && !"".equals(params)) {
				byte[] b = params.getBytes();
				urlConn.getOutputStream().write(b, 0, b.length);
				urlConn.getOutputStream().flush();
				urlConn.getOutputStream().close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return urlConn;
	}

	/**
	 * 将parameters中数据转换成用"&"链接的http请求参数形式
	 * 
	 * @param parameters
	 * @return
	 */
	public static String generatorParamString(Map<String, String> parameters) {
		StringBuffer params = new StringBuffer();
		if (parameters != null) {
			for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
				String name = iter.next();
				String value = parameters.get(name);
				params.append(name + "=");
				try {
					params.append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (Exception e) {
					String message = String.format("'%s'='%s'", name, value);
					throw new RuntimeException(message, e);
				}
				if (iter.hasNext())
					params.append("&");
			}
		}
		return params.toString();
	}

	/**
	 * 
	 * @param link
	 * @param charset
	 * @return
	 */
	public static String doGet(String link, String charset) {
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			in = new BufferedInputStream(conn.getInputStream());
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for (int i = 0; (i = in.read(buf)) > 0;) {
				out.write(buf, 0, i);
			}
			out.flush();
			String s = new String(out.toByteArray(), charset);
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * UTF-8编码
	 * 
	 * @param link
	 * @return
	 */
	public static String doGet(String link) {
		return doGet(link, "UTF-8");
	}

	public static int getIntResponse(String link) {
		String str = doGet(link);
		return Integer.parseInt(str.trim());

	}

	/**
	 * 下载网络文件
	 * 
	 * @param urlStr
	 * @param id
	 * @return
	 */
	public static File downloadNet(String urlStr, String id) {
		try {
			// 下载网络文件
			int byteread = 0;
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			File f = new File("./downimages/" + id);
			if (!f.exists()) {
				if (!f.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					f.getParentFile().mkdirs();
				}

				f.createNewFile();
			}
			FileOutputStream fs = new FileOutputStream(f);

			byte[] buffer = new byte[8204];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			fs.close();

			File cf = new File("./downimagesJpg/" + id);
			if (!cf.exists()) {
				if (!cf.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					cf.getParentFile().mkdirs();
				}
				cf.createNewFile();
			}
			ImageHelper.convert("./downimages/" + id, "jpg", "./downimagesJpg/" + id);
			//ImageHelper.scaleImageWithParams(cf, UserAction.userPicWidth, UserAction.userPicHeight, true, "jpg");
			return cf;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
