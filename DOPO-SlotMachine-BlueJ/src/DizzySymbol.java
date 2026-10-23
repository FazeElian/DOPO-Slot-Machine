/**
 * A symbol that gets dizzy: every time its wheel spins, its triangle
 * flips upside down (and back again on the next spin). It is drawn
 * narrower than a normal symbol so it can be told apart at any moment.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class DizzySymbol extends Symbol {
    /** Width of the triangle, narrower than a normal symbol. */
    public static final int WIDTH = SYMBOL_SIZE * 3 / 5;
    // Whether the triangle is upside down right now
    private boolean flipped = false;

    /**
     * Constructs a dizzy symbol whose figure stays at the origin of the canvas.
     * @param name color string that identifies the symbol
     */
    public DizzySymbol(String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs a dizzy symbol whose figure is placed at the given pixel
     * coordinates of the canvas. It starts pointing up.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public DizzySymbol(String name, int x, int y) {
        super(name, x, y);
        changeFigureSize(SYMBOL_SIZE, WIDTH);
    }

    /**
     * Returns a new dizzy symbol (pointing up) with the same name at the
     * given coordinates
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     */
    @Override
    public Symbol copyAt(int x, int y) {
        return new DizzySymbol(getName(), x, y);
    }

    /**
     * Flips the triangle. A negative height draws it upwards from its
     * anchor point, so the figure is also moved to stay inside its cell.
     */
    @Override
    public void spun() {
        flipped = !flipped;
        if (flipped) {
            changeFigureSize(-SYMBOL_SIZE, WIDTH);
            moveVertical(SYMBOL_SIZE);
        } else {
            changeFigureSize(SYMBOL_SIZE, WIDTH);
            moveVertical(-SYMBOL_SIZE);
        }
    }
}