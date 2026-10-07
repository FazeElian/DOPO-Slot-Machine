/**
 * A shy symbol: it alternates between showing and hiding itself every
 * time it is selected on its wheel (the wheel stops at it after a spin
 * or a placeSymbol). The first time it is selected it hides, the second
 * time it shows itself, and so on. Hiding is only visual: the wheel is
 * still on this symbol, so it keeps counting for the configuration and
 * the jackpot.
 *
 * @author Elián Ibarra, Oscar Poveda
 * @version 1.0
 */
public class ShySymbol extends Symbol {
    // Whether the symbol is refusing to be drawn right now
    private boolean hidden = false;
    // Whether the wheel currently wants this symbol on screen
    private boolean shown = false;

    /**
     * Constructs a shy symbol whose figure stays at the origin of the canvas.
     * @param name color string that identifies the symbol
     */
    public ShySymbol(String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs a shy symbol whose figure is placed at the given pixel
     * coordinates of the canvas. It starts willing to be seen.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public ShySymbol(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * Returns a new shy symbol with the same name at the given coordinates
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     */
    @Override
    public Symbol copyAt(int x, int y) {
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
            if (hidden) super.makeInvisible();
            else super.makeVisible();
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
        if (!hidden) super.makeVisible();
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