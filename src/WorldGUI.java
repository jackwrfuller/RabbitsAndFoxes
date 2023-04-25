

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WorldGUI extends Application {
    static final int WORLD_SIZE_X = 100;
    static final int WORLD_SIZE_Y = 100;
    static final int CELL_SIZE = 8;

    static final int BUTTON_SPACE = (int) (WORLD_SIZE_Y * CELL_SIZE * 0.2);

    private static final int WINDOW_WIDTH = WORLD_SIZE_X * CELL_SIZE;
    private static final int WINDOW_HEIGHT = WORLD_SIZE_Y * CELL_SIZE + BUTTON_SPACE;

    // The margins used for all visual assets
    private static final int MARGIN_X = 100;
    private static final int MARGIN_Y = 50;
    /*
 Distance to leave from a button to the right - used for setting up
 all the buttons at the bottom of the window.
 */
    private static final double BUTTON_BUFFER = 50.0;

    // Group containing all the buttons, sliders and text used in this application.
    private final Group controls = new Group();
    // Group containing the game display
    private final Group display = new Group();


    World world = new World(WORLD_SIZE_X, WORLD_SIZE_Y);
    Rectangle[][] squares = new Rectangle[world.sizeX][world.sizeY];
    String currentState;

    Timeline timeline;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Rabbits and Foxes");
        GridPane root = new GridPane();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
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

        root.getChildren().add(this.controls);

        this.makeControls();
        primaryStage.show();

        timeline = new Timeline(new KeyFrame(
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

    public void pause() {
        this.timeline.pause();
    }

    public void play() {
        this.timeline.play();
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

    public void makeControls() {
        Button pause = new Button();
        pause.setLayoutX(MARGIN_X);
        pause.setLayoutY(1000);
        pause.setOnAction(event -> this.pause()); // Lambda expression
        pause.setText("Pause");
        this.controls.getChildren().add(pause);

        Button play = new Button();
        play.setLayoutX(pause.getLayoutX() + BUTTON_BUFFER);
        play.setLayoutY(1000);
        play.setOnAction(event -> this.play());
        play.setText("Play");
        this.controls.getChildren().add(play);

    



    }



    public static void main(String[] args) {
        launch(args);
    }

}
