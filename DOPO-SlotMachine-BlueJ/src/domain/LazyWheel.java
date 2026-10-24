package domain;

/**
 * A wheel that only responds to every other spin request.
 * It starts awake: the first spin advances it, the second one is ignored
 * (it "falls asleep"), the third one advances it again, and so on.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class LazyWheel extends Wheel {
    /** Whether the next spin request will be obeyed. */
    private boolean awake = true;
    
    /**
     * Constructs a lazy wheel at the given grid position.
     *
     * @param posX the column of the wheel in the grid
     * @param posY the row of the wheel in the grid
     */
    public LazyWheel(final int posX, final int posY) {
        super(posX, posY);
    }
    
    /**
     * Spins this wheel only if it is awake, then toggles its state. Every
     * accepted call toggles it, whether or not the wheel moved.
     *
     * @param direction 1 to advance one step, -1 to go back one step
     * @throws SlotMachineException if the wheel is locked, even asleep
     */
    @Override
    public void spin(final int direction) throws SlotMachineException {
        checkUnlocked();
        if (awake) {
            super.spin(direction);
        }
        awake = !awake;
    }
}