package yuelj.cardUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import yuelj.utils.logs.SystemLog;

public class CardUtil {
	public static String getType(int i) {
		String str = null;
		switch (i) {
		case 0:
			str = "D";// 方块
			break;
		case 1:
			str = "C";// 梅花
			break;
		case 2:
			str = "H";// 红桃
			break;
		case 3:
			str = "S";// 黑桃
			break;
		}
		return str;
	}

	/*
	 * 获得最大牌组，判断牌型并将牌型编号加在数组最后一位
	 */
	public static List<Integer> getMaxCardsGroup(List<Integer> list) {
		if (isThuaShun(list)) {// 同花顺
			getMaxThuaShun(list);

			if (list.get(0) - list.get(1) == -48) {
				list.add(10);// 判断级别
			} else {
				list.add(9);// 判断级别
			}
		} else if (isSiTiao(list)) {// 四条
			getMaxSiTiao(list);

			list.add(8);// 判断级别
		} else if (isHuLu(list)) {// 葫芦
			getMaxHuLu(list);

			list.add(7);// 判断级别
		} else if (isThua(list)) {// 同花
			getMaxThua(list);

			list.add(6);// 判断级别
		} else if (isShunZi_Card(list)) {// 顺子
			getMaxShunZi_Card(list);

			list.add(5);// 判断级别
		} else if (isSanTiao(list)) {// 三条
			getMaxSanTiao(list);

			list.add(4);// 判断级别
		} else if (isLDui(list)) {// 两对
			getMaxLDui(list);

			list.add(3);// 判断级别
		} else if (isDuiZi(list)) {// 一对
			getMaxDuiZi(list);

			list.add(2);// 判断级别
		} else {// 单张
			getMaxDZhang(list);

			list.add(1);// 判断级别
		}
		List<Integer> listnew = new ArrayList<Integer>();
		for (Integer i : list) {
			listnew.add(i);
		}
		return listnew;
	}

