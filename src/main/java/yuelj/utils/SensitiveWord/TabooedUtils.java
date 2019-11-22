package yuelj.utils.SensitiveWord;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 对文本内容进行敏感词汇过滤的工具类。
 * 
 * @author lxr
 * 
 */
public abstract class TabooedUtils {

	// private static final Logger logger =
	// LoggerFactory.getLogger(TabooedUtils.class);

	private static final TabooedTools tabooedTools = TabooedTools.getInstance();

	/**
	 * 对文本内容进行过滤，获取所有存在的敏感词汇。
	 * 
	 * @param content
	 *            需要进行过滤的内容
	 * @return 过滤的敏感词汇列表
	 */
	public static List<String> getTabooedWords(String content) {
		if (content == null || content.length() == 0) {
			return Collections.emptyList();
		}

		return tabooedTools.getTabooedWords(content);
	}

	/**
	 * 对文本内容进行过滤，获取所有存在的敏感词汇。
	 * 
	 * @param content
	 *            需要进行过滤的内容
	 * @param replaceto
	 *            替换为**等
	 * @return 过滤的敏感词汇列表
	 */
	public static String getTabooedWords(String content, String replaceto) {
		if (content == null || content.length() == 0) {
			return "";
		}
		String retcontent = content;
		List<String> badwordlist = tabooedTools.getTabooedWords(content);
		for (String badword : badwordlist) {
			retcontent = retcontent.replace(badword, replaceto);
		}
		return retcontent;
	}

	/**
	 * 对文本内容进行检查，验证是否存在敏感词汇。
	 * 
	 * @param content
	 *            需要进行检查的内容
	 * @return 如果存在敏感词汇返回 <code>true</code>，否则返回 <code>false</code>。
	 */
	public static boolean isTabooed(String content) {
		return !getTabooedWords(content).isEmpty();
	}

	/**
	 * 此方法可以实现在不重启JVM的情况下重新加载存放敏感词汇的资源文件。
	 */
	public static void reloadTabooedWords() {
		// logger.info("Reloading tabooed words resource...");
		tabooedTools.initialize();
	}

	/**
	 * 此方法用于加载应用程序外部的敏感词汇库，比如数据库里存储的词库（使用该方法后tabooed.words里面的词汇无效）
	 * 
	 * @param tabooedWords
	 */
	public static void setTabooedWordsResource(Collection<String> tabooedWords) {
		tabooedTools.setTabooedWords(tabooedWords);
	}

}
