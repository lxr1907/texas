package yuelj.cardUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import yuelj.utils.logs.SystemLog;

/**
 * 伪随机算法
 * 
 * @author lixiaoran
 *
 */
public class PRandom {
	public static void main(String[] args) {
		long start = new Date().getTime();
		generateListTest();
		long end = new Date().getTime();
		SystemLog.printlog(end - start);
	}

	private static void generateListTest() {
		int[] resultConfigList = { 16000, 24000, 32000, 40000, 64000, 80000, 120000, 160000, 200000, 400000 };
		float[] probList = { 0.71535f, 0.1477f, 0.0589f, 0.0368f, 0.02f, 0.01f, 0.00488f, 0.00316f, 0.00216f,
				0.00105f };
		ArrayList<ProbabilityEntity> probabilityList = new ArrayList<>();
		for (int i = 0; i < resultConfigList.length; i++) {
			ProbabilityEntity p = new ProbabilityEntity();
			p.setResult(resultConfigList[i]);
			p.setProbability(probList[i]);
			probabilityList.add(p);
		}
		// calculateProbabilityByWeight(probabilityList);
		ArrayList<Integer> resultList = getRandmValueList(80000, probabilityList);
		// for (int i : resultList) {
		// SystemLog.printlog(i);
		// }
		ArrayList<Integer> timesList = new ArrayList<>(10);
		for (int i = 0; i < 10; i++) {
			timesList.add(i, 0);
		}
		for (int i : resultList) {
			timesList.set(i, timesList.get(i) + 1);
		}
		for (int i : timesList) {
			SystemLog.printlog(i);
		}
		for (ProbabilityEntity i : probabilityList) {
			SystemLog.printlog(i.getProbability());
		}
	}

	/**
	 * 生成正态分布的值列表
	 * 
	 * @param length
	 *            值列表长度
	 * @param probabilityList
	 *            概率值，中签返回值列表
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Integer> getRandmValueList(int length, ArrayList<ProbabilityEntity> probabilityList) {
		ArrayList<Integer> resultList = new ArrayList<Integer>(length);
		// 根据正态分布生成概率列表p
		ArrayList<Double> p = new ArrayList<Double>();
		for (ProbabilityEntity e : probabilityList) {
			// 求期望分布间隔
			double mean = 1.0 / e.getProbability();
			double ef = getNumberInNormalDistribution(mean, mean / 3.0);
			p.add(ef);
		}
		// 生成的结果集总长度length
		for (int i = 0; i < length; i++) {
			// 最小概率值
			double minp = 9999999999f;
			// 索引
			int minj = -1;
			// 找到最小概率
			for (int j = 0; j < p.size(); j++) {
				if (p.get(j) < minp) {
					minp = p.get(j);
					minj = j;
				}
			}
			// 生成一条结果
			resultList.add(minj);
			// 对p进行变换
			for (int j = 0; j < p.size(); j++) {
				double newpVal = p.get(j) - minp;
				p.set(j, newpVal);
			}
			// 对p中最小概率重新正态分布随机
			double minmean = 1.0 / probabilityList.get(minj).getProbability();
			double newMean = getNumberInNormalDistribution(minmean, minmean / 3);
			p.set(minj, newMean);
		}
		return resultList;
	}

	/**
	 * 根据权重计算概率
	 */
	private static void calculateProbabilityByWeight(ArrayList<ProbabilityEntity> probabilityList) {
		double weightSum = 0;
		// 计算总权重
		for (ProbabilityEntity e : probabilityList) {
			weightSum = weightSum + e.getWeight();
		}
		// 计算每种情况的概率
		for (ProbabilityEntity e : probabilityList) {
			e.setProbability(e.getWeight() / weightSum);
		}
		for (ProbabilityEntity e : probabilityList) {
			e.setMean(1 / e.getProbability());
		}
	}

	/**
	 * 正态分布模拟函数
	 * 
	 * @param mean
	 *            期望分布间隔
	 * @param std_dev
	 *            标准差
	 * @return 数组下标
	 */
	public static double getNumberInNormalDistribution(double mean, double std_dev) {
		return mean + uniform2NormalDistribution() * std_dev;
	}

	/**
	 * 供正态分布模拟使用的偏差计算
	 * 
	 * @return
	 */
	public static double uniform2NormalDistribution() {
		double sum = 0;
		Random r = new Random();
		for (int i = 0; i < 12; i++) {
			sum = sum + r.nextDouble();
		}
		return sum - 6.0;
	}
}
