package minesweeper;

import java.util.ArrayDeque;
import java.util.ArrayList;

import static minesweeper.Point.PointState.*;

class Game extends Minefield implements IndexMath {
    int explored = 0;
    private CommandEnum command;

    public void setCommand(CommandEnum command) {
        this.command = command;
    }

    ArrayList<Point> flags;
    private boolean isRunning;
    private int xCoordinate;
    private int yCoordinate;

    Game(int size, int mines) {
        super(size, mines);
        this.isRunning = true;
        this.flags = new ArrayList<>();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void readCoordinates(int x, int y) {
        this.xCoordinate = intToIndex(x);
        this.yCoordinate = intToIndex(y);
    }

    private void explore(Point point) throws MineException {
        if (point.hasMine()) {
            throw new MineException();
        }
        ArrayDeque<Point> stack = new ArrayDeque<>();
        stack.add(point);
        while (!stack.isEmpty()) {
            Point localPoint = stack.pop();
            SizeLimit limit = new SizeLimit(localPoint.getY(), localPoint.getX(), intToIndex(this.minefield.length), intToIndex(this.minefield[0].length));
            for (int y = limit.yStartClamped; y <= limit.yEndClamped; y++) {
                for (int x = limit.xStartClamped; x <= limit.xEndClamped; x++) {
                    Point checkedPoint = this.minefield[y][x];
                    if (checkedPoint.state != EXPLORED && !checkedPoint.hasMine()) {
                        this.minefield[y][x].state = EXPLORED;
                        explored++;
                        if (checkedPoint.getSurroundingMines() == 0) {
                            stack.add(checkedPoint);
                        }
                    }
                }
            }
        }
    }

    public void update() throws NumberException, MineException {
        switch (this.command) {
            case MINE:
                if (this.minefield[yCoordinate][xCoordinate].state == MARKED) {
                    this.removeFlag();
                } else {
                    this.setFlag();
                }
                if ((this.flags.size() == this.totalMines.size()) && this.flags.containsAll(this.totalMines)) {
                    this.isRunning = false;
                }
                break;
            case FREE:
                this.explore(this.minefield[yCoordinate][xCoordinate]);
                if (this.explored == this.minefield.length * this.minefield[0].length - this.totalMines.size()) {
                    this.isRunning = false;
                }
                break;
        }
    }

    private void setFlag() throws NumberException {
        if (super.minefield[yCoordinate][yCoordinate].getSurroundingMines() > 0 && super.minefield[yCoordinate][xCoordinate].state == EXPLORED) {
            throw new NumberException();
        }
        super.minefield[this.yCoordinate][this.xCoordinate].state = MARKED;
        flags.add(super.minefield[this.yCoordinate][xCoordinate]);
    }

    private void removeFlag() {
        super.minefield[this.yCoordinate][this.xCoordinate].state = UNMARKED;
        flags.remove(super.minefield[this.yCoordinate][xCoordinate]);
    }
}
