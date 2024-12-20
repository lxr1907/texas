package com.lxrtalk.texas.cardUtils;

import java.util.ArrayList;
import java.util.List;

public class ThreeCardCompareUtil {
	/**
	 * 获取欢乐拼三张的牌型，写在数组最后一位
	 * 
	 * @param list
	 * @return
	 */
	public static List<Integer> getCardsGroupType(List<Integer> listOld) {
		List<Integer> list = new ArrayList<Integer>();
		for (Integer i : listOld) {
			list.add(i);
		}
		if (isThuaShun(list)) {
			// 同花顺9
			list.add(9);
		} else if (isThua(list)) {
			// 同花6
			list.add(6);
		} else if (isShunZi_Card(list)) {
			// 顺子7
			list.add(7);
		} else if (isSanTiao(list)) {
			// 豹子三条10
			list.add(10);
		} else if (isDuiZi(list)) {
			// 一对3
			list.add(3);
			// } else if (isSpecial235(list)) {
			// 235
			// list.add(1);
		} else {
			// 单张2
			list.add(2);
		}
		return list;
	}

	/**
	 * 判断欢乐拼三张牌型大小，
	 * 
	 * @param listnew
	 * @param listold
	 * @return 返回1，listnew>listold,返回0，listnew=listold,返回-1，listnew<listold
	 */
	public static Integer compareValue(List<Integer> listnew, List<Integer> listold) {
		// 判断牌型并将牌型编号加在数组最后一位
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		list1.addAll(listnew);
		list2.addAll(listold);
		// 现根据牌型比较
		int type1 = list1.get(list1.size() - 1);
		int type2 = list2.get(list2.size() - 1);
		int typeMinus = type1 - type2;
		if (typeMinus > 0) {
			return 1;
		}
		if (typeMinus < 0) {
			return -1;
		}
		// 若牌型相同
		if (typeMinus == 0) {
			// 将A的值从0.1.2.3设为52
			for (int i = 0; i < 3; i++) {
				if (list1.get(i) / 4 == 0) {
					list1.set(i, 52);
				}
				if (list2.get(i) / 4 == 0) {
					list2.set(i, 52);
				}
			}
			// 去除牌型标志并排序
			list1 = list1.subList(0, 3);
			list1 = getOrderList(list1);
			list2 = list2.subList(0, 3);
			list2 = getOrderList(list2);
			switch (type1) {
			case 9:// 同花顺
				return getCompareFirstCard(list1, list2);
			case 7:// 顺子
				return getCompareFirstCard(list1, list2);
			case 6:// 同花
				return getCompareOneByOne(list1, list2);
			case 10:// 三条
				return getCompareFirstCard(list1, list2);
			case 3:// 一对
					// 对子牌的值
				int doubleMinus = getDoubleCard(list1) / 4 - getDoubleCard(list2) / 4;
				if (doubleMinus > 0)
					return 1;
				else if (doubleMinus < 0)
					return -1;
				else {
					int singleMinus = getSingleCard(list1) / 4 - getSingleCard(list2) / 4;
					if (singleMinus > 0)
						return 1;
					else if (singleMinus < 0)
						return -1;
					else {
						return 0;
					}
				}
			case 2:// 除235外的普通牌
				return getCompareOneByOne(list1, list2);
			// case 1:// 除同花的235
			// return 0;
			}
		}
		return null;
	}

	/**
	 * 若传入带有对子的3张牌，返回对子的值
	 * 
	 * @param list1
	 * @return
	 */
	public static int getDoubleCard(List<Integer> list1) {
		if (list1.get(0) == list1.get(1) || list1.get(0) == list1.get(2)) {
			return list1.get(0);
		} else {
			return list1.get(1);
		}
	}

	/**
	 * 若传入带有对子的3张牌，返回单张的牌
	 * 
	 * @param list1
	 * @return
	 */
	public static int getSingleCard(List<Integer> list1) {
		if (list1.get(0) == list1.get(1)) {
			return list1.get(2);
		} else if (list1.get(0) == list1.get(2)) {
			return list1.get(1);
		} else {
			return list1.get(0);
		}
	}

