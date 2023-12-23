package minesweeper;

class NumberException extends Exception {
    String message = "There is a number there!";

    public String getMessage() {
        return message;
    }
}
