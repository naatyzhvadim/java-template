package edu.spbu.matrix;

    /**
    *
    */
public interface Matrix
{
    /**
    * однопоточное умнджение матриц
    * должно поддерживаться для всех 4-х вариантов
    * @param o
    * @return
    */
    Matrix mul(Matrix o);
    //public double getelement(int i, int j);
    void print_mx();

    /**
    * многопоточное умножение матриц
    * @param o
    * @return
    */
    Matrix dmul(Matrix o);

}
