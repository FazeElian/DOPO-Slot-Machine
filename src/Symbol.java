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
    public static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    private String name;
    private Triangle figure;

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
        figure.changeSize(Wheel.SYMBOL_SIZE, Wheel.SYMBOL_SIZE);
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
     */
    public Symbol copyAt(int x, int y) {
        return new Symbol(name, x, y);
    }

    /**
     * Returns the index of the symbol on the catalog according to its
     * name, or -1 if it isn't registered
     * @param name color string of the symbol
     */
    public static int indexOf(String name) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    /**
     * Returns the color string that identifies this symbol
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
}