	/**
	 * 对比第一张牌，返回列表大小
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static int getCompareFirstCard(List<Integer> list1, List<Integer> list2) {
		int minus = list1.get(0) / 4 - list2.get(0) / 4;
		if (minus > 0)
			return 1;
		else if (minus < 0)
			return -1;
		else
			return 0;
	}

	/**
	 * 一张张对比，返回列表大小
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static int getCompareOneByOne(List<Integer> list1, List<Integer> list2) {
		if (list1.get(2) / 4 - list2.get(2) / 4 > 0) {
			return 1;
		} else if (list1.get(2) / 4 - list2.get(2) / 4 < 0) {
			return -1;
		} else {
			if (list1.get(1) / 4 - list2.get(1) / 4 > 0) {
				return 1;
			} else if (list1.get(1) / 4 - list2.get(1) / 4 < 0) {
				return -1;
			} else {
				if (list1.get(0) / 4 - list2.get(0) / 4 > 0) {
					return 1;
				} else if (list1.get(0) / 4 - list2.get(0) / 4 < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	/**
	 * 非同花色的235，比豹子大
	 * 
	 * @param list
	 * @return
	 */
	// public static boolean isSpecial235(List<Integer> list) {
	// if (list.get(0) / 4 == 1 && list.get(1) / 4 == 2 && list.get(2) / 4 == 4)
	// {
	// return true;
	// }
	// return false;
	// }

	/*
	 * 根据1到13的牌数字，判断是否是顺子
	 */
	public static boolean isShunZi(List<Integer> list) {
		List<Integer> maxList = new ArrayList<Integer>();
		//Q,K
		for (int i = 2; i < 4; i++) {
			maxList.add(i + 10);
		}
		//A
		maxList.add(1);

		if (list.containsAll(maxList)) {
			return true;
		} else {
			maxList.clear();
			for (int a = list.size() - 1; a >= 0; a--) {
				int v = list.get(a);
				maxList.clear();
				for (int j = 0; j < 3; j++, v--) {
					maxList.add(v);
				}
				if (list.containsAll(maxList)) {
					return true;
				}
			}
		}
		return false;
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
		if (list1.size() >= 3) {
			getOrderList(list1);
			if (isShunZi(list1)) {
				return true;
			}
		} else if (list2.size() >= 3) {
			getOrderList(list2);
			if (isShunZi(list2)) {
				return true;
			}
		} else if (list3.size() >= 3) {
			getOrderList(list3);
			if (isShunZi(list3)) {
				return true;
			}
		} else if (list4.size() >= 3) {
			getOrderList(list4);
			if (isShunZi(list4)) {
				return true;
			}
		}
		return false;
	}

	// -------------------------------------------------------
	/*
	 * 判断是否是同花
	 */
	public static boolean isThua(List<Integer> list) {
		if ((list.get(0) % 4) == (list.get(1) % 4) && (list.get(1) % 4) == (list.get(2) % 4)) {
			return true;
		}
		return false;
	}

	// ---------------------------------------------------------------
	/*
	 * 判断是否是顺子
	 */
	public static boolean isShunZi_Card(List<Integer> list) {
		List<Integer> valueList = new ArrayList<Integer>();
		// 全部0到51不分花色，转换为1到13
		for (int i = 0; i < list.size(); i++) {
			valueList.add(list.get(i) / 4 + 1);
		}
		return isShunZi(valueList);
	}

	// -----------------------------------------------
	/*
	 * 判断是否是三条
	 */
	public static boolean isSanTiao(List<Integer> list) {
		if ((list.get(0) / 4) == (list.get(1) / 4) && (list.get(1) / 4) == (list.get(2) / 4)) {
			return true;
		}
		return false;
	}

	// --------------------------------------------------------
	/*
	 * 判断是否是一对
	 */
	public static boolean isDuiZi(List<Integer> list) {
		if ((list.get(0) / 4) == (list.get(1) / 4) || (list.get(1) / 4) == (list.get(2) / 4)
				|| (list.get(0) / 4) == (list.get(2) / 4)) {
			return true;
		}
		return false;
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
	public static void main(String[] args) {
		List<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(13);
		list.add(12);
		System.out.println(isShunZi(list));;
	}
}
