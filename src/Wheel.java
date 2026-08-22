import java.util.ArrayList;

public class Wheel {
    public final static int CELL_SIZE = 70;
    public final static int GAP = 10;

    private ArrayList<String> symbols;
    private int currentIndex;
    private boolean visible;
    private Rectangle slot;
    private Triangle visibleShape;

    public Wheel(int posX, int posY){
        symbols = new ArrayList<String>();
        currentIndex = 1;
        visible = false;

        slot = new Rectangle();
        slot.changeSize(CELL_SIZE, CELL_SIZE);
        slot.moveHorizontal(posX*CELL_SIZE);
        slot.moveVertical(posY*CELL_SIZE);
        slot.makeVisible();

        visibleShape = new Triangle();
        visibleShape.changeSize(50,50);
        visibleShape.moveHorizontal((posX*CELL_SIZE)+(7*GAP/2));
        visibleShape.moveVertical((posX*CELL_SIZE)+GAP);
        visibleShape.makeVisible();
    }

}

