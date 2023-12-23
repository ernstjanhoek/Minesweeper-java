package minesweeper;

import java.util.*;
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