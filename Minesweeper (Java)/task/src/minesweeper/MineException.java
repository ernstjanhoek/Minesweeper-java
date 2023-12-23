package minesweeper;

class MineException extends Exception {
    String message = "You stepped on a mine and failed!";

    public String getMessage() {
        return message;
    }
}
