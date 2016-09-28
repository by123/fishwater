package com.sjy.ttclub.shopping.shoppingcar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenjiawei on 2016/1/5.
 */
public class ShoppingCarState {
    public static Map<Integer, Boolean> isSelected = new HashMap<>();

    public static void initCheckBoxState(int size) {
        for (int i = 0; i < size; i++) {
            isSelected.put(i, true);
        }
    }
}
