package domain;

/**
 * A wheel that resists being locked, swapped or removed, but can still be
 * spun and have its symbols changed. It overrides {@link #lock()} to refuse
 * being locked, and {@link SlotMachine} asks it {@link #canBeSwapped()} and
 * {@link #canBeRemoved()} before swapping or removing it.
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
    public RebelWheel(final int posX, final int posY) {
        super(posX, posY);
    }
    
    /**
     * A rebel wheel never lets itself be locked: it always refuses.
     * @throws SlotMachineException always, with the CANT_LOCK message
     */
    @Override
    public void lock() throws SlotMachineException {
        throw new SlotMachineException(SlotMachineException.CANT_LOCK);
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
