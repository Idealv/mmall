package com.joe.leetcode_impl.easy.q7;

import com.joe.leetcode_impl.LeetcodeImplApplicationTests;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReverseIntegerTest extends LeetcodeImplApplicationTests {

    @Test
    public void reverse() {
        System.out.println(new StringBuffer(Integer.toString(123)).reverse().toString());
    }
}