import javafx.scene.Parent;
import javafx.scene.layout.VBox;




public class Ship extends Parent {
    public int type;
    public boolean vertical = true;


    private int health;


    private VBox vbox;


    public Ship(int type, boolean vertical) {
        this.type = type; //how many cells it occupies
        this.vertical = vertical;
        health = type;
    }


    public void hit() {
        health--;
    }


    public boolean isAlive() {
        return health > 0;
    }
}
