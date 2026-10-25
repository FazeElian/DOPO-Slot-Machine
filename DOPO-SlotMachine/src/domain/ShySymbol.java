package domain;

/**
 * A shy symbol: each time its wheel stops at it (after a spin or a
 * placeSymbol) it toggles between hiding and showing, starting by hiding.
 * Hiding is only visual: it still counts for the configuration and jackpot.
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class ShySymbol extends Symbol {
    /** Whether the symbol is refusing to be drawn right now. */
    private boolean hidden;
    /** Whether the wheel currently wants this symbol on screen. */
    private boolean shown;

    /**
     * Constructs a shy symbol whose figure stays at the origin of the canvas.
     * @param name color string that identifies the symbol
     */
    public ShySymbol(final String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs a shy symbol whose figure is placed at the given pixel
     * coordinates of the canvas. It starts willing to be seen.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public ShySymbol(final String name, final int x, final int y) {
        super(name, x, y);
    }

    /**
     * Returns a new shy symbol with the same name at the given coordinates
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     */
    @Override
    public Symbol copyAt(final int x, final int y) {
        return new ShySymbol(getName(), x, y);
    }

    /**
     * Toggles between hiding and showing, and updates the canvas if the
     * wheel has this symbol on screen. When the wheel is spun by steps,
     * every step the wheel stops at this symbol counts as a selection,
     * since the symbol is shown on that step.
     */
    @Override
    public void selected() {
        hidden = !hidden;
        if (shown) {
            if (hidden) {
                super.makeInvisible();
            } else {
                super.makeVisible();
            }
        }
    }

    /**
     * Returns whether the symbol is hiding right now
     * @return true if it is hiding, false if it lets itself be seen
     */
    @Override
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Shows the figure only if the symbol is not hiding; either way it
     * remembers that the wheel wants it on screen.
     */
    @Override
    public void makeVisible() {
        shown = true;
        if (!hidden) {
            super.makeVisible();
        }
    }

    /**
     * Hides the figure and remembers that the wheel took it off screen.
     */
    @Override
    public void makeInvisible() {
        shown = false;
        super.makeInvisible();
    }
}