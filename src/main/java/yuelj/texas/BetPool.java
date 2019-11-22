package yuelj.texas;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import yuelj.entity.Player;

/**
 * 奖池计算
 * 
 * @author lixiaoran
 *
 */
public class BetPool {
	/**
	 * 该分池总金额
	 */
	@Expose
	private long betSum;
	/**
	 * 该分池的玩家列表
	 */
	@Expose
	private List<Player> betPlayerList = new ArrayList<>();

	public long getBetSum() {
		return betSum;
	}

	public void setBetSum(long betSum) {
		this.betSum = betSum;
	}

	public List<Player> getBetPlayerList() {
		return betPlayerList;
	}

	public void setBetPlayerList(List<Player> betPlayerList) {
		this.betPlayerList = betPlayerList;
	}
}
