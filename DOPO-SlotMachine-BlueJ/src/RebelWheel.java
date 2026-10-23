/**
 * A wheel that resists being locked, swapped or removed.
 * It can still be spun and have its symbols changed like a normal
 * {@link Wheel}. The restrictions are enforced by {@link SlotMachine},
 * which asks every wheel {@link #canBeLocked()}, {@link #canBeSwapped()}
 * and {@link #canBeRemoved()} before doing those actions.
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
     * A rebel wheel never lets itself be locked.
     *
     * @return always false
     */
    @Override
    public boolean canBeLocked() {
        return false;
    }

    /**
     * A rebel wheel never lets itself be swapped.
     *
     * @return always false
     */
    @Override
    public boolean canBeSwapped() {
        return false;
    }

    /**
     * A rebel wheel never lets itself be removed.
     *
     * @return always false
     */
    @Override
    public boolean canBeRemoved() {
        return false;
    }
}