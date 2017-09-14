package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void merge(int[] arr, int left, int mid, int right) {
      int i = left, j = mid, k = 0;
      int[] res = new int[right - left];
      while(i < mid && j < right){
        if(arr[i] < arr[j])
          res[k++] = arr[i++];
        else
          res[k++] = arr[j++];
      }
      while(i < mid)
        res[k++] = arr[i++];
      while(j < right)
        res[k++] = arr[j++];
      for(i = left, k = 0; i < right; ++i, ++k)
        arr[i] = res[k];
    }

  public static void mergesort(int[] arr, int left, int right){
      if (left + 1 >= right)
        return;
      int mid = (right + left) / 2;
      //System.out.println(left + " " + mid + " " + right);
      mergesort(arr, left, mid);
      mergesort(arr, mid, right);
      merge(arr, left, mid, right);
    }

  public static void sort (int array[]) {
    mergesort(array, 0, array.length);
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
