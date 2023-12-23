package minesweeper;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

class Minefield implements IndexMath {
    static private final Random random = new Random();
    protected Point[][] minefield;
    protected ArrayList<Point> totalMines;

    Minefield(int size, int mines) {
        this.minefield = new Point[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.minefield[i][j] = new Point(i, j);
            }
        }
        this.randomize(mines);
        this.placeMines();
        this.updateSurroundingFields();
    }

    private void updateSurroundingFields() {
        for (Point mine : this.totalMines) {
            SizeLimit limit = new SizeLimit(mine.getY(), mine.getX(), intToIndex(this.minefield.length), intToIndex(this.minefield[0].length));
            for (int y = limit.yStartClamped; y <= limit.yEndClamped; y++) {
                for (int x = limit.xStartClamped; x <= limit.xEndClamped; x++) {
                    if (!this.minefield[y][x].hasMine()) {
                        this.minefield[y][x].increaseSurroundingMines();
                    }
                }
            }
        }
    }

    private void randomize(int mines) {
        ArrayList<Point> totalMines = new ArrayList<>();
        while (mines > totalMines.size()) {
            int randomX = Minefield.random.nextInt(minefield.length);
            int randomY = Minefield.random.nextInt(minefield.length);
            Point localPoint = new Point(randomY, randomX);
            localPoint.setMine();
            Predicate<Point> localPredicate = i -> (i.equals(localPoint));
            if (totalMines.stream().noneMatch(localPredicate)) {
                totalMines.add(localPoint);
            }
        }
        this.totalMines = totalMines;
    }

    private void placeMines() {
        for (Point mine : this.totalMines) {
            this.minefield[mine.getY()][mine.getX()] = mine;
        }
    }

    public String display(DisplayMode mode) {
        StringBuilder out = new StringBuilder(" |");
        for (int i = 0; i < this.minefield.length; i++) {
            out.append(i + 1);
        }
        out.append("|\n-|");
        out.append("-".repeat(this.minefield.length));
        out.append("|\n");
        for (int i = 0; i < this.minefield[0].length; i++) {
            out.append(i + 1).append("|");
            for (int j = 0; j < this.minefield.length; j++) {
                out.append(this.minefield[i][j].returnCharacter(mode));
            }
            out.append("|\n");
        }
        out.append("-|");
        out.append("-".repeat(this.minefield.length));
        out.append("|");
        return out.toString();
    }
}
