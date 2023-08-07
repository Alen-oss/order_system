package com.colorfull.order_system.sort;

import java.util.Arrays;

/**
 * 快速排序分治算法：求无序数组中第K大的元素
 */
public class QuickSort1 {

    public static void main(String[] args) {
        int[] nums = new int[]{10, 1, 5, 3, 2, 7};
        QuickSort1 quickSort1 = new QuickSort1();
        int ans = quickSort1.findKthLargest(nums, 2);
        System.out.println(ans);
        quickSort1.sort(nums, 0, nums.length - 1);
        for (int num : nums) {
            System.out.println(num);
        }
    }

    public int findKthLargest(int[] nums, int k) {

        int pivot = partition(nums, 0, nums.length - 1);
        if (pivot == k - 1) {
            return nums[pivot];
        } else if (pivot < k - 1) {
            return findKthLargest(Arrays.copyOfRange(nums, pivot + 1, nums.length), k - pivot - 1);
        } else {
            return findKthLargest(Arrays.copyOfRange(nums, 0, pivot), k);
        }
    }

    public void sort(int[] nums, int left, int right) {

        if (left < right) {
            int pivot = partition(nums, left, right);
            sort(nums, left, pivot - 1);
            sort(nums, pivot, right);
        }
    }

    /**
     * 寻找nums[right]在数组中正确的位置，降序排列
     */
    public int partition(int[] nums, int left, int right) {

        int pivot = nums[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            // 正常情况下，这里是 < 号，代表升序排列
            if (nums[j] > pivot) {
                i++;
                swap(nums, i, j);
            }
        }
        int temp = nums[right];
        nums[right] = nums[i + 1];
        nums[i + 1] = temp;
        return i + 1;
    }

    public void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
