package com.lxrtalk.texas.utils.SensitiveWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;

/**
 * 敏感词汇类. 此类会读取资源文件(默认为tabooed.words)中的词汇.
 * 
 * @author lxr
 */
public class TabooedWords {

	// private static final Logger logger =
	// LoggerFactory.getLogger(TabooedWords.class);

	private final Collection<String> tabooedWords = new HashSet<String>();

	/**
	 * 从默认的敏感词汇文件中读取词汇, 初始化敏感词汇列表.
	 */
	public synchronized void initialize() {
		InputStream in = TabooedWords.class.getClassLoader().getResourceAsStream("com/lxrtalk/texas/utils/SensitiveWord/tabooed.words");
		initialize(in, "UTF-8");
	}

	/**
	 * 从指定的敏感词汇输入流中读取词汇, 初始化敏感词汇列表.
	 * 
	 * @param in
	 *            敏感词汇输入流
	 * @param charset
	 *            编码方式
	 */
	public synchronized void initialize(InputStream in, String charset) {
		// logger.debug("Initializing tabooed words...");

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line != null && line.length() != 0) {
					tabooedWords.add(line.toLowerCase());
				}
			}
			// logger.info("Loaded {} tabooed words", tabooedWords.size());
		} catch (Exception e) {
			// logger.error("Could not read tabooed.words", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// FileUtils.close(reader);
		}
	}

	/**
	 * 获取所有读取到的敏感词汇.
	 * 
	 * @return 敏感词汇
	 */
	public Collection<String> getTabooedWords() {
		return tabooedWords;
	}

}
