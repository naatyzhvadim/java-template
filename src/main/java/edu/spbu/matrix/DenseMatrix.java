package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  private double[][] mx = null;
  private int n;
  /**
   * загружает матрицу из файла
   * @param fileName
   */

  public DenseMatrix(String fileName) throws IOException {
    String str;
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    str = in.readLine();
    double[] ar =  Arrays.stream(str.split(" ")).mapToDouble(Double::parseDouble).toArray();
    n = ar.length;
    int j = 0;
    mx = new double[n][n];
    mx[j] = ar;
    while(++j < n) {
      str = in.readLine();
      mx[j] = Arrays.stream(str.split(" ")).mapToDouble(Double::parseDouble).toArray();
    }
  }
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param
   * @return
   */

  private int getsize(){
    return n;
  }

  public void print_mx(){
      for(int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j)
              System.out.print(mx[i][j] + " ");
          System.out.println();
      }
  }

  public double[][] getMx(){
    return mx;
  }

  DenseMatrix(double[][] m){
    mx = m;
    n = mx[0].length;
  }

  DenseMatrix(Matrix o){
    if (o instanceof DenseMatrix){
      DenseMatrix other;
      other = (DenseMatrix)o;
      mx = other.getMx();
      n = other.getsize();
    }else{
      SparseMatrix other = (SparseMatrix)o;

      n = other.get_n();
      mx = new double[n][n];
      int[] col_indexes = other.get_col_ind();
      int[] row_pointers = other.get_row_ptr();
      double[] CRS_values = other.get_CRS();

      int cur_i = 0, cur_j, val = 0, ptr = 0;
      //int new_line = 0;
      cur_j = col_indexes[val];
      while((ptr + 1 < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])){
        ++ptr;
        ++cur_i;
      }
      //System.out.println("CUR = " + cur_j);
      /*for(int i = 0; i < cur_j; ++i)
        System.out.print("0.0 ");*/
      mx[cur_i][cur_j] = CRS_values[val];
      ++val;
      while(val < CRS_values.length) {
        //System.out.println("val = " + val + " ptr = " + ptr);
        //int f = 1;
        while ((ptr + 1 < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])) {
          ++ptr;
          ++cur_i;
          //new_line = 1;

          /*if (f == 1)
            --f;*/
        }

        if (ptr + 1 < row_pointers.length && val == row_pointers[ptr + 1]) {
          //System.out.println("+++++++++");
          ++cur_i;
          ++ptr;
          //new_line = 1;

          /*if (f == 1)
            --f;*/

        }
        while ((ptr + 1  < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])) {
          ++ptr;
          ++cur_i;
          //new_line = 1;

          /*if (f == 1)
            --f;*/
        }

        cur_j = col_indexes[val];

        mx[cur_i][cur_j] = CRS_values[val];
        ++val;
        /*if (new_line == 1)
          new_line = 0;*/
      }
    }
  }

  public double getelement(int i, int j){
    return mx[i][j];
  }

  @Override public Matrix mul(Matrix o) {
    DenseMatrix other;
    if (o instanceof DenseMatrix){
      other = (DenseMatrix) o;
    }else
      other  = new DenseMatrix(o);

    final int fn = n;

    double[][] m = other.getMx();
    double thatColumn[] = new double[fn];
    double[][] c = new double[fn][fn];
    for (int j = 0; j < fn; ++j) {
      for (int k = 0; k < fn; ++k) {
        thatColumn[k] = m[k][j];
      }
      for (int i = 0; i < fn; ++i) {
        double thisRow[] = mx[i];
        double summand = 0;
        for (int k = 0; k < fn; ++k) {
          summand += thisRow[k] * thatColumn[k];
        }
        c[i][j] = summand;
      }
    }
    //pr_mx(c);
    return new DenseMatrix(c);
  }

  public void pr_mx(double[][] m){
    int ns = m[0].length;
    for(int i = 0; i < ns; ++i) {
      for (int j = 0; j < ns; ++j)
        System.out.print(m[i][j] + " ");
      System.out.println();
    }
  }

  public Matrix natmul(Matrix o) {
    double[][] c = new double[n][n], m = ((DenseMatrix)o).getMx();

    for(int i = 0; i < n; ++i)
      for(int j = 0; j < n; ++j){
        double sum = 0;
        for(int k = 0; k < n; ++k)
          sum += mx[i][k] * m[k][j];
        c[i][j] = sum;
      }
    return new DenseMatrix(c);
  }
  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */

  private double[][] t_mx, res_mx;
  private int flag = 0;

  private void make_t_mx(double[][] m){
      t_mx = new double[n][n];
      for(int i = 0; i < n; ++i)
          for(int j = 0; j < n; ++j)
              t_mx[i][j] = m[j][i];
  }

  private synchronized int get_next_task(){
      //System.out.println("GET f = " + flag);
      flag += 16;
      return flag - 16;
  }

  class MyRun implements Runnable{
      public void run(){
          int k = get_next_task();
          while(k < n) {
              for(int x = k; x - k < 16 && x < n; ++x) {
                  for (int i = 0; i < n; ++i) {
                      int sum = 0;
                      for (int j = 0; j < n; ++j)
                          sum += mx[i][j] * t_mx[x][j];
                      res_mx[i][x] = sum;
                  }
                  //System.out.println();
              }
              k = get_next_task();
          }
      }
  }


  @Override public Matrix dmul(Matrix o) throws InterruptedException
  {
      DenseMatrix m2 = new DenseMatrix(o);
      int x = 10;
      make_t_mx(m2.getMx());
      res_mx = new double[n][n];
      Thread[] t = new Thread[n/x];
      for(int i = 0; i < n/x; ++i){
          //System.out.println("NEW THREAD");
          t[i] = new Thread(new MyRun());
          t[i].start();
      }
      for (int i = 0; i < n/x; ++i)
          t[i].join();
      //System.out.println("HERE flag = " + flag);

      /*for(int i = 0; i < n; ++i) {
          for (int j = 0; j < n; ++j)
              System.out.print(res_mx[i][j] + " ");
          System.out.println();
      }*/
      return new DenseMatrix(res_mx);
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {

    //System.out.println("EQUALS IN DENSE");
    /*if(!super.equals(o))
      return false;*/
    if (this == o)
      return true;
    if (o == null)
      return false;
    if (o instanceof DenseMatrix) {
      DenseMatrix otherObj = (DenseMatrix) o;
      if (n != otherObj.getsize())
        return false;
      for (int i = 0; i < n; ++i)
        for (int j = 0; j < n; ++j)
          if (getelement(i, j) != otherObj.getelement(i, j))
            return false;
    }else if (o instanceof SparseMatrix){
      //System.out.println("FOCUS");
      return o.equals(this);
    }
    return true;
    }
}