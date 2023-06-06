package com.colorfull.order_system.sort;

import java.util.Arrays;

/**
 * 快速排序分治算法：求无序数组中第K大的元素
 */
public class QuickSort1 {

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

    /**
     * 寻找nums[right]在数组中正确的位置，升序排列
     */
    public int partition(int[] nums, int left, int right) {

        int pivot = nums[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
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
