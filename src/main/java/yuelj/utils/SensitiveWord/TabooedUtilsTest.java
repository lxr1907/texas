package yuelj.utils.SensitiveWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StopWatch;

import yuelj.utils.HttpTool;

/**
 * @author lxr
 */
public class TabooedUtilsTest {
	private static Logger logger = LogManager.getLogger(TabooedUtilsTest.class);
	public static void main(String[] args) {
		testGetTabooedWords();
	}

	public static void testGetTabooedWords() {
		String content = "DY 我靠 ，  我  靠， 我靠 18DY卖车办证我这些18test都很再擦倒萨，伟大撒旦撒台湾18DY电影 "
				+ "飞shit飞 have DY hav深咖啡阿斯顿发率快递费AV就Av历史aV库大房间 hav1av";
		String result = TabooedUtils.getTabooedWords(content, "**");
		//logger.info(result);
		System.out.println(result);
	}

	public static void testReloadTabooedWords() throws IOException {
		TabooedUtils.reloadTabooedWords();

		final StringBuilder sb = new StringBuilder();
		InputStream in = TabooedWords.class.getClassLoader().getResourceAsStream("tabooed_test_file.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			sb.append(line);
		}
		logger.info("File Length: " + sb.length());
		logger.info("File Content:\n" + sb);

		List<String> result = null;
		StopWatch stopWatch = new StopWatch("Tabooed Filter Test");
		stopWatch.start("getTabooedWords()");
		int n = 1000;
		for (int i = 0; i < n; i++) {
			result = TabooedUtils.getTabooedWords(sb.toString());
		}
		stopWatch.stop();

		logger.info(stopWatch.prettyPrint());
		logger.info(result);

		n = Runtime.getRuntime().availableProcessors() + 1;
		final CountDownLatch countDownLatch = new CountDownLatch(n);
		ExecutorService executor = Executors.newFixedThreadPool(n);

		for (int i = 0; i < n; i++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					StopWatch stopWatch = new StopWatch("Tabooed Filter Test(" + Thread.currentThread() + ")");
					stopWatch.start("getTabooedWords()");

					TabooedUtils.getTabooedWords(sb.toString());

					stopWatch.stop();
					logger.info(stopWatch.shortSummary());

					countDownLatch.countDown();
				}
			});
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executor.shutdown();
	}

	public static void testSetTabooedWordsResource() throws Exception {

		String content = "av 不是敏感词吧, 法轮功，abcabc，shit";
		StringBuilder sb = new StringBuilder();
		InputStream in = TabooedWords.class.getClassLoader().getResourceAsStream("tabooed_test_file.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			sb.append(line);
		}
		content = sb.toString();
		List<String> list = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < 1000; i++) {
			list.add("test" + i);
		}
		list.add("shit");
		list.add("smmm");
		list.add("abc");
		list.add("敏感词");
		list.add("法轮功");
		StopWatch stopWatch = new StopWatch("Test SetTabooedWordsResource");
		stopWatch.start("start SetTabooedWordsResource");
		TabooedUtils.setTabooedWordsResource(list);
		for (int i = 0; i < 1000; i++) {
			result = TabooedUtils.getTabooedWords(content);
		}
		stopWatch.stop();
		logger.info(stopWatch.prettyPrint());
		logger.info(result);

	}
}
