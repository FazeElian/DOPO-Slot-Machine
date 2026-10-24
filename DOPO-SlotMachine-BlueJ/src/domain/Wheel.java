package domain;

import presentation.Rectangle;

import java.util.ArrayList;

/**
 * A wheel of the slot machine: a background slot (Rectangle) and its own
 * Symbols. Only the figure of the symbol at the current index is shown, so
 * spinning hides the previous figure and shows the new one.
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
*/
public class Wheel {
    /** Side in pixels of the square slot of a wheel. */
    public final static int CELL_SIZE = 70; 
    /** Horizontal offset of the symbol's figure inside the slot. */
    public final static int SYMBOL_OFFSET_X = 7 * 10 / 2; 
    /** Vertical offset of the symbol's figure inside the slot. */
    public final static int SYMBOL_OFFSET_Y = 10;
    /** Space in pixels between two slots. */
    public final static int GAP = 10;
    /** Symbols of this wheel, in the order they are cycled. */
    private final ArrayList<Symbol> symbols;
    /** Index of the symbol currently shown, or -1 if there are none. */
    private int currentIndex;
    /** Whether the wheel is drawn on the canvas. */
    private boolean visible;
    /** Column of the wheel in the grid. */
    private int posX;
    /** Row of the wheel in the grid. */
    private int posY;
    /** Black square drawn behind the symbol. */
    private final Rectangle slot;
    /** Symbol whose figure is currently drawn (null if none). */
    private Symbol shownSymbol;
    /** Whether the wheel is locked and can't be spun or modified. */
    private boolean locked;

