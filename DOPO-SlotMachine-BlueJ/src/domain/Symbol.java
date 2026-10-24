package domain;

import presentation.Triangle;

import java.util.ArrayList;

/**
 * A symbol of a wheel: its name (a color such as "red" or "#3fae1c") and
 * the Triangle that draws it. {@link #symbols} is the catalog shared by the
 * board; every wheel builds its own copies so each figure moves with it.
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class Symbol {
    /** Side in pixels of the figure of a normal symbol. */
    public final static int SYMBOL_SIZE = 50;
    /** Catalog of the symbols registered on the machine, in order. */
    public static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    /** Color string that identifies the symbol. */
    private String name;
    /** Triangle that draws the symbol on the canvas. */
    private Triangle figure;
    /** Current height of the figure, kept so it can be queried. */
    private int height;
    /** Current width of the figure, kept so it can be queried. */
    private int width;

    /**
     * Constructs a symbol whose figure stays at the origin of the canvas.
     * Used for the entries of the catalog, whose figures are never drawn.
     * @param name color string that identifies the symbol
     */
    public Symbol(final String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs a symbol whose figure is placed at the given pixel
     * coordinates of the canvas. The figure starts invisible.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public Symbol(final String name, final int x, final int y) {
        this.name = name;
        figure = new Triangle();
        changeFigureSize(SYMBOL_SIZE, SYMBOL_SIZE);
        figure.changeColor(name);
        figure.moveHorizontal(x);
        figure.moveVertical(y);
    }

    /**
     * Returns a new symbol of this same type and name, with its figure at
     * the given coordinates. Subclasses override it to return their own
     * type, so wheels can copy any symbol without knowing its class.
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     * @return the new symbol
     */
    public Symbol copyAt(final int x, final int y) {
        return new Symbol(name, x, y);
    }

    /**
     * Returns the index of the symbol on the catalog according to its
     * name, or -1 if it isn't registered
     * @param name color string of the symbol
     * @return the 0-based index, or -1 if it isn't registered
     */
    public static int indexOf(final String name) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the color string that identifies this symbol
     * @return the name of the symbol
     */
    public String getName() {
        return name;
    }

    /**
     * Shows the figure of the symbol on the canvas
     */
    public void makeVisible() {
        figure.makeVisible();
    }

    /**
     * Hides the figure of the symbol from the canvas
     */
    public void makeInvisible() {
        figure.makeInvisible();
    }

    /**
     * Moves the figure horizontally by the given distance in pixels
     * @param distance pixels to move (negative moves left)
     */
    public void moveHorizontal(final int distance) {
        figure.moveHorizontal(distance);
    }

    /**
     * Moves the figure vertically by the given distance in pixels
     * @param distance pixels to move (negative moves up)
     */
    public final void moveVertical(final int distance) {
        figure.moveVertical(distance);
    }
    
    /**
     * Called by the wheel on every one of its symbols each time it spins.
     * A normal symbol ignores it; subclasses override it to react to spins.
     */
    public void spun() {
        // A normal symbol doesn't react to spins
    }

    /**
     * Called by the wheel when this symbol becomes the selected one (the
     * one the wheel stops at), after a spin or a placeSymbol. A normal
     * symbol ignores it; subclasses override it to react.
     */
    public void selected() {
        // A normal symbol doesn't react to being selected
    }

    /**
     * Returns the current height of the figure in pixels. It is negative
     * when the figure is drawn upside down.
     * @return the height of the figure
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the current width of the figure in pixels
     * @return the width of the figure
     */
    public int getWidth() {
        return width;
    }

    /**
     * Indicates whether this symbol is refusing to be drawn even when its
     * wheel shows it. A normal symbol never hides.
     * @return true if the symbol is hiding, false otherwise
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Changes the size of the figure. Used by the subclasses, which can't
     * reach the figure directly.
     * @param height new height in pixels; a negative value draws the
     *               figure upside down. It can't be 0
     * @param width new width in pixels
     */
    protected final void changeFigureSize(final int height, final int width) {
        figure.changeSize(height, width);
        this.height = height;
        this.width = width;
    }
}
