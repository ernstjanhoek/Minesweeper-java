package minesweeper;

class SizeLimit {
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
