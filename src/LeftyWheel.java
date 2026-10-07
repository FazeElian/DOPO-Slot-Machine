/**
 * A wheel that copies the state of the wheel to its left.
 * When spun, if it has a left neighbor it shows that neighbor's visible
 * symbol; otherwise it behaves like a normal {@link Wheel}.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class LeftyWheel extends Wheel {
    private Wheel left;

    /**
     * Constructs a lefty wheel at the given grid position.
     *
     * @param posX the column of the wheel in the grid
     * @param posY the row of the wheel in the grid
     */
    public LeftyWheel(int posX, int posY) {
        super(posX, posY);
    }

    /**
     * Sets the wheel this one will copy when spun.
     *
     * @param left the wheel to the left, or null if there is none
     */
    @Override
    public void setLeftNeighbor(Wheel left) {
        this.left = left;
    }

    /**
     * Spins this wheel. If it has a left neighbor, it takes that neighbor's
     * visible symbol and the direction is ignored; otherwise it spins
     * one step in the given direction like a normal wheel. Either way it
     * counts as a spin for its symbols.
     *
     * @param direction 1 to advance one step, -1 to go back one step
     * @throws SlotMachineException propagated from placeSymbol if the
     *         neighbor's symbol doesn't exist on this wheel
     */
    @Override
    public void spin(int direction) throws SlotMachineException {
        if (left == null) {
            super.spin(direction);
        } else {
            notifySpin();
            placeSymbol(left.visibleSymbol());
        }
    }
}