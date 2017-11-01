package edu.spbu.matrix;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MatrixTest {
    /**
    * ожидается 4 таких теста
    */
    @Test
    public void mulDD() throws IOException {
        Matrix m1 = new DenseMatrix("./src/main/java/edu/spbu/m1.txt");
        Matrix m2 = new DenseMatrix("./src/main/java/edu/spbu/m2.txt");
        Matrix expected = new DenseMatrix("./src/main/java/edu/spbu/result.txt");
        assertEquals(expected, m1.mul(m2));
    }
    @Test
    public void mulSS() throws IOException {
        Matrix s1 = new SparseMatrix("./src/main/java/edu/spbu/s1.txt");
        Matrix s2 = new SparseMatrix("./src/main/java/edu/spbu/s2.txt");
        Matrix expected = new SparseMatrix("./src/main/java/edu/spbu/sresult.txt");
        Matrix res = s1.mul(s2);
        res.print_mx();
        assertEquals(expected, res);
    }
    @Test
    public void mulSD() throws IOException{
        Matrix s1 = new SparseMatrix("./src/main/java/edu/spbu/s1.txt");
        Matrix m2 = new DenseMatrix("./src/main/java/edu/spbu/s2.txt");
        Matrix expected = new SparseMatrix("./src/main/java/edu/spbu/sresult.txt");
        Matrix res = new SparseMatrix(s1.mul(m2));
        res.print_mx();
        assertEquals(expected, s1.mul(m2));
    }

    @Test
    public void mulDS() throws IOException{
        Matrix m1 = new DenseMatrix("./src/main/java/edu/spbu/m1.txt");
        Matrix s2 = new SparseMatrix("./src/main/java/edu/spbu/m2.txt");
        Matrix expected = new SparseMatrix("./src/main/java/edu/spbu/result.txt");
        //Matrix res = new DenseMatrix(m1.mul(s2));
        //res.print_mx();
        assertEquals(expected, m1.mul(s2));
    }


    @Test
    public void test() throws IOException{
        Matrix m1 = new DenseMatrix("./src/main/java/edu/spbu/m1.txt");
        Matrix m2 = new DenseMatrix("./src/main/java/edu/spbu/m2.txt");
        Matrix expected = new SparseMatrix("./src/main/java/edu/spbu/result.txt");
        try{
            assertEquals(expected, m1.dmul(m2));
        }catch (InterruptedException e){

        }
    }

    @Test
    public void test1() throws IOException{
        Matrix m1 = new DenseMatrix("m1.txt");
        Matrix m2 = new DenseMatrix("m2.txt");
        //Matrix expected = new SparseMatrix("./src/main/java/edu/spbu/result.txt");
        System.out.println("Nat mul starts");
        long start = System.currentTimeMillis();
        Matrix res1 = m1.mul(m2);
        System.out.println("Nat mul ended in " + (System.currentTimeMillis() - start));
        try{
            System.out.println("Dmul starts");
            start = System.currentTimeMillis();
            Matrix res2 = m1.dmul(m2);
            System.out.println("Dmul ended in " + (System.currentTimeMillis() - start));
            assertEquals(res1, res2);
        }catch (InterruptedException e){

        }
    }
}