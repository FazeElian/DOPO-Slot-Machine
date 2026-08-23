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

    /**
     * Constructs the object for every wheel of the board
     * @param posX the position in X axis of the board
     * @param posY the position in Y axis of the board
     */
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

    }

    /**
     * Adds a symbol to the symbols array for all
     * the wheels of the board
     * @param pos
     * @param color
     */
    public void addSymbol(int pos, String color){
        if (pos<1){
            MessageUtil.showWarning("La posición del nuevo símbolo no puede ser inferior a 1.");
            symbols.add(0,color);
        }
        else if(pos > symbols.size()){
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

    /**
     * Removes a symbol to the symbols array for all
     * the wheels of the board
     * @param color string value of the symbol's color
     */
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

    /**
     * Updates the value of the index of the current wheel
     * @param color string value of the symbol's color
     */
    public void placeSymbol(String color){
        int index = symbols.indexOf(color);
        currentIndex = index;
        refreshShape();
    }

    /**
     * Change the wheel ("spin" the board) going to
     * the next one on the slot
     */
    public void spin(){
        if (currentIndex == symbols.size() - 1){
            currentIndex = 0;
        }else{
            currentIndex += 1;
        }
        refreshShape();
    }

    /**
     * Return the list of symbols on the board
     */
    public String[] symbols(){
        return symbols.toArray(String[]::new);
    }

    /**
     * Return the symbol located on the current index
     */
    public String visibleSymbol(){
        return symbols.get(currentIndex);
    }

    /**
     * Show the wheel: the symbol and the slot which it
     * is located
     */
    public void makeVisible(){
        visible = true;
        slot.makeVisible();
        visibleShape.makeVisible();
    }

    /**
     * Hide the wheel: the symbol and the slot which it
     * is located
     */
    public void makeInvisible(){
        visible = false;
        visibleShape.makeInvisible();
        slot.makeInvisible();
    }
    /**
     * Updates the color of the visible shape on the screen
     * according to the symbol that is located on the 
     * current index
     */
    private void refreshShape(){
        visibleShape.changeColor(symbols.get(currentIndex));
    }

    /**
     * Repositions the slot and its associated visual shape on the canvas based on grid coordinates.
     * Translates the grid matrix positions into screen pixel coordinates using the cell size, 
     * gap spacing, and offset adjustments, then updates the internal position state.
     * @param newPosX the target column (X index) in the grid matrix
     * @param newPosY the target row (Y index) in the grid matrix
     */
    private void accommodate(int newPosX, int newPosY) {
        int oldTargetX = 0;
        int oldTargetY = 0;
        if (posX!=0 && posY!=0){
            // Convert the positions of the grid to coordinates on the
            // screen using the constant CELL_SIZE and the space between them
            // which is GAP
            oldTargetX = posX * (CELL_SIZE + GAP) + GAP;
            oldTargetY = posY * (CELL_SIZE + GAP) + GAP;
        }
 
        // Convert the positions of the grid to coordinates on the
        // screen using the constant CELL_SIZE and the space between them
        // which is GAP
        int newTargetX = newPosX * (CELL_SIZE + GAP) + GAP;
        int newTargetY = newPosY * (CELL_SIZE + GAP) + GAP;
 
        // Substract the previous position to know how many
        // pixels should move the shape
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