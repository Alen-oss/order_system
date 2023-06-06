package com.colorfull.order_system.sort;

/**
 * 优先队列（构建大顶堆）：求无序数组中第K大的元素
 */
public class PriorityQueueTest {

    public int findKthLargest(int[] nums, int k) {

        int heapSize = nums.length;
        buildMaxHeap(nums, heapSize);
        for (int i = nums.length - 1; i >= nums.length - k + 1; --i) {
            // 将堆顶元素与末尾元素交换，等同于移除堆顶元素
            swap(nums, 0, i);
            --heapSize;
            maxHeapify(nums, 0, heapSize);
        }
        return nums[0];
    }

    public void buildMaxHeap(int[] a, int heapSize) {

        for (int i = heapSize / 2; i >= 0; --i) {
            maxHeapify(a, i, heapSize);
        }
    }

    /**
     * 堆化操作：即与左右子节点比较大小
     */
    public void maxHeapify(int[] a, int i, int heapSize) {

        int l = i * 2 + 1, r = i * 2 + 2, largest = i;
        if (l < heapSize && a[l] > a[largest]) {
            largest = l;
        }
        if (r < heapSize && a[r] > a[largest]) {
            largest = r;
        }
        // 这里如果发生改变，则继续递归
        if (largest != i) {
            swap(a, i, largest);
            maxHeapify(a, largest, heapSize);
        }
    }

    public void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
