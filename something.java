import java.util.Scanner;

public class SimpleEuler {

    // *** CHANGE THIS LINE to use a different differential equation ***
    // This is dy/dx = x + y.  Examples: return x * y;   return 2 * x - y;   return Math.sin(x) + y;
    public static double f(double x, double y) {
        return x + y;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Euler's Method  (equation is in the f method at the top of the code)");
        System.out.print("Initial x: ");
        double x = in.nextDouble();
        System.out.print("Initial y: ");
        double y = in.nextDouble();

        System.out.println("Which two do you know?");
        System.out.println("1) dx and number of steps");
        System.out.println("2) dx and final x");
        System.out.println("3) number of steps and final x");
        int choice = in.nextInt();

        double dx = 0, xFinal = 0;
        int steps = 0;

        if (choice == 1) {
            System.out.print("dx: ");
            dx = in.nextDouble();
            System.out.print("Number of steps: ");
            steps = in.nextInt();
        } else if (choice == 2) {
            System.out.print("dx: ");
            dx = in.nextDouble();
            System.out.print("Final x: ");
            xFinal = in.nextDouble();
            steps = (int) Math.round((xFinal - x) / dx);
        } else {
            System.out.print("Number of steps: ");
            steps = in.nextInt();
            System.out.print("Final x: ");
            xFinal = in.nextDouble();
            dx = (xFinal - x) / steps;
        }

        System.out.println("\nstep      x          y");
        System.out.printf("%4d  %9.4f  %9.4f%n", 0, x, y);

        for (int i = 1; i <= steps; i++) {
            y = y + dx * f(x, y);   // Euler's Method: new y = old y + dx * slope
            x = x + dx;
            System.out.printf("%4d  %9.4f  %9.4f%n", i, x, y);
        }

        System.out.printf("%nFinal point: (%.4f, %.4f)%n", x, y);
    }
}