	/*
	 * List1,List2比较大小 List1>List2返回1、List1等于List2返回0、List1<List2返回-1
	 */
	public static Integer compareValue(List<Integer> listnew, List<Integer> listold) {
		// getMaxCardsGroup(list1);
		// getMaxCardsGroup(list2);
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		list1.addAll(listnew);
		list2.addAll(listold);

		if (list1.get(list1.size() - 1) - list2.get(list2.size() - 1) > 0) {
			return 1;
		}

		if (list1.get(list1.size() - 1) - list2.get(list2.size() - 1) < 0) {
			return -1;
		}

		if (list1.get(list1.size() - 1) - list2.get(list2.size() - 1) == 0) {
			for (int i = 0; i < 5; i++) {
				if (list1.get(i) / 4 == 0) {
					list1.set(i, 52);
				}
				if (list2.get(i) / 4 == 0) {
					list2.set(i, 52);
				}
			}
			switch (list1.get(list1.size() - 1)) {
			case 10:
				return 0;
			case 9:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0)
					return 0;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				break;
			case 8:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0) {
					if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
						return 1;
					if (list1.get(4) / 4 - list2.get(4) / 4 == 0)
						return 0;
					if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
						return -1;
				}
				break;
			case 7:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0) {
					if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
						return 1;
					if (list1.get(4) / 4 - list2.get(4) / 4 == 0)
						return 0;
					if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
						return -1;
				}
				break;
			case 6:
				if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
					return 1;
				if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
					return -1;
				if (list1.get(4) / 4 - list2.get(4) / 4 == 0) {
					if (list1.get(3) / 4 - list2.get(3) / 4 > 0)
						return 1;
					if (list1.get(3) / 4 - list2.get(3) / 4 < 0)
						return -1;
					if (list1.get(3) / 4 - list2.get(3) / 4 == 0) {
						if (list1.get(2) / 4 - list2.get(2) / 4 > 0)
							return 1;
						if (list1.get(2) / 4 - list2.get(2) / 4 < 0)
							return -1;
						if (list1.get(2) / 4 - list2.get(2) / 4 == 0) {
							if (list1.get(1) / 4 - list2.get(1) / 4 > 0)
								return 1;
							if (list1.get(1) / 4 - list2.get(1) / 4 < 0)
								return -1;
							if (list1.get(1) / 4 - list2.get(1) / 4 == 0) {
								if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
									return 1;
								if (list1.get(0) / 4 - list2.get(0) / 4 == 0)
									return 0;
								if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
									return -1;
							}
						}
					}
				}
				break;
			case 5:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0)
					return 0;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				break;
			case 4:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0) {
					if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
						return 1;
					if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
						return -1;
					if (list1.get(4) / 4 - list2.get(4) / 4 == 0) {
						if (list1.get(3) / 4 - list2.get(3) / 4 > 0)
							return 1;
						if (list1.get(3) / 4 - list2.get(3) / 4 == 0)
							return 0;
						if (list1.get(3) / 4 - list2.get(3) / 4 < 0)
							return -1;
					}
				}
				break;
			case 3:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0) {
					if (list1.get(2) / 4 - list2.get(2) / 4 > 0)
						return 1;
					if (list1.get(2) / 4 - list2.get(2) / 4 < 0)
						return -1;
					if (list1.get(2) / 4 - list2.get(2) / 4 == 0) {
						if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
							return 1;
						if (list1.get(4) / 4 - list2.get(4) / 4 == 0)
							return 0;
						if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
							return -1;
					}
				}
				break;
			case 2:
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
					return 1;
				if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
					return -1;
				if (list1.get(0) / 4 - list2.get(0) / 4 == 0) {
					if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
						return 1;
					if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
						return -1;
					if (list1.get(4) / 4 - list2.get(4) / 4 == 0) {
						if (list1.get(3) / 4 - list2.get(3) / 4 > 0)
							return 1;
						if (list1.get(3) / 4 - list2.get(3) / 4 < 0)
							return -1;
						if (list1.get(3) / 4 - list2.get(3) / 4 == 0) {
							if (list1.get(2) / 4 - list2.get(2) / 4 > 0)
								return 1;
							if (list1.get(2) / 4 - list2.get(2) / 4 == 0)
								return 0;
							if (list1.get(2) / 4 - list2.get(2) / 4 < 0)
								return -1;
						}
					}
				}
				break;
			case 1:
				if (list1.get(4) / 4 - list2.get(4) / 4 > 0)
					return 1;
				if (list1.get(4) / 4 - list2.get(4) / 4 < 0)
					return -1;
				if (list1.get(4) / 4 - list2.get(4) / 4 == 0) {
					if (list1.get(3) / 4 - list2.get(3) / 4 > 0)
						return 1;
					if (list1.get(3) / 4 - list2.get(3) / 4 < 0)
						return -1;
					if (list1.get(3) / 4 - list2.get(3) / 4 == 0) {
						if (list1.get(2) / 4 - list2.get(2) / 4 > 0)
							return 1;
						if (list1.get(2) / 4 - list2.get(2) / 4 < 0)
							return -1;
						if (list1.get(2) / 4 - list2.get(2) / 4 == 0) {
							if (list1.get(1) / 4 - list2.get(1) / 4 > 0)
								return 1;
							if (list1.get(1) / 4 - list2.get(1) / 4 < 0)
								return -1;
							if (list1.get(1) / 4 - list2.get(1) / 4 == 0) {
								if (list1.get(0) / 4 - list2.get(0) / 4 > 0)
									return 1;
								if (list1.get(0) / 4 - list2.get(0) / 4 == 0)
									return 0;
								if (list1.get(0) / 4 - list2.get(0) / 4 < 0)
									return -1;
							}
						}
					}
				}
				break;
			}
		}
		return null;
	}

	/*
	 * 从小到大排列
	 */
	public static List<Integer> getOrderList(List<Integer> list) {
		int tmp = 0;
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(j) < list.get(i)) {
					tmp = list.get(i);
					list.set(i, list.get(j));
					list.set(j, tmp);
				}
			}
		}
		return list;
	}

	/*
	 * 判断是否是顺子_通用
	 */
	public static boolean isShunZi(List<Integer> list) {
		List<Integer> maxList = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++) {
			maxList.add(i + 10);
		}
		maxList.add(1);

		if (list.containsAll(maxList)) {
			return true;
		} else {
			maxList.clear();
			for (int a = list.size() - 1; a >= 0; a--) {
				int v = list.get(a);
				maxList.clear();
				for (int j = 0; j < 5; j++, v--) {
					maxList.add(v);
				}
				if (list.containsAll(maxList)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 得到最大的顺子_通用(参数List需从小到大排好序)
	 */
	public static List<Integer> getMaxShunZi(List<Integer> list) {
		List<Integer> maxList = new ArrayList<Integer>();

		maxList.add(1);
		for (int i = 0; i < 4; i++) {
			maxList.add(13 - i);
		}

		if (!list.containsAll(maxList)) {
			maxList.clear();
			for (int a = list.size() - 1; a >= 0; a--) {
				int v = list.get(a);
				maxList.clear();
				for (int j = 0; j < 5; j++, v--) {
					maxList.add(v);
				}
				if (list.containsAll(maxList)) {
					break;
				}
			}
		}
		return maxList;
	}

	// --------------------------------------------------------------------------
	/*
	 * 判断是否是同花顺
	 */
	public static boolean isThuaShun(List<Integer> list) {
		List<Integer> list1 = new ArrayList<Integer>();// <方块>
		List<Integer> list2 = new ArrayList<Integer>();// <梅花>
		List<Integer> list3 = new ArrayList<Integer>();// <红桃>
		List<Integer> list4 = new ArrayList<Integer>();// <黑桃>

		for (int i = 0; i < list.size(); i++) {
			switch (list.get(i) % 4 + 1) {
			case 1:
				list1.add(list.get(i) / 4 + 1);
				break;
			case 2:
				list2.add(list.get(i) / 4 + 1);
				break;
			case 3:
				list3.add(list.get(i) / 4 + 1);
				break;
			case 4:
				list4.add(list.get(i) / 4 + 1);
				break;
			}
		}
		if (list1.size() >= 5) {
			getOrderList(list1);
			if (isShunZi(list1)) {
				return true;
			}
		} else if (list2.size() >= 5) {
			getOrderList(list2);
			if (isShunZi(list2)) {
				return true;
			}
		} else if (list3.size() >= 5) {
			getOrderList(list3);
			if (isShunZi(list3)) {
				return true;
			}
		} else if (list4.size() >= 5) {
			getOrderList(list4);
			if (isShunZi(list4)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 得到最大同花顺
	 */
	public static void getMaxThuaShun(List<Integer> list) {
		List<Integer> list1 = new ArrayList<Integer>();// <方块>
		List<Integer> list2 = new ArrayList<Integer>();// <梅花>
		List<Integer> list3 = new ArrayList<Integer>();// <红桃>
		List<Integer> list4 = new ArrayList<Integer>();// <黑桃>

		for (int i = 0; i < list.size(); i++) {
			switch (list.get(i) % 4 + 1) {
			case 1:
				list1.add(list.get(i) / 4 + 1);
				break;
			case 2:
				list2.add(list.get(i) / 4 + 1);
				break;
			case 3:
				list3.add(list.get(i) / 4 + 1);
				break;
			case 4:
				list4.add(list.get(i) / 4 + 1);
				break;
			}
		}

		list.clear();
		if (list1.size() >= 5) {
			getOrderList(list1);
			for (int i : getMaxShunZi(list1)) {
				list.add((i - 1) * 4);
			}
		}
		if (list2.size() >= 5) {
			getOrderList(list2);
			for (int i : getMaxShunZi(list2)) {
				list.add((i - 1) * 4 + 1);
			}
		}
		if (list3.size() >= 5) {
			getOrderList(list3);
			for (int i : getMaxShunZi(list3)) {
				list.add((i - 1) * 4 + 2);
			}
		}
		if (list4.size() >= 5) {
			getOrderList(list4);
			for (int i : getMaxShunZi(list4)) {
				list.add((i - 1) * 4 + 3);
			}
		}
	}

	// ----------------------------------------------------------------
	/*
	 * 判断是否是四条
	 */
	public static boolean isSiTiao(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		getOrderList(list);

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 3) {
				return true;
			}
		}

		return false;
	}

	/*
	 * 得到最大的四条
	 */
	public static void getMaxSiTiao(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		getOrderList(list);

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 3) {
				valueList.clear();
				valueList.addAll(list.subList(i, i + 4));
				list.removeAll(valueList);
				break;
			}
		}

		if (list.get(0) / 4 == 0) {
			valueList.add(list.get(0));
		} else {
			valueList.add(list.get(list.size() - 1));
		}

		list.clear();
		for (int i : valueList) {
			list.add(i);
		}

	}

	// ----------------------------------------------------------------
	/*
	 * 判断是否是葫芦
	 */
	public static boolean isHuLu(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 2) {
				ls.addAll(valueList.subList(i, i + 3));
				valueList.removeAll(ls);
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int i = 0; i < valueList.size(); i++) {
				if (valueList.lastIndexOf(valueList.get(i)) - i >= 1) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 得到最大葫芦
	 */
	public static void getMaxHuLu(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) / 4 == 0) {
				valueList.add(14);
			} else {
				valueList.add(list.get(i) / 4 + 1);
			}
		}
		getOrderList(valueList);

		for (int i = valueList.size() - 1; i >= 0; i--) {
			if (i - valueList.indexOf(valueList.get(i)) == 2) {
				ls.addAll(valueList.subList(i - 2, i + 1));
				valueList.removeAll(ls);
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int i = valueList.size() - 1; i >= 0; i--) {
				if (i - valueList.indexOf(valueList.get(i)) >= 1) {
					ls.addAll(valueList.subList(i - 1, i + 1));
					break;
				}
			}

			valueList.clear();
			for (int i = 0; i < ls.size(); i++) {
				if (ls.get(i) == 14) {
					ls.set(i, 1);
				}
				valueList.add(ls.get(i));
			}

			Iterator<Integer> iter = list.iterator();
			while (iter.hasNext()) {
				int value = iter.next() / 4 + 1;
				if (ls.contains(value)) {
					for (int j = 0; j < ls.size(); j++) {
						if (ls.get(j) == value) {
							ls.set(j, 0);
							break;
						}
					}
					continue;
				} else {
					iter.remove();
				}
			}
		}
		inOrderList(list, valueList);
	}

	// -------------------------------------------------------
	/*
	 * 判断是否是同花
	 */
	public static boolean isThua(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) % 4 + 1);
		}

		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i >= 4) {
				return true;
			}
		}

		return false;
	}

	/*
	 * 得到最大同花
	 */
	public static void getMaxThua(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		int value = 0;

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) % 4 + 1);
		}

		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i >= 4) {
				value = valueList.get(i);
				break;
			}
		}

		Iterator<Integer> iter = list.iterator();
		while (iter.hasNext()) {
			if (iter.next() % 4 + 1 != value) {
				iter.remove();
			}
		}

		getOrderList(list);
		if (list.get(0) / 4 == 0) {
			list.add(list.get(0));
		}

		list.subList(0, list.size() - 5).clear();

	}

	// ---------------------------------------------------------------
	/*
	 * 判断是否是顺子
	 */
	public static boolean isShunZi_Card(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}

		return isShunZi(valueList);
	}

	/*
	 * 得到最大顺子
	 */
	public static void getMaxShunZi_Card(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		valueList = getMaxShunZi(valueList);
		for (int i : valueList) {
			ls.add(i);
		}

		Iterator<Integer> iter = list.iterator();
		while (iter.hasNext()) {
			int value = iter.next() / 4 + 1;
			if (valueList.contains(value)) {
				for (int j = 0; j < valueList.size(); j++) {
					if (valueList.get(j) == value) {
						valueList.set(j, 0);
						break;
					}
				}
				continue;
			} else {
				iter.remove();
			}
		}

		inOrderList(list, ls);
	}

	// -----------------------------------------------
	/*
	 * 判断是否是三条
	 */
	public static boolean isSanTiao(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 2) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 得到最大三条
	 */
	public static void getMaxSanTiao(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) / 4 == 0) {
				valueList.add(14);
			} else {
				valueList.add(list.get(i) / 4 + 1);
			}
		}
		getOrderList(valueList);

		for (int i = valueList.size() - 1; i >= 0; i--) {
			if (i - valueList.indexOf(valueList.get(i)) == 2) {
				ls.addAll(valueList.subList(i - 2, i + 1));
				valueList.removeAll(ls);
				break;
			}
		}

		ls.addAll(valueList.subList(valueList.size() - 2, valueList.size()));

		valueList.clear();
		for (int i = 0; i < ls.size(); i++) {
			if (ls.get(i) == 14) {
				ls.set(i, 1);
			}
			valueList.add(ls.get(i));
		}

		Iterator<Integer> iter = list.iterator();
		while (iter.hasNext()) {
			int value = iter.next() / 4 + 1;
			if (ls.contains(value)) {
				for (int j = 0; j < ls.size(); j++) {
					if (ls.get(j) == value) {
						ls.set(j, 0);
						break;
					}
				}
				continue;
			} else {
				iter.remove();
			}
		}

		inOrderList(list, valueList);
	}

	// -----------------------------------------------------
	/*
	 * 判断是否是两对
	 */
	public static boolean isLDui(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 1) {
				ls.addAll(valueList.subList(i, i + 2));
				valueList.removeAll(ls);
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int i = 0; i < valueList.size(); i++) {
				if (valueList.lastIndexOf(valueList.get(i)) - i >= 1) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 得到最大两对
	 */
	public static void getMaxLDui(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) / 4 == 0) {
				valueList.add(14);
			} else {
				valueList.add(list.get(i) / 4 + 1);
			}
		}
		getOrderList(valueList);

		for (int i = valueList.size() - 1; i >= 0; i--) {
			if (i - valueList.indexOf(valueList.get(i)) == 1) {
				ls.addAll(valueList.subList(i - 1, i + 1));
				valueList.removeAll(ls);
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int i = valueList.size() - 1; i >= 0; i--) {
				if (i - valueList.indexOf(valueList.get(i)) >= 1) {
					ls.addAll(valueList.subList(i - 1, i + 1));
					valueList.removeAll(ls);
					break;
				}
			}

			ls.add(valueList.get(valueList.size() - 1));

			valueList.clear();
			for (int i = 0; i < ls.size(); i++) {
				if (ls.get(i) == 14) {
					ls.set(i, 1);
				}
				valueList.add(ls.get(i));
			}

			Iterator<Integer> iter = list.iterator();
			while (iter.hasNext()) {
				int value = iter.next() / 4 + 1;
				if (ls.contains(value)) {
					for (int j = 0; j < ls.size(); j++) {
						if (ls.get(j) == value) {
							ls.set(j, 0);
							break;
						}
					}
					continue;
				} else {
					iter.remove();
				}
			}
		}

		inOrderList(list, valueList);
	}

	// --------------------------------------------------------
	/*
	 * 判断是否是一对
	 */
	public static boolean isDuiZi(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		getOrderList(valueList);

		for (int i = 0; i < valueList.size(); i++) {
			if (valueList.lastIndexOf(valueList.get(i)) - i == 1) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 得到最大对子
	 */
	public static void getMaxDuiZi(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> ls = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) / 4 == 0) {
				valueList.add(14);
			} else {
				valueList.add(list.get(i) / 4 + 1);
			}
		}
		getOrderList(valueList);

		for (int i = valueList.size() - 1; i >= 0; i--) {
			if (i - valueList.indexOf(valueList.get(i)) == 1) {
				ls.addAll(valueList.subList(i - 1, i + 1));
				valueList.removeAll(ls);
				break;
			}
		}
		if (valueList.size() >= 3) {
			ls.addAll(valueList.subList(valueList.size() - 3, valueList.size()));
		} else {
			ls.addAll(valueList);
		}

		valueList.clear();
		for (int i = 0; i < ls.size(); i++) {
			if (ls.get(i) == 14) {
				ls.set(i, 1);
			}
			valueList.add(ls.get(i));
		}

		Iterator<Integer> iter = list.iterator();
		while (iter.hasNext()) {
			int value = iter.next() / 4 + 1;
			if (ls.contains(value)) {
				for (int j = 0; j < ls.size(); j++) {
					if (ls.get(j) == value) {
						ls.set(j, 0);
						break;
					}
				}
				continue;
			} else {
				iter.remove();
			}
		}

		inOrderList(list, valueList);
	}

	// --------------------------------------------------
	/*
	 * 得到最大单张
	 */
	public static void getMaxDZhang(List<Integer> list) {
		getOrderList(list);
		if (list.get(0) / 4 == 0) {
			list.add(list.get(0));
		}
		if (list.size() >= 5) {
			list.subList(0, list.size() - 5).clear();
		}
	}

	/*
	 * 将List按照指定的顺序排列
	 */
	public static List<Integer> inOrderList(List<Integer> list, List<Integer> valueList) {
		List<Integer> tempList = new ArrayList<Integer>();

		for (int i : valueList) {
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) / 4 + 1 == i && !tempList.contains(list.get(j))) {
					tempList.add(list.get(j));
					break;
				}
			}
		}
		list.clear();
		for (int i : tempList) {
			list.add(i);
		}

		return list;
	}

	public static void main(String[] args) {
		List<Integer> listnew = new ArrayList<>();
		int[] newcards = { 40, 39, 32, 28, 26, 5 };
		for (int i = 0; i < newcards.length; i++) {
			listnew.add(newcards[i]);
		}
		List<Integer> listold = new ArrayList<>();
		int[] oldcards = { 12, 14, 39, 40, 51, 2 };
		for (int i = 0; i < oldcards.length; i++) {
			listold.add(oldcards[i]);
		}
		Integer result = compareValue(listnew, listold);
		SystemLog.printlog(result);
	}
}