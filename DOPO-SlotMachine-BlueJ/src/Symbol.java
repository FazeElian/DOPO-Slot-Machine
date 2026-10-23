import java.util.ArrayList;

/**
 * Represents a single symbol of a wheel: its name (the color string,
 * e.g. "red" or "#3fae1c") and the figure (a Triangle) that draws it
 * on the canvas. The static list {@link #symbols} is the catalog of
 * registered symbols shared by the whole board; every wheel builds its own
 * Symbol objects from it, so each figure moves with the wheel it belongs to.
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class Symbol {
    /** Side in pixels of the figure of a normal symbol. */
    public final static int SYMBOL_SIZE = 50;
    /** Catalog of the symbols registered on the machine, in order. */
    public static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    private String name;
    private Triangle figure;
    // Current size of the figure, kept so it can be queried
    private int height;
    private int width;

    /**
     * Constructs a symbol whose figure stays at the origin of the canvas.
     * Used for the entries of the catalog, whose figures are never drawn.
     * @param name color string that identifies the symbol
     */
    public Symbol(String name) {
        this(name, 0, 0);
    }

    /**
     * Constructs a symbol whose figure is placed at the given pixel
     * coordinates of the canvas. The figure starts invisible.
     * @param name color string that identifies the symbol
     * @param x pixel position in X axis of the figure
     * @param y pixel position in Y axis of the figure
     */
    public Symbol(String name, int x, int y) {
        this.name = name;
        figure = new Triangle();
        changeFigureSize(SYMBOL_SIZE, SYMBOL_SIZE);
        figure.changeColor(name);
        figure.moveHorizontal(x);
        figure.moveVertical(y);
    }

    /**
     * Returns a new symbol of the same type as this one, with the same
     * name and its figure placed at the given pixel coordinates. Every
     * subclass must override it to return an instance of its own type,
     * so wheels can copy any registered symbol without knowing its class.
     * @param x pixel position in X axis of the new figure
     * @param y pixel position in Y axis of the new figure
     * @return the new symbol
     */
    public Symbol copyAt(int x, int y) {
        return new Symbol(name, x, y);
    }

    /**
     * Returns the index of the symbol on the catalog according to its
     * name, or -1 if it isn't registered
     * @param name color string of the symbol
     * @return the 0-based index, or -1 if it isn't registered
     */
    public static int indexOf(String name) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getName().equals(name)) return i;
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
    public void moveHorizontal(int distance) {
        figure.moveHorizontal(distance);
    }

    /**
     * Moves the figure vertically by the given distance in pixels
     * @param distance pixels to move (negative moves up)
     */
    public void moveVertical(int distance) {
        figure.moveVertical(distance);
    }
    
    /**
     * Called by the wheel on every one of its symbols each time it spins.
     * A normal symbol ignores it; subclasses override it to react to spins.
     */
    public void spun() {}

    /**
     * Called by the wheel when this symbol becomes the selected one (the
     * one the wheel stops at), after a spin or a placeSymbol. A normal
     * symbol ignores it; subclasses override it to react.
     */
    public void selected() {}

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
    protected void changeFigureSize(int height, int width) {
        figure.changeSize(height, width);
        this.height = height;
        this.width = width;
    }
}
