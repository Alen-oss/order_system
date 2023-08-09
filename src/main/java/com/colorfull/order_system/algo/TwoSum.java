package com.colorfull.order_system.algo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 两数之和
 * 题目描述：给定一个有序整数数组nums和一个整数目标值target，请在该数组中找出和为target的两个元素的下标值，并返回
 */
public class TwoSum {

    public static void main(String[] args) {
        TwoSum twoSum = new TwoSum();
        int[] nums = {1, 2, 3, 4, 5, 6};
        int[][] res = twoSum.find3(nums, 7);
        for (int[] num : res) {
            System.out.println("left: " + num[0] + ", right" + num[1]);
        }
    }

    /**
     * 解法1
     * 左右指针 + 考虑边界相同数值问题
     * 左右指针适合有序的数组
     */
    public int[][] find(int[] nums, int target) {
        // 临时存储数组结果
        Queue<int[]> queue = new LinkedList<>();
        // sort方法默认使用的是快速排序，时间复杂度为O(n * log(n))
        Arrays.sort(nums);
        int left = 0, right = nums.length - 1;
        // 最坏情况就是时间复杂度为O(n的平方)：nums = {2, 2, 2, 2} target = 4
        while (left < right) {
            int s = nums[left] + nums[right];
            if (s < target) {
                left++;
            } else if (s > target) {
                right--;
            } else {
                queue.add(new int[]{left, right});
                // 这里要考虑边界问题，即数组中有相同的元素
                int tempLeft = left + 1;
                while (tempLeft < right && nums[left] == nums[tempLeft]) {
                    queue.add(new int[]{tempLeft, right});
                    tempLeft++;
                }
                if (nums[right] != nums[right - 1]) {
                    left = tempLeft;
                }
                right--;
            }
        }
        int[][] ans = new int[queue.size()][2];
        int i = 0;
        while (!queue.isEmpty()) {
            ans[i++] = queue.poll();
        }
        return ans;
    }

    /**
     * 解法2
     * 暴力破解：这个方法无论数组原本是否有序，都无所谓
     */
    public int[][] find2(int[] nums, int target) {
        Queue<int[]> queue = new LinkedList<>();
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) {
                    queue.add(new int[]{i, j});
                }
            }
        }
        int[][] ans = new int[queue.size()][2];
        int i = 0;
        while (!queue.isEmpty()) {
            ans[i++] = queue.poll();
        }
        return ans;
    }

    /**
     * 解法3：针对不存在重复元素的数组才有效
     * HashMap
     */
    public int[][] find3(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                queue.add(new int[]{i, map.get(target - nums[i])});
            }
            map.put(nums[i], i);
        }
        int[][] ans = new int[queue.size()][2];
        int i = 0;
        while (!queue.isEmpty()) {
            ans[i++] = queue.poll();
        }
        return ans;
    }
}
