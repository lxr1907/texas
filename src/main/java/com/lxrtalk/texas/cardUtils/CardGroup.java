package com.lxrtalk.texas.cardUtils;

import java.util.ArrayList;
import java.util.Random;

public class CardGroup {
    // 一副牌有52张
    public static final int CARD_NUM = 52;

    /**
     * 获取0到52的list代表一副牌，0到3代表黑A红A梅A方A
     */
    private static ArrayList<Integer> getInitialCards() {
        ArrayList<Integer> cardsList = new ArrayList<Integer>(CARD_NUM);
        for (int i = 0; i < CARD_NUM; i++) {
            cardsList.add(i);
        }
        return cardsList;
    }

    /**
     * 获取随机卡组,范围0到52，0到3代表黑A红A梅A方A
     */
    public static ArrayList<Integer> getRandomCards() {
        ArrayList<Integer> src = getInitialCards();
        ArrayList<Integer> cardsList = new ArrayList<Integer>(CARD_NUM);
        while (!src.isEmpty()) {
            int size = src.size();
            Random r = new Random();
            int index = r.nextInt(size);

            cardsList.add(src.get(index));
            src.remove(index);
        }
        return cardsList;
    }

}
