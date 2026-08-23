import java.util.ArrayList;

/**
 * Represents a single wheel of the slot machine.
 * Each wheel owns its own list of symbols and its visual 
 * representation: a background Rectangle (slot) and a Traingle
 * that shows the currently visible symbol (visibleShape). Both
 * shapes are created once, with a fixed size; only their color changes when the wheel spins
 * 
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
*/
public class Wheel {
    public final static int CELL_SIZE = 70; 
    public final static int SYMBOL_SIZE = 50;
    public final static int SYMBOL_OFFSET_X = 7 * 10 / 2; 
    public final static int SYMBOL_OFFSET_Y = 10;
    public final static int GAP = 10;
    private ArrayList<String> symbols;
    private int currentIndex;
    private boolean visible;
    private int posX;
    private int posY;
    private Rectangle slot;
    private Triangle visibleShape;

    public Wheel(int posX, int posY) {
        symbols = new ArrayList<String>();
        currentIndex = -1;
        visible = false;

        slot = new Rectangle();
        slot.changeColor("black");
        slot.changeSize(CELL_SIZE, CELL_SIZE);
        

        visibleShape = new Triangle();
        visibleShape.changeColor("yellow");
        visibleShape.changeSize(SYMBOL_SIZE, SYMBOL_SIZE);
        
        accommodate(posX,posY);
        this.posX = posX;
        this.posY = posY;

        slot.makeVisible();
        visibleShape.makeVisible();
    }

    public void addSymbol(int pos, String color){
        if (pos<1){
            symbols.add(0,color);
        }
        else if(pos >= symbols.size()){
            symbols.add(color);
        }
        else {
            symbols.add(pos-1, color);
        }

        if (currentIndex == -1){
            currentIndex = 0;
        }
        refreshShape();
    }

    public void delSymbol(String color){
        int index = symbols.indexOf(color);
        if (currentIndex == index){
            if (currentIndex == 0){
                currentIndex = 1;
            }else{
                currentIndex -=1;
            }
            refreshShape();
        }
        symbols.remove(index);
    }

    public void placeSymbol(String color){
        int index = symbols.indexOf(color);
        currentIndex = index;
        refreshShape();
    }

    public void spin(){
        if (currentIndex == symbols.size()-1){
            currentIndex = 0;
        }else{
            currentIndex += 1;
        }
        refreshShape();
    }

    public String[] symbols(){
        return symbols.toArray(String[]::new); // // Returns the symbols as a String array
    }

    public String visibleSymbol(){
        return symbols.get(currentIndex);
    }

    public void makeVisible(){
        visible = true;
        slot.makeVisible();
        visibleShape.makeVisible();
    }

    public void makeInvisible(){
        visible = false;
        visibleShape.makeInvisible();
        slot.makeInvisible();
    }

    private void refreshShape(){
        visibleShape.changeColor(symbols.get(currentIndex));
    }

private void accommodate(int newPosX, int newPosY) {
        int oldTargetX = 0;
        int oldTargetY = 0;
        if (posX!=0 && posY!=0){
            oldTargetX = posX * (CELL_SIZE + GAP) + GAP;
            oldTargetY = posY * (CELL_SIZE + GAP) + GAP;
        }
 
        int newTargetX = newPosX * (CELL_SIZE + GAP) + GAP;
        int newTargetY = newPosY * (CELL_SIZE + GAP) + GAP;
 
        int deltaX = newTargetX - oldTargetX;
        int deltaY = newTargetY - oldTargetY;
 
        slot.moveHorizontal(deltaX);
        slot.moveVertical(deltaY);
        if (posX==0 && posY==0){
            deltaX +=SYMBOL_OFFSET_X;
            deltaY +=SYMBOL_OFFSET_Y;
        }
        visibleShape.moveHorizontal(deltaX);
        visibleShape.moveVertical(deltaY);
        posX = newPosX;
        posY = newPosY;
    }
 
    /**
     * Moves this wheel one grid position forward (move == 1) or
     * backward (move == -1), wrapping to the next/previous row
     * when it falls off the edge of the grid. Delegates to
     * accommodate(int, int) with the newly computed position.
     */
    public void accommodate(int move) {
        int newPosX = posX;
        int newPosY = posY;
        if (move==-1){
            if (posX == 1 && posY != 1) {
                newPosX = 14;
                newPosY = posY - 1;
            } else if (posX != 1) {
                newPosX = posX - 1;
            }
        }else{
            if (posX ==14 && posY!=9){
                newPosX = 1;
                newPosY = posY + 1;
            }
            else if(posX!=14){
                newPosX = posX + 1;
            }
        }
        accommodate(newPosX, newPosY);
    }
}