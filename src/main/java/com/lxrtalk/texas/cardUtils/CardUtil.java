package com.lxrtalk.texas.cardUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CardUtil {
    private static final Logger logger = LogManager.getLogger(CardUtil.class);
    // 牌型枚举
    public enum HandRank {
        HIGH_CARD(0), PAIR(1), TWO_PAIRS(2), THREE_OF_A_KIND(3), STRAIGHT(4), FLUSH(5),
        FULL_HOUSE(6), FOUR_OF_A_KIND(7), STRAIGHT_FLUSH(8), ROYAL_FLUSH(9);

        private final int rank;

        HandRank(int rank) {
            this.rank = rank;
        }

        public int getRank() {
            return rank;
        }
    }

    // 获取最大牌型的5张牌
    public static List<Integer> getMaxHand(List<Integer> cards) {
        if (cards.size() != 7) {
            throw new IllegalArgumentException("输入的牌数必须为7张");
        }

        Map<HandRank, List<Integer>> handRanks = new HashMap<>();
        for (HandRank rank : HandRank.values()) {
            handRanks.put(rank, new ArrayList<>());
        }

        // 生成所有可能的5张牌组合
        List<List<Integer>> combinations = generateCombinations(cards, 5);
        for (List<Integer> combination : combinations) {
            HandRank rank = evaluateHand(combination);
            handRanks.get(rank).add(getHandValue(combination));
        }

        // 找出最高牌型
        for (HandRank rank : HandRank.values()) {
            List<Integer> hands = handRanks.get(rank);
            if (!hands.isEmpty()) {
                int maxHandValue = Collections.max(hands);
                for (List<Integer> combination : combinations) {
                    if (evaluateHand(combination) == rank && getHandValue(combination) == maxHandValue) {
                        return combination;
                    }
                }
            }
        }

        throw new IllegalStateException("无法找到最大牌型");
    }

    // 比较两个牌型的大小
    public static int compareHands(List<Integer> hand1, List<Integer> hand2) {
        HandRank rank1 = evaluateHand(hand1);
        HandRank rank2 = evaluateHand(hand2);

        if (rank1.getRank() != rank2.getRank()) {
            return Integer.compare(rank1.getRank(), rank2.getRank());
        }

        int value1 = getHandValue(hand1);
        int value2 = getHandValue(hand2);

        return Integer.compare(value1, value2);
    }

    // 生成所有可能的5张牌组合
    private static List<List<Integer>> generateCombinations(List<Integer> cards, int k) {
        List<List<Integer>> combinations = new ArrayList<>();
        generateCombinationsHelper(cards, k, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<Integer> cards, int k, int start, List<Integer> current, List<List<Integer>> combinations) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, k, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }

    // 评估牌型
    private static HandRank evaluateHand(List<Integer> hand) {
        Map<Integer, Integer> valueCounts = new HashMap<>();
        Map<Integer, Integer> suitCounts = new HashMap<>();

        for (int card : hand) {
            int value = card % 13;
            int suit = card / 13;
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }

        boolean isStraight = isStraight(hand);
        boolean isFlush = isFlush(suitCounts);

        if (isStraight && isFlush) {
            if (Collections.min(hand) % 13 == 8) { // 10, J, Q, K, A
                return HandRank.ROYAL_FLUSH;
            } else {
                return HandRank.STRAIGHT_FLUSH;
            }
        }

        if (valueCounts.containsValue(4)) {
            return HandRank.FOUR_OF_A_KIND;
        }

        if (valueCounts.containsValue(3) && valueCounts.containsValue(2)) {
            return HandRank.FULL_HOUSE;
        }

        if (isFlush) {
            return HandRank.FLUSH;
        }

        if (isStraight) {
            return HandRank.STRAIGHT;
        }

        if (valueCounts.containsValue(3)) {
            return HandRank.THREE_OF_A_KIND;
        }

        if (valueCounts.containsValue(2)) {
            int pairs = 0;
            for (int count : valueCounts.values()) {
                if (count == 2) {
                    pairs++;
                }
            }
            if (pairs == 2) {
                return HandRank.TWO_PAIRS;
            } else {
                return HandRank.PAIR;
            }
        }

        return HandRank.HIGH_CARD;
    }

    // 计算牌型的值
    private static int getHandValue(List<Integer> hand) {
        List<Integer> values = new ArrayList<>();
        for (int card : hand) {
            int value = card % 13;
            if (value == 0) {
                value = 13; // A
            }
            values.add(value);
        }
        Collections.sort(values, Collections.reverseOrder());

        return values.stream().reduce(0, (a, b) -> a * 100 + b);
    }

    // 判断是否为顺子
    private static boolean isStraight(List<Integer> hand) {
        List<Integer> values = new ArrayList<>();
        for (int card : hand) {
            int value = card % 13;
            if (value == 0) {
                value = 13; // A
            }
            values.add(value);
        }
        Collections.sort(values);

        if (values.get(0) == 1 && values.get(1) == 10 && values.get(2) == 11 && values.get(3) == 12 && values.get(4) == 13) {
            return true; // A, 10, J, Q, K
        }

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) != values.get(i - 1) + 1) {
                return false;
            }
        }

        return true;
    }

    // 判断是否为同花
    private static boolean isFlush(Map<Integer, Integer> suitCounts) {
        for (int count : suitCounts.values()) {
            if (count >= 5) {
                return true;
            }
        }
        return false;
    }
    // 将牌的整数表示转换为字符串表示
    public static String cardToString(int card) {
        String[] suits = {"黑", "红", "梅", "方"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        int suit = card / 13;
        int rank = card % 13;
        return suits[suit] + ranks[rank];
    }

    // 将字符串表示的牌转换为整数表示
    public static int stringToCard(String cardStr) {
        String[] suits = {"黑", "红", "梅", "方"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        String suitStr = cardStr.substring(0, 1);
        String rankStr = cardStr.substring(1);

        int suit = -1;
        for (int i = 0; i < suits.length; i++) {
            if (suits[i].equals(suitStr)) {
                suit = i;
                break;
            }
        }

        int rank = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(rankStr)) {
                rank = i;
                break;
            }
        }

        if (suit == -1 || rank == -1) {
            throw new IllegalArgumentException("无效的牌字符串: " + cardStr);
        }

        return suit * 13 + rank;
    }

    // 将整数列表转换为字符串列表
    public static List<String> cardsToStringList(List<Integer> cards) {
        List<String> result = new ArrayList<>();
        for (int card : cards) {
            result.add(cardToString(card));
        }
        return result;
    }

    // 将字符串列表转换为整数列表
    public static List<Integer> stringsToCardsList(List<String> cardStrings) {
        List<Integer> result = new ArrayList<>();
        for (String cardStr : cardStrings) {
            result.add(stringToCard(cardStr));
        }
        return result;
    }
    public static void main(String[] args) {
        // 测试转换方法
        List<Integer> cards = Arrays.asList(0, 13, 26, 39);
        List<String> cardStrings = cardsToStringList(cards);
        System.out.println("整数列表: " + cards + " 转换为字符串列表: " + cardStrings);

        List<Integer> convertedCards = stringsToCardsList(cardStrings);
        System.out.println("字符串列表: " + cardStrings + " 转换为整数列表: " + convertedCards);

    }
}