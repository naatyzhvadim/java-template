package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{


  /**
   * загружает матрицу из файла
   * @param fileName
   */

  private void print_empty(){
    for(int i = 0; i < n; ++i)
      System.out.print("0.0 ");
    System.out.println();
  }


  public void print_mx(){
    int cur_i = 0, cur_j, val = 0, ptr = 0, new_line = 0;
    cur_j = col_indexes[val];
    while((ptr + 1 < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])){
      //System.out.println("OOO");
      print_empty();
      ++ptr;
      ++cur_i;
    }
    //System.out.println("CUR = " + cur_j);
    for(int i = 0; i < cur_j; ++i)
      System.out.print("0.0 ");
    System.out.print(CRS_values[val] + " ");
    ++val;
    while(val < CRS_values.length) {
      //System.out.println("val = " + val + " ptr = " + ptr);
      int f = 1;
      while ((ptr + 1 < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])) {
        ++ptr;
        ++cur_i;
        new_line = 1;

        if (f == 1){
          for(int i = 0; i < n - cur_j - 1; ++i)
            System.out.print("0.0 ");
          System.out.println();
          --f;
        }else
          print_empty();
      }

      if (ptr + 1 < row_pointers.length && val == row_pointers[ptr + 1]) {
        //System.out.println("+++++++++");
        ++cur_i;
        ++ptr;
        new_line = 1;

        if (f == 1){
          for(int i = 0; i < n - cur_j - 1; ++i)
            System.out.print("0.0 ");
          System.out.println();
          --f;
        }else
          print_empty();
      }
      while ((ptr + 1  < row_pointers.length) && (row_pointers[ptr] == row_pointers[ptr + 1])) {
        ++ptr;
        ++cur_i;
        new_line = 1;

        if (f == 1){
          for(int i = 0; i < n - cur_j - 1; ++i)
            System.out.print("0.0 ");
          System.out.println();
          --f;
        }else
          print_empty();
      }

      cur_j = col_indexes[val];

      if (new_line == 1)
        for(int i = 0; i < cur_j; ++i)
          System.out.print("0.0 ");
      else {
        //System.out.print("..");
        for (int i = 0; i < cur_j - col_indexes[val - 1] - 1; ++i)
          System.out.print("0.0 ");
      }
      System.out.print(CRS_values[val] + " ");
      ++val;
      if (new_line == 1)
        new_line = 0;
    }
    for(int i = 0; i < n - cur_j - 1; ++i)
      System.out.print("0.0 ");
  }

  /*private ArrayList<Double> CRS_values, CSR_values;
  private ArrayList<Integer> col_indexes, row_pointers, row_indexes, col_pointers;*/

  private double[] CRS_values, CSR_values;
  private int[] col_indexes, row_pointers, row_indexes, col_pointers;
  private int n;

  public double[] get_CRS(){
    return CRS_values;
  }
  private double[] get_CSR(){
    return CSR_values;
  }

  public int[] get_col_ind(){
    return col_indexes;
  }
  public int[] get_row_ptr(){
    return row_pointers;
  }
  private int[] get_row_ind(){
    return row_indexes;
  }
  private int[] get_col_ptr(){
    return col_pointers;
  }
  public int get_n(){
    return n;
  }

  private void print_arr_d(final double[] a){
    for(int i = 0; i < a.length; ++i)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  private static void print_arr(final int[] a){
    for(int i = 0; i < a.length; ++i)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public SparseMatrix(String fileName) throws IOException{

    String str;
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    str = in.readLine();
    double[] ar =  Arrays.stream(str.split(" ")).mapToDouble(Double::parseDouble).toArray();

    n = ar.length;
    ArrayList<Double> CRS_val = new ArrayList<>();
    ArrayList<Integer> col_ind = new ArrayList<>();
    ArrayList<Integer> row_ptr = new ArrayList<>();
    int i = 0, j, k = 0;

    row_ptr.add(k);
    for(j = 0; j < n; ++j)
      if (ar[j] != 0){
        ++k;
        CRS_val.add(ar[j]);
        col_ind.add(j);
      }

      i++;
    for(;i < n; i++) {
      str = in.readLine();
      ar = Arrays.stream(str.split(" ")).mapToDouble(Double::parseDouble).toArray();

      row_ptr.add(k);
      for(j = 0; j < n; ++j)
        if (ar[j] != 0){
          ++k;
          CRS_val.add(ar[j]);
          col_ind.add(j);
        }
    }

    CRS_values = new double[CRS_val.size()];
    for(int q = 0; q < CRS_val.size(); ++q)
      CRS_values[q] = CRS_val.get(q);

    col_indexes = new int[col_ind.size()];
    for(int q = 0; q < col_ind.size(); ++q)
      col_indexes[q] = col_ind.get(q);

    row_pointers = new int[row_ptr.size()];
    for(int q = 0; q < row_ptr.size(); ++q)
      row_pointers[q] = row_ptr.get(q);
  }

  private void make_CSR(){

    ArrayList<Double> CSR_val = new ArrayList<>();
    ArrayList<Integer> row_ind = new ArrayList<>();
    ArrayList<Integer> col_ptr = new ArrayList<>();

    int k = 0;
    col_ptr.add(k);
    //System.out.println("N = " + n);
    for(int i = 0; i < n; ++i){
      int flag = k;
      for(int j = 0; j < col_indexes.length; ++j){
        if (col_indexes[j] == i) {
          CSR_val.add(CRS_values[j]);
          int x = 0;
          while(x < row_pointers.length && j > row_pointers[x]) {
            ++x;
          }
          while(x < row_pointers.length && j == row_pointers[x])
            ++x;
          /*if (i == 0 && j == 0)
            System.out.println("x == " + x + " asda " + row_pointers.size());*/
          if (x < row_pointers.length)
            row_ind.add(j == row_pointers[x]? x  : x - 1);
          else
            row_ind.add(x-1);
          ++k;
        }
      }
      if (flag - k != 0)
        col_ptr.add(k);
    }
    col_ptr.remove(col_ptr.size() - 1);

    CSR_values = new double[CSR_val.size()];
    for(int q = 0; q < CSR_val.size(); ++q)
      CSR_values[q] = CSR_val.get(q);
    //System.arraycopy(CSR_val.toArray(new Double[CSR_val.size()]), 0, CSR_values, 0, CSR_val.size() );
    col_pointers = new int[col_ptr.size()];
    for(int q = 0; q < col_ptr.size(); ++q)
      col_pointers[q] = col_ptr.get(q);
    //System.arraycopy(col_ptr.toArray(new Integer[col_ptr.size()]), 0, col_pointers, 0, col_ptr.size() );
    row_indexes = new int[row_ind.size()];
    for(int q = 0; q < row_ind.size(); ++q)
      row_indexes[q] = row_ind.get(q);
    //System.arraycopy(row_ind.toArray(new Integer[row_ind.size()]), 0, row_indexes, 0, row_ind.size() );

  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */

  /*public void add_rp(int k){
    row_pointers.add(k);
  }
  public void add_CRS(double s){
    CRS_values.add(s);
  }
  public void add_ci(int j) {
    col_indexes.add(j);
  }*/

  /*public SparseMatrix(){

  }*/

  public SparseMatrix(Matrix o){
    if (o instanceof SparseMatrix){

      //System.out.println("Sparse  ");
      SparseMatrix otherObj = (SparseMatrix)o;
      CRS_values = otherObj.get_CRS();
      col_indexes = otherObj.get_col_ind();
      row_pointers = otherObj.get_row_ptr();
      n = otherObj.get_n();
      /*row_pointers = new ArrayList<>();
      CRS_values = new ArrayList<>();
      col_indexes = new ArrayList<>();

      CRS_values.addAll(otherObj.get_CRS());
      col_indexes.addAll(otherObj.get_col_ind());
      row_pointers.addAll(otherObj.get_row_ptr());*/

      //System.out.println(CRS_values.size() + "  " + col_indexes.size() + "  " + row_pointers.size());

      /*System.out.println("Constructor");

      System.out.print("A        = ");
      print_arr_d(CRS_values);
      System.out.print("col_ind  = ");
      print_arr(col_indexes);
      System.out.print("row_poi  = ");
      print_arr(row_pointers);
      System.out.println();*/

    }else if (o instanceof DenseMatrix){
      //System.out.println("Dense  111111");

      DenseMatrix otherObj = (DenseMatrix)o;
      ArrayList<Integer> row_ptr = new ArrayList<>();
      ArrayList<Double> CRS_val = new ArrayList<>();
      ArrayList<Integer> col_ind = new ArrayList<>();
      double[][] m = otherObj.getMx();

      int k = 0, n1 = m[0].length;
      for(int i = 0; i < n1; ++i) {
        row_ptr.add(k);
        for(int j = 0; j < n1; ++j)
          if (m[i][j] != 0){
            ++k;
            CRS_val.add(m[i][j]);
            col_ind.add(j);
          }
      }
      n = n1;

      CRS_values = new double[CRS_val.size()];
      for(int i = 0; i < CRS_val.size(); ++i)
        CRS_values[i] = CRS_val.get(i);
      //System.arraycopy(CRS_val.toArray(new Double[CRS_val.size()]), 0, CRS_values, 0, CRS_val.size() );
      col_indexes = new int[col_ind.size()];
      for(int i = 0; i < col_ind.size(); ++i)
        col_indexes[i] = col_ind.get(i);
      //System.arraycopy(col_ind.toArray(new Integer[col_ind.size()]), 0, col_indexes, 0, col_ind.size() );
      row_pointers = new int[row_ptr.size()];
      for (int i = 0; i < row_ptr.size(); ++i)
        row_pointers[i] = row_ptr.get(i);
      //System.arraycopy(row_ptr.toArray(new Integer[row_ptr.size()]), 0, row_pointers, 0, row_ptr.size() );

      /*System.out.println("Constructor 1111");

      System.out.print("A        = ");
      print_arr_d(CRS_values);
      System.out.print("col_ind  = ");
      print_arr(col_indexes);
      System.out.print("row_poi  = ");
      print_arr(row_pointers);
      System.out.println();*/
      //print_mx();
      //make_CSR();
    }
  }

  private SparseMatrix(Double[] CRS, Integer[] col, Integer[] row, int n1){
    CRS_values = new double[CRS.length];
    for(int i = 0; i < CRS.length; ++i)
      CRS_values[i] = CRS[i];
    //System.arraycopy(CRS, 0, CRS_values, 0, CRS.length);
    col_indexes = new int[col.length];
    for(int i = 0; i < col.length; ++i)
      col_indexes[i] = col[i];
    //System.arraycopy(col, 0, col_indexes, 0, col.length);
    row_pointers = new int[row.length];
    for(int i = 0; i < row.length; ++i)
      row_pointers[i] = row[i];
    //System.arraycopy(row, 0, row_pointers, 0, row.length);
    n = n1;
  }

  @Override public Matrix mul(Matrix o) {
    SparseMatrix other;
    if (o instanceof SparseMatrix) {
      //System.out.println("OOOOOEEEEEE");
      other = (SparseMatrix) o;
    }else{
      //System.out.println("OOOOOLLLLLL");
      other = new SparseMatrix(o);
    }

    other.make_CSR();

    int [] col_ptr = other.get_col_ptr();
    int[] row_ind = other.get_row_ind();
    double[] CSR_val = other.get_CSR();

    ArrayList<Double> res_CRS = new ArrayList<>();
    ArrayList<Integer> res_col = new ArrayList<>();
    ArrayList<Integer> res_row = new ArrayList<>();

    /*System.out.println("Copied arrays");
    System.out.print("A        = ");
    print_arr_d(CSR_val);
    System.out.print("col_ind  = ");
    print_arr(col_ptr);
    System.out.print("row_poi  = ");
    print_arr(row_ind);
    System.out.println();*/

    int k = 0;
    for (int i = 0; i < row_pointers.length; i++) {
      //System.out.println("ROW_PTR === " + row_pointers.size());
      res_row.add(k);
      for (int j = 0; j < col_ptr.length; j++) {
        //System.out.println("i = " + i + " j = " + j + "Size = " + col_ptr.size());
        double sum = 0.0;


        int i1 = row_pointers[i];
        int i2 = col_ptr[j];
        int end1;
        if (i != row_pointers.length - 1)
          end1 = row_pointers[i + 1];
        else {
          end1 = CRS_values.length;
        }
        int end2;
        if (j != col_ptr.length - 1)
          end2 = col_ptr[j+1];
        else {
          end2 = CSR_val.length;
        }

        //System.out.println("i1 = " + i1 + " end1 = " + end1 + " i2 = " + i2 + " end2 = " + end2);
        while (i1 < end1 && i2 < end2) {
          if (col_indexes[i1] == row_ind[i2]) {
            sum += CRS_values[i1++] * CSR_val[i2++];
          } else if (col_indexes[i1] < row_ind[i2]) {
            i1++;
          } else {
            i2++;
          }
        }
        if (sum != 0.0) {
          //System.out.println(" i = " + i + " j = " + j + " sum = " + sum);
          ++k;
          res_CRS.add(sum);
          res_col.add(j);
        }

      }
    }
    /*System.out.println("RESULT k = " + k);
    res.print_arr_d(res.get_CRS());
    res.print_arr(res.get_col_ind());
    res.print_arr(res.get_row_ptr());*/
    /*System.out.println("RESSS = " + res.get_n());
    res.print_mx();*/
    return new SparseMatrix(res_CRS.toArray(new Double[res_CRS.size()]),
            res_col.toArray(new Integer[res_col.size()]),
            res_row.toArray(new Integer[res_row.size()]), n);
  }

  /*private void set_n(int x){
    n = x;
  }*/

  /*public void print_mx(){
    for(int i = 0; i < )
  }*/

  @Override public boolean equals(Object o) {
    //System.out.println("EQUALS IN SPARSE");
    /*if(!super.equals(o))
      return false;*/
    if (this == o)
      return true;
    if (o == null)
      return false;
    /*if(this.getClass() != o.getClass())
      return false;*/
    if (o instanceof SparseMatrix) {
      SparseMatrix otherObj = (SparseMatrix) o;
      //System.out.println("OOps");
      if (!Arrays.equals(otherObj.get_CRS(), CRS_values))
        return false;
      if (!Arrays.equals(otherObj.get_col_ind(), col_indexes))
        return false;
      if (!Arrays.equals(otherObj.get_row_ptr(), row_pointers))
        return false;

      /*ArrayList<Double> d = new ArrayList<>();
      d.addAll(otherObj.get_CRS());

      System.out.println("VALUES:");
    for(int i = 0; i < CRS_values.size(); ++i)
      System.out.print(CRS_values.get(i) + " ");
    System.out.println(d.size());
    for(int i = 0; i < d.size(); ++i)
      System.out.print(d.get(i) + " ");

      for (int i = 0; i < CRS_values.size(); ++i)
        if (Double.compare(CRS_values.get(i), d.get(i)) != 0) {
          System.out.println("OOOOOOOOOOOPs");
          return false;
        }
      ArrayList<Integer> a = otherObj.get_col_ind();
      for (int i = 0; i < col_indexes.size(); ++i)
        if (col_indexes.get(i) != a.get(i))
          return false;
      a = otherObj.get_row_ptr();
      for (int i = 0; i < row_pointers.size(); ++i)
        if (row_pointers.get(i) != a.get(i))
          return false;*/
    }else{
      //System.out.println("OPA");
      SparseMatrix other = new SparseMatrix((Matrix) o);
      return this.equals(other);
      //SparseMatrix other = new SparseMatrix(o);
    }
    return true;
  }
  /*row_pointers.add(k);
    for(j = 0; j < n; ++j)
      if (ar[j] != 0){
        ++k;
        CRS_values.add(ar[j]);
        col_indexes.add(j);
      }*/
  public Matrix sub(Matrix o){
    return null;
  }
  public Matrix add(Matrix o){
    return null;
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
}
