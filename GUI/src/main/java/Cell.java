import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class Cell extends Rectangle {
    public int x, y;
    public Ship ship = null;
    public boolean wasShot = false;


    private Board board;


    public Cell(int x, int y, Board board) {
        super(30, 30);
        this.x = x;
        this.y = y;
        this.board = board;
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }


    public boolean shoot() {
        wasShot = true;
        setFill(Color.web("#0c2730"));


        if (ship != null) {
            ship.hit();
            setFill(Color.web("#a10b2d"));
            setStroke(Color.TRANSPARENT);


            if (!ship.isAlive()) {
                board.ships--;


            }
            return true;
        }


        return false;
    }
}
