package com.colorfull.order_system.sort;

/**
 * 归并排序
 */
public class MergeSort {

    public static void main(String[] args) {
        int[] nums = new int[]{7, 5 , 1, 2, 10};
        MergeSort mergeSort = new MergeSort();
        mergeSort.reversePairs(nums);
        for (int num : nums) {
            System.out.println(num);
        }
    }

    public void reversePairs(int[] nums) {
        int n = nums.length;
        sort(0, n - 1, nums);
    }

    public void sort(int left, int right, int[] nums) {

        if (left < right) {
            int mid = left + ((right - left) >> 1);
            sort(left, mid, nums);
            sort(mid + 1, right, nums);
            merge(left, mid, right, nums);
        }
    }

    public void merge(int left, int mid, int right, int[] nums) {

        int[] l = new int[mid - left + 1];
        int[] r = new int[right - mid];
        for (int i = left; i <= mid; i++) {
            l[i - left] = nums[i];
        }
        for (int j = mid + 1; j <= right; j++) {
            r[j - mid - 1] = nums[j];
        }
        int index = left, i = 0, j = 0, llen = mid - left + 1, rlen = right - mid;
        while (i < llen && j < rlen) {
            if (l[i] <= r[j]) {
                nums[index] = l[i];
                i++;
            } else {
                nums[index] = r[j];
                j++;
            }
            index++;
        }

        while (i < llen) {
            nums[index] = l[i];
            i++;
            index++;
        }

        while (j < rlen) {
            nums[index] = r[j];
            j++;
            index++;
        }
    }
}
