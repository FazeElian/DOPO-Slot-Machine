/**
 * A wheel that only responds to every other spin request.
 * It starts awake: the first spin advances it, the second one is ignored
 * (it "falls asleep"), the third one advances it again, and so on.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class LazyWheel extends Wheel {
    private boolean awake = true;
    
    /**
     * Constructs a lazy wheel at the given grid position.
     *
     * @param posX the column of the wheel in the grid
     * @param posY the row of the wheel in the grid
     */
    public LazyWheel(int posX, int posY) {
        super(posX, posY);
    }
    
    /**
     * Spins this wheel only if it is awake, then toggles its state.
     * Every call toggles the state, whether or not the wheel moved.
     *
     * @param direction 1 to advance one step, -1 to go back one step
     */
    @Override
    public void spin(int direction) {
        if (awake) {
            super.spin(direction);
        }
        awake = !awake;
    }
}