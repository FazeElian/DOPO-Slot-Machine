/**
 * An ephemeral symbol: every time its wheel spins, its triangle shrinks
 * one step (width and height together, so it stays equilateral) until it
 * is just a point. It shrinks on every spin of the wheel, whether or not
 * it is the symbol being shown.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class EphemeralSymbol extends Symbol {
    /** Pixels the side of the triangle loses on every spin. */
    public static final int STEP = 10;
    /** Side of the triangle when it has become a point. */
    public static final int MIN_SIDE = 2;
    // Current side (base) of the triangle
    private int side = SYMBOL_SIZE;

    /**
     * Constructs an ephemeral symbol whose figure stays at the origin of the canvas.
     * @param name color string that identifies the symbol
     */
    public EphemeralSymbol(String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs an ephemeral symbol whose figure is placed at the given
     * pixel coordinates of the canvas. It starts at full size.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public EphemeralSymbol(String name, int x, int y) {
        super(name, x, y);
        int height = heightOf(side);
        changeFigureSize(height, side);
        moveVertical((SYMBOL_SIZE - height) / 2);   // center it in the cell
    }

    /**
     * Returns a new ephemeral symbol (at full size) with the same name at
     * the given coordinates
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     */
    @Override
    public Symbol copyAt(int x, int y) {
        return new EphemeralSymbol(getName(), x, y);
    }

    /**
     * Shrinks the whole triangle (width and height together) one step,
     * keeping it equilateral and centered, until it is just a point.
     */
    @Override
    public void spun() {
        int oldHeight = heightOf(side);
        side = Math.max(MIN_SIDE, side - STEP);
        int newHeight = heightOf(side);
        changeFigureSize(newHeight, side);
        moveVertical((oldHeight - newHeight) / 2);  // keep it centered
    }

    // Height of an equilateral triangle with the given side
    private static int heightOf(int side) {
        return Math.max(1, (int) Math.round(side * Math.sqrt(3) / 2));
    }
}