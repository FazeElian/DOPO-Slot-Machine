/**
 * A wheel that resists being locked, swapped or removed.
 * It can still be spun and have its symbols changed like a normal
 * {@link Wheel}. The swap and removal restrictions are enforced by
 * {@link SlotMachine}, which checks {@link #isRebel()}.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class RebelWheel extends Wheel {
    /**
     * Constructs a rebel wheel at the given grid position.
     *
     * @param posX the column of the wheel in the grid
     * @param posY the row of the wheel in the grid
     */
    public RebelWheel(int posX, int posY) {
        super(posX, posY);
    }
    
    /**
     * Ignores the request: a rebel wheel never gets locked,
     * so {@link #isLocked()} always returns false.
     */
    @Override
    public void lock() {}
    
    /**
     * Indicates that this wheel is of the "rebel" type.
     *
     * @return always true
     */
    @Override
    public boolean isRebel() {
        return true;
    }
}