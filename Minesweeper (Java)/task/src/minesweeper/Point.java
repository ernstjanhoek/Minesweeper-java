package minesweeper;

import static minesweeper.Point.PointState.*;

class Point {
    enum PointState {
        MARKED, UNMARKED, EXPLORED
    }

    boolean mine;
    private final int x;
    private final int y;
    private int surroundingMines;

    protected char returnCharacter(DisplayMode mode) {
        if (mode != DisplayMode.FOW) {
            if (this.hasMine()) {
                return 'X';
            }
        }
        return characterValues();
    }

    private char characterValues() {
        if (this.state == EXPLORED) {
            if (this.surroundingMines == 0) {
                return '/';
            } else {
                return (char) (surroundingMines + '0');
            }
        }
        if (this.state == MARKED) {
            return '*';
        }
        if (this.state == UNMARKED) {
            return '.';
        }
        return 'D';
    }

    protected void setMine() {
        this.mine = true;
    }

    protected boolean hasMine() {
        return this.mine;
    }

    PointState state;

    public int getSurroundingMines() {
        return this.surroundingMines;
    }

    protected void increaseSurroundingMines() {
        this.surroundingMines++;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    Point(int y, int x) {
        this.state = UNMARKED;
        this.x = x;
        this.y = y;
        this.surroundingMines = 0;
    }

    protected boolean equals(Point otherPoint) {
        return (this.y == otherPoint.getY() && this.x == otherPoint.getX() && this.mine == otherPoint.mine);

    }
}
