package com.joe.leetcode_impl.easy.q9;

public class PalindromeNumber {
    public boolean isPalindrome(int x) {
        if (x<0||(x%10==0&&x!=0)){
            return false;
        }

        int reversedNum = 0;

        while (x>reversedNum){
            reversedNum = reversedNum * 10 + x % 10;
            x /= 10;
        }

        //长度为偶数 reversedNum == x
        //奇数 x == reversedNum / 10

        return reversedNum == x || (x == reversedNum / 10);
    }

    public static boolean isPalindrome1(int x) {
        if (x<0||(x%10==0&&x!=0)){
            return false;
        }

        String s = Integer.toString(x);

        if (s.equals(new StringBuffer(s).reverse().toString())){
            return true;
        }
        return false;
    }
}
