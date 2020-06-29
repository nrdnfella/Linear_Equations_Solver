package solver;

import javax.swing.text.MutableAttributeSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.StrictMath.round;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        HashMap<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) {
                arguments.put(args[i], "");
            } else {
                arguments.put(args[i - 1], args[i]);
            }
        }

        Matrix matrix = null;
        if (arguments.containsKey("-in") && !arguments.get("-in").isEmpty()) {
            try {
                matrix = inputMatrix(arguments.get("-in"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        solve(matrix);

        if (arguments.containsKey("-out") && !arguments.get("-out").isEmpty()) {
            File file = new File(arguments.get("-out"));
            try (PrintWriter printWriter = new PrintWriter (file)) {
                if (matrix != null) {
                    for (Row r : matrix.rows()) {
                        printWriter.printf("%fd\n",r.coefficients()[Matrix.size]);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Matrix inputMatrix(String pathToFile) throws IOException {
        File file = new File(pathToFile);

        try (Scanner fileScanner = new Scanner(file)) {
            Matrix matrix = new Matrix(Integer.parseInt(fileScanner.nextLine()));
            while (fileScanner.hasNext()) {
                matrix.add(fileScanner.nextLine().split(" "));
            }
            return matrix;
        }
    }


    public static void solve(Matrix matrix) {
        double m;

        for (int i = 0; i < Matrix.size; i++) {
            if (matrix.row(i).coefficient(i) != 1) {
                m = 1 / matrix.row(i).coefficient(i);
                multiply(m, matrix.row(i));
            }

            for (int j = i + 1; j < Matrix.size; j++) {
                if (matrix.row(j).coefficient(i) != 0) {
                    m = -1 * matrix.row(j).coefficient(i);
                    summarize(m, matrix.row(i), matrix.row(j));
                }
            }
        }

        for (int i = Matrix.size - 1; i >= 0; i--) {
            if (round(matrix.row(i).coefficient(i)) != 1) {
                m = 1 / matrix.row(i).coefficient(i);
                multiply(m, matrix.row(i));
            }

            for (int j = i - 1; j >= 0; j--) {
                if (matrix.row(j).coefficient(i) != 0) {
                    m = -1 * matrix.row(j).coefficient(i);
                    summarize(m, matrix.row(i), matrix.row(j));
                }
            }
        }
    }

    private static void multiply(double m, Row row) {
        for (int i = 0; i <= Matrix.size; i++) {
            row.coefficients()[i] = row.coefficients()[i] * m;
        }
    }

    private static void summarize(double m, Row row1, Row row2) {
        for (int i = 0; i <= Matrix.size; i++) {
            if (row1.coefficients()[i] != 0) {
                double k = row1.coefficients()[i] * m;
                row2.coefficients()[i] = row2.coefficients()[i] + k;
            }
        }
    }
}

class Matrix {
    static int size = 0;
    private Row[] rows;

    public Matrix(int n) {
        this.rows = new Row[n];
    }

    public void add(String[] array) {
        rows[size] = new Row(array);
        size++;
    }

    public Row row(int index) {
        return rows[index];
    }

    public Row[] rows() {
        return rows;
    }
}

class Row {
    private int index;
    private double[] coefficients;

    public Row(String[] array) {
        ++index;
        this.coefficients = new double[array.length];
        setCoefficients(array);
    }

    private void setCoefficients(String[] array) {
        int i = 0;
        for (String s : array) {
            this.coefficients[i] = Double.parseDouble(s);
            i++;
        }
    }

    public double coefficient(int index) {
        return coefficients[index];
    }

    public double[] coefficients() {
        return coefficients;
    }

    @Override
    public String toString() {
        return "R" + index;
    }
}