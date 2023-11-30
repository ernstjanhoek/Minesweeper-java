package minesweeper;

import java.util.*;
import java.util.function.Predicate;

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
class SizeLimit  {
    int padding = 1;
    int yStartClamped;
    int yEndClamped;
    int xStartClamped;
    int xEndClamped;
    SizeLimit(int yValue, int xValue, int yLength, int xLength) {
        this.yStartClamped = Math.max(yValue - padding, 0);
        this.yEndClamped = Math.min(yValue + padding, yLength);
        this.xStartClamped = Math.max(xValue - padding, 0);
        this.xEndClamped = Math.min(xValue + padding, xLength);
    }
}
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
enum DisplayMode {
    OPEN, FOW,
}
enum CommandEnum {
    FREE, MINE
}
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
    public void readCoordinates(int x, int y)  {
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
        switch(this.command) {
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
interface IndexMath {
    default int intToIndex(int value) {
        return value - 1;
    }
}
class NumberException extends Exception {
    String message = "There is a number there!";
    public String getMessage() {
        return message;
    }
}
class MineException extends Exception {
    String message = "You stepped on a mine and failed!";
    public String getMessage() {
        return message;
    }
}
public class Main {
    public static void main(String[] args) {
        Scanner scanner  = new Scanner(System.in);
        int size = 9;
        System.out.println("How many mines do you want on the field?");
        int mines = Integer.parseInt(scanner.nextLine());
        Game game = new Game(size, mines);
        System.out.println(game.display(DisplayMode.FOW));
        while (game.isRunning()) {
            System.out.print("Set/delete mines marks(x and y coordinates): ");
            String input = scanner.nextLine();
            String[] inputLine = input.split(" ");
            try {
                int xInput = Integer.parseInt(inputLine[0]);
                int yInput = Integer.parseInt(inputLine[1]);
                game.setCommand(CommandEnum.valueOf(inputLine[2].toUpperCase()));
                game.readCoordinates(xInput, yInput);
                game.update();
                System.out.println(game.display(DisplayMode.FOW));
            } catch (NumberException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (MineException e) {
                System.out.println(game.display(DisplayMode.OPEN));
                System.out.println(e.getMessage());
                break;
            }
        }
        if (!game.isRunning()) {
            System.out.println("Congratulations! You found all mines!");
        }
    }
}