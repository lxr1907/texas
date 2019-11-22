package yuelj.cardUtils;

/**
 * 概率对象
 * 
 * @author lixiaoran
 *
 */
public class ProbabilityEntity {
	public ProbabilityEntity() {
	}

	public ProbabilityEntity(int weightVal) {
		this.weight = weightVal;
	}

	/**
	 * 概率值
	 */
	private double probability;

	/**
	 * 权重
	 */
	private int weight;

	/**
	 * 中签后的返回结果
	 */
	private int result;

	/**
	 * 期望中签间隔次数
	 */
	private double mean;

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
}
