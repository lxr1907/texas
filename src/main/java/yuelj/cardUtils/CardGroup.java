package yuelj.cardUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class CardGroup {
	/**
	 * 获取0到52的list代表一副牌
	 * 
	 * @return
	 */
	public static ArrayList<Integer> getInitialCards() {
		ArrayList<Integer> cardsList = new ArrayList<Integer>(52);
		for (int i = 0; i < 52; i++) {
			cardsList.add(i);
		}
		return cardsList;
	}

	/**
	 * 获取随机卡组,范围0到52，0到3代表黑A红A梅A方A
	 * 
	 * @return
	 */
	public static ArrayList<Integer> getRandomCards() {
		ArrayList<Integer> src = getInitialCards();
		ArrayList<Integer> cardsList = new ArrayList<Integer>(52);
		while (src.size() > 0) {
			int size = src.size();
			Random r = new Random();
			int index = r.nextInt(size);

			cardsList.add(src.get(index));
			src.remove(index);
		}
		return cardsList;
	}

	public static ArrayList<Integer> getRandomCardsByReplace() {
		ArrayList<Integer> cardsList = getInitialCards();
		int one = 0;
		for (int i = 0; i < cardsList.size(); i++) {
			Random r = new Random();
			int index = r.nextInt(cardsList.size());
			one = cardsList.get(i);
			cardsList.set(i, cardsList.get(index));
			cardsList.set(index, one);
		}
		return cardsList;
	}

	public static void main(String[] args) {
		Date start = new Date();
		for (int i = 0; i < 4000000; i++) {
			getRandomCardsByReplace();
		}
		Date end = new Date();
		Date start2 = new Date();
		for (int i = 0; i < 4000000; i++) {
			getRandomCards();
		}
		Date end2 = new Date();

		System.out.println((end2.getTime() - start2.getTime()) / 1000);
		System.out.println((end.getTime() - start.getTime()) / 1000);

//		ArrayList<Integer> countList = new ArrayList<>();
//		for (int i = 0; i < 52; i++) {
//			countList.add(0);
//		}
//		for (int i = 0; i < 100000; i++) {
//			ArrayList<Integer> cardsList = getRandomCards();
//			List<Integer> p1cards = cardsList.subList(0, 3);
//			cardsList.remove(0);
//			cardsList.remove(0);
//			cardsList.remove(0);
//			List<Integer> pcards = cardsList.subList(0, 3);
//			countList.set(pcards.get(0), countList.get(pcards.get(0)) + 1);
//		}
//		Arrays.asList(countList).forEach(times -> {
//			System.out.println(times);
//		});
	}
}