    /**
     * Constructs the object for every wheel of the board
     * @param posX the position in X axis of the board
     * @param posY the position in Y axis of the board
     */
    public Wheel(final int posX, final int posY) {
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
            addSymbol(i, Symbol.symbols.get(i));
        }
    }

    /**
     * Inserts into the wheel's cycle its own copy of the given registered
     * symbol (same type, figure placed on this wheel), adjusting the current
     * index if the new symbol was inserted before or at the wheel's current
     * position, so the visible symbol does not change.
     * @param index 0-based position of the symbol in Symbol.symbols
     * @param registered the symbol of Symbol.symbols to copy
     */
    public final void addSymbol(final int index, final Symbol registered){
        final Symbol symbol = registered.copyAt(symbolX(), symbolY());
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
    public void delSymbol(final String color){
        final int index = getIndexOfSymbol(color);
        if (index == -1 || symbols.size() <= 1) {
            return;
        }
        final Symbol removed = symbols.remove(index);
        removed.makeInvisible();
        if (shownSymbol == removed) {
            shownSymbol = null;
        }
        if (currentIndex > index || (currentIndex == index && index > 0)){
            currentIndex -= 1;
        }
        refreshShape();
    }

    /**
     * Updates the value of the index of the current wheel
     * @param color string value of the symbol's color
     * @throws SlotMachineException if the wheel is locked or doesn't have
     *         that symbol
     */
    public void placeSymbol(final String color) throws SlotMachineException {
        checkUnlocked();
        final int index = getIndexOfSymbol(color);
        if (index == -1) {
            throw new SlotMachineException(String.format(SlotMachineException.SYMBOL_NOT_FOUND, color));
        }
        currentIndex = index;
        symbols.get(index).selected();
        refreshShape();
    }
    
    /**
     * Moves the visible symbol one position forward (direction == 1) or
     * backward (direction == -1), wrapping around at the ends.
     *
     * @param direction 1 to advance one step, -1 to go back one step
     * @throws SlotMachineException if the wheel is locked or a subclass
     *         can't complete the spin
     */
    public void spin(final int direction) throws SlotMachineException {
        checkUnlocked();
        if (symbols.isEmpty()) {
            return;
        }
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
        notifySpin();
        symbols.get(currentIndex).selected();
        refreshShape();
    }

    /**
     * Tells every symbol of this wheel that the wheel has just spun, so the
     * symbols that react to spins (like EphemeralSymbol or DizzySymbol) can
     * change. Subclasses that spin in their own way (like LeftyWheel) must
     * call it too.
     */
    protected void notifySpin() {
        for (final Symbol s : symbols) {
            s.spun();
        }
    }

    /**
     * Change the wheel ("spin" the board) going to the next one on the slot.
     * Equivalent to spin(1).
     * @throws SlotMachineException if a subclass can't complete the spin
     */
    public void spin() throws SlotMachineException {
        spin(1);
    }


    /**
     * Return the symbol located on the current index
     */
    public String visibleSymbol(){
        return symbols.get(currentIndex).getName();
    }

    /**
     * Returns the Symbol object this wheel is showing, so its state (size,
     * whether it is hiding) can be queried.
     * @return the symbol on the current index, or null if the wheel has no symbols
     */
    public Symbol getShownSymbol(){
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    /**
     * Show the wheel: the symbol and the slot which it
     * is located
     */
    public void makeVisible(){
        visible = true;
        slot.makeVisible();
        if (shownSymbol != null) {
            shownSymbol.makeVisible();
        }
    }

    /**
     * Hide the wheel: the symbol and the slot which it
     * is located
     */
    public void makeInvisible(){
        visible = false;
        if (shownSymbol != null) {
            shownSymbol.makeInvisible();
        }
        slot.makeInvisible();
    }
    /**
     * Hides the figure that was shown and shows the figure of the
     * symbol located on the current index
     */
    private void refreshShape(){
        final Symbol current = symbols.isEmpty() ? null : symbols.get(currentIndex);
        if (current == shownSymbol) {
            return;
        }
        if (shownSymbol != null) {
            shownSymbol.makeInvisible();
        }
        shownSymbol = current;
        if (visible && shownSymbol != null) {
            shownSymbol.makeVisible();
        }
    }

    // Pixel coordinates where the figure of a symbol of this wheel is drawn
    private int symbolX(){
        return posX * (CELL_SIZE + GAP) + GAP + SYMBOL_OFFSET_X;
    }

    private int symbolY(){
        return posY * (CELL_SIZE + GAP) + GAP + SYMBOL_OFFSET_Y;
    }

    /**
     * Moves the slot and its symbols to the given grid cell, translating
     * grid positions into pixels with CELL_SIZE and GAP.
     * @param newPosX the target column (X index) in the grid matrix
     * @param newPosY the target row (Y index) in the grid matrix
     */
    private void accommodate(final int newPosX, final int newPosY) {
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
        final int newTargetX = newPosX * (CELL_SIZE + GAP) + GAP;
        final int newTargetY = newPosY * (CELL_SIZE + GAP) + GAP;
 
        // Substract the previous position to know how many
        // pixels should move the shape
        final int deltaX = newTargetX - oldTargetX;
        final int deltaY = newTargetY - oldTargetY;
 
        slot.moveHorizontal(deltaX);
        slot.moveVertical(deltaY);
        for (final Symbol symbol : symbols) {
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
    public void accommodate(final int move) {
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
    public void slowMoveHorizontal(final int distance) {
        slot.moveHorizontal(distance);
        for (final Symbol symbol : symbols) {
            symbol.moveHorizontal(distance);
        }
        pause();
    }

    /**
     * Moves the slot and every symbol of this wheel vertically and then
     * pauses briefly, so the movement can be seen step by step.
     *
     * @param distance number of pixels to move; negative values move up
     */
    public void slowMoveVertical(final int distance) {
        slot.moveVertical(distance);
        for (final Symbol symbol : symbols) {
            symbol.moveVertical(distance);
        }
        pause();
    }

    /**
     * Returns the index of the symbol on the wheel according to its value,
     * or -1 if the wheel doesn't have it
     * @param symbol string value of the symbol
     */
    private int getIndexOfSymbol(final String symbol) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getName().equals(symbol)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Modify the coordiinates of the wheel to the new values
     * @param posX the new position in X axis of the board
     * @param posY the new position in Y axis of the board
     */
    public void setLocation(final int posX, final int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Locks this wheel, preventing it from being spun or modified until it
     * is explicitly unlocked. Wheels that can't be locked, like
     * {@link RebelWheel}, override it to refuse.
     * @throws SlotMachineException if the wheel is already locked or this
     *         kind of wheel can't be locked
     */
    public void lock() throws SlotMachineException {
        checkUnlocked();
        locked = true;
    }

    /**
     * Checks that this wheel isn't locked before it changes. Every kind of
     * wheel calls it before changing, so a locked wheel never changes.
     * @throws SlotMachineException if the wheel is locked
     */
    protected final void checkUnlocked() throws SlotMachineException {
        if (locked) {
            throw new SlotMachineException(SlotMachineException.LOCKED_WHEEL);
        }
    }

    /**
     * Unlocks this wheel, allowing it to be spun or modified again.
     * @throws SlotMachineException if the wheel is not locked  
     */
    public void unlock() throws SlotMachineException {
        if (!locked) {
            throw new SlotMachineException(SlotMachineException.NOT_LOCKED);
        }
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
    public void setLeftNeighbor(Wheel left) {
        // A normal wheel doesn't depend on its neighbors
    }
    /**
     * Indicates whether this wheel allows being swapped with another one.
     * A normal wheel does; subclasses such as {@link RebelWheel} override it.
     *
     * @return true if the wheel can be swapped, false otherwise
     */
    public boolean canBeSwapped() {
        return true;
    }

    /**
     * Indicates whether this wheel allows being removed from the machine.
     * A normal wheel does; subclasses such as {@link RebelWheel} override it.
     *
     * @return true if the wheel can be removed, false otherwise
     */
    public boolean canBeRemoved() {
        return true;
    }
}
