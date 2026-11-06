import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Euler's Method calculator.
 *
 * Inputs:  a differential equation dy/dx = f(x, y), an initial point (x0, y0),
 *          and any two of: step size (dx), number of steps, final x-value.
 * Output:  the final (x, y) point, plus an optional table of every step.
 *
 * Run it with:   java EulersMethod.java      (or double-click run.command on a Mac)
 */
public class EulersMethod {

    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        boolean again = true;
        while (again) {
            runOnce();
            again = askYesNo("\nRun Euler's Method again? (y/n): ");
        }
        System.out.println("Goodbye!");
    }

    // ------------------------------------------------------------------------
    //  One full run: ask for inputs, compute, print results
    // ------------------------------------------------------------------------
    private static void runOnce() {
        // 1. The differential equation
        Expression f = readEquation();

        // 2. Initial point
        System.out.println();
        double x0 = readNumber("Initial x: ");
        double y0 = readNumber("Initial y: ");

        // 3. Which two of (dx, steps, final x) are known?
        System.out.println();
        System.out.println("Which two values do you know?");
        System.out.println("  1) step size (dx) and number of steps");
        System.out.println("  2) step size (dx) and final x");
        System.out.println("  3) number of steps and final x");
        int choice = readChoice("Enter 1, 2, or 3: ", 1, 3);

        double dx;
        int steps;

        if (choice == 1) {
            dx = readNonZero("Step size dx: ");
            steps = readPositiveInt("Number of steps: ");
        } else if (choice == 2) {
            dx = readNonZero("Step size dx: ");
            double xFinal = readNumber("Final x: ");
            if (xFinal == x0) {
                System.out.println("Final x is the same as initial x, so there is nothing to step. Let's start over.\n");
                return;
            }
            // Make dx point toward the final x (e.g. going from x=2 back to x=0)
            if ((xFinal - x0) * dx < 0) {
                dx = -dx;
                System.out.println("  Note: final x is to the " + (dx < 0 ? "left" : "right")
                        + " of initial x, so dx = " + fmt(dx) + " will be used.");
            }
            double exact = (xFinal - x0) / dx;
            steps = (int) Math.round(exact);
            if (steps < 1) steps = 1;
            if (Math.abs(exact - steps) > 1e-9) {
                System.out.println("  Note: dx does not divide evenly into the interval. Using " + steps
                        + " steps, which ends at x = " + fmt(x0 + steps * dx) + ".");
            }
        } else {
            steps = readPositiveInt("Number of steps: ");
            double xFinal = readNumber("Final x: ");
            if (xFinal == x0) {
                System.out.println("Final x is the same as initial x, so there is nothing to step. Let's start over.\n");
                return;
            }
            dx = (xFinal - x0) / steps;
        }

        boolean showTable = askYesNo("\nShow every step in a table? (y/n): ");

        // 4. Run Euler's Method
        List<double[]> points = euler(f, x0, y0, dx, steps);

        // 5. Output
        System.out.println();
        System.out.println("dy/dx = " + f.text + ",  start (" + fmt(x0) + ", " + fmt(y0) + "),  dx = "
                + fmt(dx) + ",  " + steps + " step" + (steps == 1 ? "" : "s"));

        if (showTable) {
            printTable(f, points);
        }

        double[] last = points.get(points.size() - 1);
        if (points.size() - 1 < steps) {
            System.out.println("\n  Stopped early at step " + (points.size() - 1)
                    + ": the slope or y-value became undefined or infinite (dividing by zero, sqrt of a negative, etc.).");
        }
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Final point: (" + fmt(last[0]) + ", " + fmt(last[1]) + ")");
        System.out.println("========================================");
    }

    // ------------------------------------------------------------------------
    //  Euler's Method itself
    //     new y = old y + dx * (slope at old point)
    //     new x = old x + dx
    // ------------------------------------------------------------------------
    static List<double[]> euler(Expression f, double x0, double y0, double dx, int steps) {
        List<double[]> points = new ArrayList<>();
        double x = x0;
        double y = y0;
        points.add(new double[] {x, y});

        for (int i = 1; i <= steps; i++) {
            double slope = f.eval(x, y);
            if (!Double.isFinite(slope)) break;

            y = y + dx * slope;
            x = x0 + i * dx;          // computed this way to avoid rounding drift

            if (!Double.isFinite(y)) break;
            points.add(new double[] {x, y});
        }
        return points;
    }

    private static void printTable(Expression f, List<double[]> points) {
        System.out.println();
        System.out.printf("%6s  %14s  %14s  %14s%n", "step", "x", "y", "dy/dx");
        System.out.println("  ----  --------------  --------------  --------------");
        for (int i = 0; i < points.size(); i++) {
            double x = points.get(i)[0];
            double y = points.get(i)[1];
            String slope = (i < points.size() - 1) ? fmt(f.eval(x, y)) : "";
            System.out.printf("%6d  %14s  %14s  %14s%n", i, fmt(x), fmt(y), slope);
        }
    }

    // ------------------------------------------------------------------------
    //  User-friendly input helpers (they re-ask instead of crashing)
    // ------------------------------------------------------------------------
    private static void printBanner() {
        System.out.println("==============================================");
        System.out.println("            EULER'S METHOD CALCULATOR");
        System.out.println("==============================================");
        System.out.println("Type the right side of  dy/dx = ...  using x and y.");
        System.out.println("Examples:   x + y      2x - y      x*y^2      sin(x) + y");
        System.out.println("            -2xy       e^x - y     sqrt(x) / (1 + y)");
        System.out.println("You can use:  + - * / ^ ( )   and   sin cos tan sqrt ln log exp abs pi e");
        System.out.println("Numbers can be fractions or expressions too, like 1/2 or pi/4.");
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!IN.hasNextLine()) {
            System.out.println("\nNo more input. Goodbye!");
            System.exit(0);
        }
        return IN.nextLine().trim();
    }

    private static Expression readEquation() {
        while (true) {
            String line = readLine("\ndy/dx = ");
            // Allow people to type the whole thing, e.g. "dy/dx = x + y" or "y' = x + y"
            int eq = line.indexOf('=');
            if (eq >= 0) line = line.substring(eq + 1).trim();
            if (line.isEmpty()) {
                System.out.println("  Please type an equation, for example: x + y");
                continue;
            }
            try {
                return new Expression(line, true);
            } catch (IllegalArgumentException e) {
                System.out.println("  Sorry, I couldn't understand that: " + e.getMessage());
                System.out.println("  Try something like:  x + y   or   2*x - 3*y");
            }
        }
    }

    private static double readNumber(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                double v = new Expression(line, false).eval(0, 0);
                if (Double.isFinite(v)) return v;
                System.out.println("  That doesn't give a real number. Please try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("  Please enter a number (like 2, -0.5, 1/3, or pi/4).");
            }
        }
    }

    private static double readNonZero(String prompt) {
        while (true) {
            double v = readNumber(prompt);
            if (v != 0) return v;
            System.out.println("  The step size can't be 0. Please try again.");
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            double v = readNumber(prompt);
            if (v >= 1 && v == Math.floor(v) && v <= 10_000_000) return (int) v;
            System.out.println("  Please enter a whole number of steps, 1 or more.");
        }
    }

    private static int readChoice(String prompt, int lo, int hi) {
        while (true) {
            String line = readLine(prompt);
            try {
                int v = Integer.parseInt(line);
                if (v >= lo && v <= hi) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("  Please enter a number from " + lo + " to " + hi + ".");
        }
    }

    private static boolean askYesNo(String prompt) {
        while (true) {
            String line = readLine(prompt).toLowerCase();
            if (line.startsWith("y")) return true;
            if (line.startsWith("n")) return false;
            System.out.println("  Please type y or n.");
        }
    }

    /** Formats a number with up to 6 decimal places and no trailing zeros. */
    static String fmt(double v) {
        if (Math.abs(v) < 5e-7) return "0";
        if (Math.abs(v) >= 1e9) return String.format("%.6e", v);
        String s = String.format("%.6f", v);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    // ========================================================================
    //  A small math-expression reader so the equation can be typed in.
    //  Supports + - * / ^, parentheses, implicit multiplication (2x, 3(x+1), xy),
    //  functions sin cos tan asin acos atan sqrt ln log exp abs, and constants pi, e.
    // ========================================================================
    interface Node {
        double eval(double x, double y);
    }

    static class Expression {
        final String text;
        private final boolean allowVars;
        private final Node root;
        private String s;
        private int pos;

        private static final String[] FUNCTIONS =
                {"asin", "acos", "atan", "sqrt", "sin", "cos", "tan", "exp", "abs", "log", "ln"};

        Expression(String text, boolean allowVars) {
            this.text = text;
            this.allowVars = allowVars;
            this.s = text.toLowerCase().replace(" ", "").replace("**", "^");
            this.pos = 0;
            if (s.isEmpty()) throw new IllegalArgumentException("nothing was entered");
            this.root = parseExpr();
            if (pos < s.length()) {
                throw new IllegalArgumentException("unexpected '" + s.charAt(pos) + "'");
            }
        }

        double eval(double x, double y) {
            return root.eval(x, y);
        }

        // expr := term (('+' | '-') term)*
        private Node parseExpr() {
            Node left = parseTerm();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '+') {
                    pos++;
                    Node a = left, b = parseTerm();
                    left = (x, y) -> a.eval(x, y) + b.eval(x, y);
                } else if (c == '-') {
                    pos++;
                    Node a = left, b = parseTerm();
                    left = (x, y) -> a.eval(x, y) - b.eval(x, y);
                } else {
                    break;
                }
            }
            return left;
        }

        // term := unary (('*' | '/' | <implicit>) unary)*
        private Node parseTerm() {
            Node left = parseUnary();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '*') {
                    pos++;
                    Node a = left, b = parseUnary();
                    left = (x, y) -> a.eval(x, y) * b.eval(x, y);
                } else if (c == '/') {
                    pos++;
                    Node a = left, b = parseUnary();
                    left = (x, y) -> a.eval(x, y) / b.eval(x, y);
                } else if (c == '(' || Character.isLetter(c) || Character.isDigit(c) || c == '.') {
                    // implicit multiplication: 2x, 3(x+1), xy, 2sin(x)
                    Node a = left, b = parseUnary();
                    left = (x, y) -> a.eval(x, y) * b.eval(x, y);
                } else {
                    break;
                }
            }
            return left;
        }

        // unary := ('-' | '+') unary | power
        private Node parseUnary() {
            if (pos < s.length() && s.charAt(pos) == '-') {
                pos++;
                Node a = parseUnary();
                return (x, y) -> -a.eval(x, y);
            }
            if (pos < s.length() && s.charAt(pos) == '+') {
                pos++;
                return parseUnary();
            }
            return parsePower();
        }

        // power := primary ('^' unary)?      (right-associative, so 2^3^2 = 2^9)
        private Node parsePower() {
            Node base = parsePrimary();
            if (pos < s.length() && s.charAt(pos) == '^') {
                pos++;
                Node exp = parseUnary();
                return (x, y) -> Math.pow(base.eval(x, y), exp.eval(x, y));
            }
            return base;
        }

        private Node parsePrimary() {
            if (pos >= s.length()) throw new IllegalArgumentException("the equation ends too early");
            char c = s.charAt(pos);

            if (c == '(') {
                pos++;
                Node inside = parseExpr();
                expect(')');
                return inside;
            }

            if (Character.isDigit(c) || c == '.') {
                int start = pos;
                while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
                double v;
                try {
                    v = Double.parseDouble(s.substring(start, pos));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("bad number '" + s.substring(start, pos) + "'");
                }
                return (x, y) -> v;
            }

            if (Character.isLetter(c)) {
                for (String fn : FUNCTIONS) {
                    if (s.startsWith(fn, pos)) {
                        pos += fn.length();
                        Node arg;
                        if (pos < s.length() && s.charAt(pos) == '(') {
                            pos++;
                            arg = parseExpr();
                            expect(')');
                        } else {
                            arg = parsePower();     // allows "sin x"
                        }
                        return function(fn, arg);
                    }
                }
                if (s.startsWith("pi", pos)) {
                    pos += 2;
                    return (x, y) -> Math.PI;
                }
                if (c == 'e') {
                    pos++;
                    return (x, y) -> Math.E;
                }
                if (c == 'x' || c == 'y') {
                    if (!allowVars) throw new IllegalArgumentException("variables aren't allowed here");
                    pos++;
                    return (c == 'x') ? (x, y) -> x : (x, y) -> y;
                }
                throw new IllegalArgumentException("unknown letter or word starting at '" + s.substring(pos) + "'");
            }

            throw new IllegalArgumentException("unexpected '" + c + "'");
        }

        private void expect(char c) {
            if (pos >= s.length() || s.charAt(pos) != c) {
                throw new IllegalArgumentException("missing '" + c + "'");
            }
            pos++;
        }

        private static Node function(String name, Node a) {
            switch (name) {
                case "sin":  return (x, y) -> Math.sin(a.eval(x, y));
                case "cos":  return (x, y) -> Math.cos(a.eval(x, y));
                case "tan":  return (x, y) -> Math.tan(a.eval(x, y));
                case "asin": return (x, y) -> Math.asin(a.eval(x, y));
                case "acos": return (x, y) -> Math.acos(a.eval(x, y));
                case "atan": return (x, y) -> Math.atan(a.eval(x, y));
                case "sqrt": return (x, y) -> Math.sqrt(a.eval(x, y));
                case "exp":  return (x, y) -> Math.exp(a.eval(x, y));
                case "abs":  return (x, y) -> Math.abs(a.eval(x, y));
                case "ln":   return (x, y) -> Math.log(a.eval(x, y));
                case "log":  return (x, y) -> Math.log10(a.eval(x, y));
                default: throw new IllegalArgumentException("unknown function " + name);
            }
        }
    }
}
