

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WorldGUI extends Application {
    static final int WORLD_SIZE_X = 100;
    static final int WORLD_SIZE_Y = 100;
    static final int CELL_SIZE = 9;

    World world = new World(WORLD_SIZE_X, WORLD_SIZE_Y);
    Rectangle[][] squares = new Rectangle[world.sizeX][world.sizeY];
    String currentState;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("World");
        GridPane root = new GridPane();
        Scene scene = new Scene(root, world.sizeX * CELL_SIZE, world.sizeY * CELL_SIZE);
        primaryStage.setScene(scene);

        /*
         * Create an array of Rectangles in a world.sizeX*world.sizeY grid,
         * representing each location in the world.
         */
        for (int x = 0; x < world.sizeX; x++) {
            for (int y = 0; y < world.sizeY; y++) {
                squares[x][y] = new Rectangle(0, 0, CELL_SIZE, CELL_SIZE);
                squares[x][y].setStrokeWidth(2.0);
                root.add(squares[x][y], x, y);
            }
        }

        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(200),
                ae -> {
                    world.update();
                    String currentState = world.getCurrentState();
                    if (currentState != null) {
                        for (int x = 0; x < world.sizeX; x++) {
                            for (int y = 0; y < world.sizeY; y++) {
                                setSquareColor(currentState, x, y);
                            }
                        }
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Given a state string representing the current state of the world,
     * extract the three characters representing the state of square (x,y)
     * and set the color+stroke color of the Rectangle representing that
     * square.
     *
     * @param currentState a state string representing the world
     * @param x            the x coordinate [0..world.sizeX-1] of the square
     * @param y            the y coordinate [0..world.sizeY-1] of the square
     */
    private void setSquareColor(String currentState, int x, int y) {
        Color color;
        int grassCharPos = (x * world.sizeY + y) * 3;
        char grassChar = currentState.charAt(grassCharPos);
        if (grassChar < '0' || grassChar > '5') {
            throw new IllegalStateException("Invalid grass char " + grassChar + " at position " + grassCharPos + " in current state " + currentState);
        }
        int grassAmount = grassChar - '0';
        Color grassColor = Color.rgb(245 - 49 * grassAmount, 220 - 24 * grassAmount, 180 - 36 * grassAmount);
        squares[x][y].setStroke(grassColor);
        int animalCharPos = (x * world.sizeY + y) * 3 + 1;
        char animalChar = currentState.charAt(animalCharPos);
        if (animalChar == ' ') {
            // no animal, only grass
            color = grassColor;
        } else {
            int hungerCharPos = (x * world.sizeY + y) * 3 + 2;
            char hungerChar = currentState.charAt(hungerCharPos);
            if (hungerChar < '0' || hungerChar > '9') {
                throw new IllegalStateException("Invalid hunger char " + hungerChar + " at position " + hungerCharPos + " in current state " + currentState);
            }
            int hunger = hungerChar - '0';
            if (animalChar == 'f') {
                // fox
                color = Color.DARKORANGE;
                if (hunger > 5) {
                    color = Color.ORANGE;
                }
            } else if (animalChar == 'r') {
                // rabbit
                color = Color.GRAY;
                if (hunger > 3) {
                    color = Color.LIGHTGRAY;
                }
            } else {
                throw new IllegalStateException("Invalid animal char " + animalChar + " at position " + animalCharPos + " in current state " + currentState);
            }
        }
        squares[x][y].setFill(color);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
