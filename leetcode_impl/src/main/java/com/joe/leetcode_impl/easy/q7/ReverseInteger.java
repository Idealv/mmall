package com.joe.leetcode_impl.easy.q7;

public class ReverseInteger {
    public static int reverse(int x) {
        Long result;
        String temp = Integer.toString(x);
        if (x < 0) {
            result = -Long.valueOf(new StringBuffer(temp.substring(1)).reverse().toString());
        }else {
            result = +Long.valueOf(new StringBuffer(temp).reverse().toString());
        }
        if (result>Integer.MAX_VALUE||result<Integer.MIN_VALUE){
            return 0;
        }

        return Integer.valueOf(result.toString());
    }
}
