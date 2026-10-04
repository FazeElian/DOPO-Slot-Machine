import java.util.ArrayList;

/**
 * Represents a single wheel of the slot machine.
 * Each wheel owns its own list of Symbol objects and a background
 * Rectangle (slot). Every Symbol carries its own figure; only the
 * figure of the symbol at the current index is shown, so spinning
 * the wheel hides the previous figure and shows the new one.
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
    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private boolean visible;
    private int posX;
    private int posY;
    private Rectangle slot;
    // Symbol whose figure is currently drawn (null if none)
    private Symbol shownSymbol;
    private boolean locked;

    /**
     * Constructs the object for every wheel of the board
     * @param posX the position in X axis of the board
     * @param posY the position in Y axis of the board
     */
    public Wheel(int posX, int posY) {
        currentIndex = -1;
        visible = false;
        symbols = new ArrayList<Symbol>();

        slot = new Rectangle();
        slot.changeColor("black");
        slot.changeSize(CELL_SIZE, CELL_SIZE);
        locked = false;

        accommodate(posX,posY);
        this.posX = posX;
        this.posY = posY;

        // The wheel starts with its own copy of every registered symbol
        for (int i = 0; i < Symbol.symbols.size(); i++) {
            addSymbol(i);
        }
    }

    /**
     * Inserts into the wheel's cycle its own copy of the catalog symbol
     * at the given index, adjusting the current index if the new symbol was
     * inserted before or at the wheel's current position, so the visible
     * symbol does not change.
     * @param index 0-based position of the symbol in Symbol.symbols
     */
    public void addSymbol(int index){
        String name = Symbol.symbols.get(index).getName();
        Symbol symbol = new Symbol(name, symbolX(), symbolY());
        symbols.add(index, symbol);
        if (currentIndex == -1){
            currentIndex = 0;
        }else if (currentIndex >= index){
            currentIndex+=1;
        }
        refreshShape();
    }

    /**
     * Removes a symbol from the wheel's cycle. If the removed symbol was
     * the visible one, the wheel shows the previous symbol (or the next
     * one when it was the first). The last symbol can't be removed.
     * @param color string value of the symbol's color
     */
    public void delSymbol(String color){
        int index = getIndexOfSymbol(color);
        if (index == -1 || symbols.size() <= 1) return;
        Symbol removed = symbols.remove(index);
        removed.makeInvisible();
        if (shownSymbol == removed) shownSymbol = null;
        if (currentIndex > index || (currentIndex == index && index > 0)){
            currentIndex -= 1;
        }
        refreshShape();
    }

    /**
     * Updates the value of the index of the current wheel
     * @param color string value of the symbol's color
     */
    public void placeSymbol(String color){
        int index = getIndexOfSymbol(color);
        if (index != -1) currentIndex = index;
        else {
            if (visible)MessageUtil.showError("Ese símbolo no existe, añádelo e intenta de nuevo.");
        }
        refreshShape();
    }
    /**
     * Changes the wheel's visible symbol by one position in the given
     * direction: forward (direction == 1) advances to the next symbol,
     * wrapping to the first when at the end; backward (direction == -1)
     * moves to the previous symbol, wrapping to the last when at the start.
     *
     * @param direction 1 to advance one step, -1 to go back one step
     */
    public void spin(int direction) {
        if (symbols.isEmpty()) return;
        if (direction >= 0) {
            if (currentIndex == symbols.size() - 1) {
                currentIndex = 0;
            } else {
                currentIndex += 1;
            }
        } else {
            if (currentIndex == 0) {
                currentIndex = symbols.size() - 1;
            } else {
                currentIndex -= 1;
            }
        }
        refreshShape();
    }

    /**
     * Change the wheel ("spin" the board) going to the next one on the slot.
     * Equivalent to spin(1).
     */
    public void spin(){
        spin(1);
    }


    /**
     * Return the symbol located on the current index
     */
    public String visibleSymbol(){
        return symbols.get(currentIndex).getName();
    }

    /**
     * Show the wheel: the symbol and the slot which it
     * is located
     */
    public void makeVisible(){
        visible = true;
        slot.makeVisible();
        if (shownSymbol != null) shownSymbol.makeVisible();
    }

    /**
     * Hide the wheel: the symbol and the slot which it
     * is located
     */
    public void makeInvisible(){
        visible = false;
        if (shownSymbol != null) shownSymbol.makeInvisible();
        slot.makeInvisible();
    }
    /**
     * Hides the figure that was shown and shows the figure of the
     * symbol located on the current index
     */
    private void refreshShape(){
        Symbol current = symbols.isEmpty() ? null : symbols.get(currentIndex);
        if (current == shownSymbol) return;
        if (shownSymbol != null) shownSymbol.makeInvisible();
        shownSymbol = current;
        if (visible && shownSymbol != null) shownSymbol.makeVisible();
    }

    // Pixel coordinates where the figure of a symbol of this wheel is drawn
    private int symbolX(){
        return posX * (CELL_SIZE + GAP) + GAP + SYMBOL_OFFSET_X;
    }

    private int symbolY(){
        return posY * (CELL_SIZE + GAP) + GAP + SYMBOL_OFFSET_Y;
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
        for (Symbol symbol : symbols) {
            symbol.moveHorizontal(deltaX);
            symbol.moveVertical(deltaY);
        }
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

    // Pauses execution briefly so the step-by-step movement can be visibly evidenced
    private void pause(){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Moves the slot and every symbol of this wheel horizontally and then
     * pauses briefly, so the movement can be seen step by step.
     *
     * @param distance number of pixels to move; negative values move left
     */
    public void slowMoveHorizontal(int distance) {
        slot.moveHorizontal(distance);
        for (Symbol symbol : symbols) symbol.moveHorizontal(distance);
        pause();
    }

    /**
     * Moves the slot and every symbol of this wheel vertically and then
     * pauses briefly, so the movement can be seen step by step.
     *
     * @param distance number of pixels to move; negative values move up
     */
    public void slowMoveVertical(int distance) {
        slot.moveVertical(distance);
        for (Symbol symbol : symbols) symbol.moveVertical(distance);
        pause();
    }

    /**
     * Returns the index of the symbol on the wheel according to its value,
     * or -1 if the wheel doesn't have it
     * @param symbol string value of the symbol
     */
    private int getIndexOfSymbol(String symbol) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getName().equals(symbol)) return i;
        }
        return -1;
    }

    /**
     * Modify the coordiinates of the wheel to the new values
     * @param posX the new position in X axis of the board
     * @param posY the new position in Y axis of the board
     */
    public void setLocation(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Locks this wheel, preventing it from being spun or modified
     * until it is explicitly unlocked.
     */
    public void lock(){
        locked = true;
    }
    /**
     * Unlocks this wheel, allowing it to be spun or modified again.
     */
    public void unlock(){
        locked=false;
    }

    /**
     * Returns whether this element is currently locked.
     *
     * @return true if locked, false otherwise
     */
    public boolean isLocked(){
        return locked;
    }
    
    /**
     * Sets the wheel located immediately to the left of this one.
     * The base implementation ignores it, since a normal wheel does not depend
     * on its neighbors. Subclasses such as {@link LeftyWheel} override it.
     *
     * @param left the wheel to the left, or null if there is none
     */
    public void setLeftNeighbor(Wheel left) {}
    
    /**
     * Indicates whether this wheel is of the "rebel" type, which cannot be
     * locked, swapped or removed.
     *
     * @return true if this wheel is a rebel wheel, false otherwise
     */
    public boolean isRebel() {
        return false;
    }
    
    /**
     * Indicates whether this wheel is of the "lefty" type, which copies the
     * visible symbol of its left neighbor when spun.
     *
     * @return true if this wheel is a lefty wheel, false otherwise
     */
    public boolean isLefty() {
        return false;
    }
}