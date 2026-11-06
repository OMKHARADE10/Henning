# Euler's Method Calculator

Approximates the solution of a differential equation `dy/dx = f(x, y)` using Euler's Method.

## How to run

### On GitHub (no install needed)

1. On the repository page, click the green **Code** button → **Codespaces** tab → **Create codespace on main**.
2. Wait for it to finish setting up (the first time takes a minute or two).
3. In the terminal at the bottom, type:
   ```
   java EulersMethod.java
   ```
   and press Enter. Answer the questions in that terminal.

   (Or open `EulersMethod.java` and click **Run** above `main`, or press **F5**.)

Note: pressing `.` on the repo opens github.dev, which can only edit files and **cannot run code**. Use a Codespace instead.

### On a Mac

**Easiest:** double-click `run.command` in Finder.
(The first time, macOS may block it. If so, right-click it, choose **Open**, then click **Open** again.)

**From a terminal:**
```
./run.command
```
or, if Java is on your PATH:
```
java EulersMethod.java
```

Requires Java 11 or newer. To install it on a Mac: `brew install openjdk` (or download from https://adoptium.net).

## What it asks for

1. **The equation**: type the right side of `dy/dx = ...`, e.g. `x + y`, `2x - y`, `x*y^2`, `sin(x) + y`, `e^x - y`.
   Supported: `+ - * / ^ ( )`, `sin cos tan asin acos atan sqrt ln log exp abs`, `pi`, `e`.
   Multiplication can be implied: `2x`, `3(x+1)`, `xy`.
2. **The initial point**: x and y.
3. **Any two of**: step size (dx), number of steps, final x.
4. Whether to show a table of every step.

Number inputs accept fractions and expressions too, like `1/2` or `pi/4`.

## Output

The final (x, y) point and, if you want it, a table listing every step's x, y, and slope (dy/dx).

## Example

```
dy/dx = x + y
Initial x: 0
Initial y: 1
Enter 1, 2, or 3: 1
Step size dx: 0.1
Number of steps: 10
...
  Final point: (1, 3.187485)
```
