package minesweeper;

interface IndexMath {
    default int intToIndex(int value) {
        return value - 1;
    }
}
