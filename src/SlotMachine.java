import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 * Represents a slot machine composed of a visual board (gray background)
 * and a list of wheels ({@link Wheel}) organized in a grid of up to
 * 14 columns and 9 rows. Symbols are shared globally across all wheels
 * through {@link Wheel#symbols}.
 *
 * <p>Position convention: all positions exposed in public methods are
 * <b>1-based</b> (the first wheel is position 1).</p>
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachine {

    /** Maximum number of columns in the wheel grid. */
    public final static int MAX_COLUMNS = 14;
    /** Maximum number of rows in the wheel grid. */
    public final static int MAX_ROWS = 9;

    /** Gray rectangle that serves as the visual background for all wheels. */
    private Rectangle board;
    /** List of active wheels in the machine, in logical position order. */
    private ArrayList<Wheel> wheels;
    /**
     * Indicates whether the last executed operation was successful.
     * Queried via {@link #ok()}.
     */
    private boolean ok;


    // MINI-CYCLE 1 – Visual structure of the machine
    
    /**
     * Constructs an empty SlotMachine with a gray board positioned
     * to wrap exactly one wheel cell (with a 10 px margin).
     * The board grows automatically when wheels are added via {@link #addWheel(int)}.
     */
    
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.board = new Rectangle();
        this.board.changeColor("gray");

        // First cell position: pos * (CELL_SIZE + GAP) + GAP = 1 * 80 + 10 = 90px
        int firstWheelX = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px
        int firstWheelY = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px

        int boardX = firstWheelX - 10; // 80 px
        int boardY = firstWheelY - 10; // 80 px

        this.board.moveHorizontal(boardX);
        this.board.moveVertical(boardY);

        // Initial size: one cell + 10px margin on each side
        int defaultWidth = Wheel.CELL_SIZE + 20;
        int defaultHeight = Wheel.CELL_SIZE + 20;

        this.board.changeSize(defaultHeight, defaultWidth);
        this.board.makeVisible();
        this.ok = true;
    }

    /**
     * Adds a new wheel at the given position.
     * Positions below 1 are corrected to 1; positions beyond the end are corrected
     * to the last valid index to avoid gaps.
     * Grid position is derived from pos using: posX = ((pos-1) % MAX_COLUMNS) + 1
     * and posY = ((pos-1) / MAX_COLUMNS) + 1, so there is only one calculation
     * system and no external counters that could fall out of sync.
     * When inserting in the middle, all wheels after pos are shifted right with accommodate(1).
     *
     * @param pos 1-based position where the new wheel is inserted
     */
    public void addWheel(int pos) {
        if (pos < 1) pos = 1;
        if (pos > wheels.size() + 1) pos = wheels.size() + 1;

        // Grid limit: 14 x 9 = 126 wheels
        if (wheels.size() >= MAX_COLUMNS * MAX_ROWS) {
            MessageUtil.showError("Ha alcanzado el máximo de ruedas posibles.");
            return;
        }

        if (pos > wheels.size()) {// Adding at the end
            int nextPos = wheels.size() + 1;
            int posX = ((nextPos - 1) % MAX_COLUMNS) + 1;
            int posY = ((nextPos - 1) / MAX_COLUMNS) + 1;

            Wheel newWheel = new Wheel(posX, posY);
            wheels.add(newWheel);
        } else { // Inserting in the middle
            int posX = ((pos - 1) % MAX_COLUMNS) + 1;
            int posY = ((pos - 1) / MAX_COLUMNS) + 1;

            Wheel newWheel = new Wheel(posX, posY);
            wheels.add(pos - 1, newWheel);
            // Move every wheel after the new one one position to the right
            for (int i = pos; i < wheels.size(); i++) {
                wheels.get(i).accommodate(1);
            }
        }

        updateBoardSize();
    }

/**
     * Removes the wheel at the given position and shifts all subsequent wheels
     * one position to the left with accommodate(-1).
     *
     * @param pos 1-based position of the wheel to remove
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }

        int index = adjustPosition(pos);
        Wheel removedWheel = wheels.remove(index);
        removedWheel.makeInvisible(); // remove from screen before shifting
        // Move every wheel after the removed one one position to the left
        for (int i = index; i < wheels.size(); i++) {
            wheels.get(i).accommodate(-1); 
        }

        updateBoardSize();
        ok = true;
    }

    // MINI-CYCLE 2 – Symbols

    /**
     * Adds a new color to the global symbol list at the given position and
     * inserts it into every existing wheel.
     * Colors are lowercased to avoid case-sensitive duplicates.
     * If the color already exists, shows an error and sets ok = false.
     *
     * @param pos   1-based position in the global symbol list
     * @param color color name to add
     */
    public void addSymbol(int pos, String color) {
        int index = Wheel.symbols.indexOf(color.toLowerCase());
         // indexOf returns -1 if the symbol is not yet registered
        if (index==-1){
            if (pos<1){
                Wheel.symbols.add(0,color.toLowerCase());
            }
            else if(pos > Wheel.symbols.size()){
                Wheel.symbols.add(color.toLowerCase());
            }
            else {
                Wheel.symbols.add(pos-1, color.toLowerCase());
            }
            // Adjust currentIndex if the new symbol was inserted 
            // before or at the current position
            for (int i = 0; i < wheels.size(); i++) {
                wheels.get(i).addSymbol(pos);
                ok = true;
            }
        }else{
            ok = false;
            MessageUtil.showError(color.toLowerCase() + " ya es un simbolo, elige uno nuevo");
        }
    }

    /**
     * Removes a color from the global symbol list and from every wheel.
     * If the color does not exist, shows an error and sets ok = false.
     *
     * @param color color name to remove
     */
    public void delSymbol(String color) {
        if (!Wheel.symbols.contains(color)) {
            MessageUtil.showError(color + " no existe como símbolo.");
            ok = false;
            return;
        }

        for (Wheel wheel : wheels) {
            wheel.delSymbol(color);
        }

        Wheel.symbols.remove(color);
        ok = true;
    }
    /**
     * Returns the global list of registered symbols.
     *
     * @return array of color names
    */
   public String[] symbols() {
       return Wheel.symbols.toArray(String[]::new);
   }

   // MINI-CYCLE 3 – Spin and query result

   /**
     * Spins the wheel at the given position.
     *
     * @param wheelPos 1-based position of the wheel to spin
     */
    public void spin(int wheelPos) {
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).spin();
            ok = true;
        } else {
            ok = false;
        }
    }

     /**
     * Spins all wheels simultaneously.
     * Sets ok = false if there are no wheels.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.spin();
        }
        ok = true;
    }

    /**
     * Sets the visible symbol of a wheel to the given color.
     *
     * @param wheelPos 1-based position of the wheel
     * @param symbol   color to display
     */
   public void placeSymbol(int wheelPos, String symbol) {
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).placeSymbol(symbol);
            ok = true;
        } else {
            ok = false;
        }
    }

    
    /**
     * Returns the currently visible symbol of each wheel, in order.
     * Returns an empty array and shows an error if no symbols are registered.
     *
     * @return array of visible color names, one per wheel
     */
    public String[] configuration() {
        if (!Wheel.symbols.isEmpty()){
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).visibleSymbol();
        }
        ok = true;
        return config;
        }else{
            MessageUtil.showError("No existen simbolos aun");
            return new String[0];
        }
    }

    /**
     * Returns the number of unique symbols currently visible across all wheels.
     * Returns 0 and shows an error if no symbols are registered.
     *
     * @return count of distinct visible symbols
     */
    public int distinctSymbols() {
        Set<String> uniqueSymbols = new HashSet<>();
        if (!Wheel.symbols.isEmpty()){

            for (Wheel wheel : wheels) {
                uniqueSymbols.add(wheel.visibleSymbol());
            }
            ok = true;
            return uniqueSymbols.size();
        }
        else{
            MessageUtil.showError("No existen simbolos aun");
            return 0;
        }
    }

    /**
     * Returns true if all wheels are showing the same symbol.
     * Returns false if the wheels list is empty or no symbols are registered.
     *
     * @return true on jackpot, false otherwise
     */
    public boolean isJackpot() {
        if (!Wheel.symbols.isEmpty()){
            if (wheels.isEmpty()) {
                ok = true;
                return false;
            }
            String first = wheels.get(0).visibleSymbol();
            for (Wheel wheel : wheels) {
                if (!wheel.visibleSymbol().equals(first)) {
                    ok = true;
                    return false;
                }
            }
            ok = true;
            return true;
        }else{
            MessageUtil.showError("No existen simbolos aun");
            return false;
        }
    }


    // MINI-CYCLE 4 – Usability and visibility

    /**
     * Makes the board and all wheels visible.
     */
    public void makeVisible() {
        board.makeVisible();
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
        ok = true;
    }
    
    /**
     * Hides all wheels first, then the board, to avoid the board
     * covering shapes still on the canvas.
     */
    public void makeInvisible() {
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        board.makeInvisible();
        ok = true;
    }

    /** Terminates the application. */
    public void exit() {
        System.exit(0);
    }

    // Auxiliar Methods
    /** Returns true if the last operation was successful. */
    public boolean ok() {
        return ok;
    }

    // Converts a 1-based position to a 0-based index for the wheels list.
    // Returns 0 if pos < 1, or wheels.size() (out of range) if pos > size.
    // Callers must validate the result before using it as an index.
    private int adjustPosition(int pos) {
        if (pos < 1) return 0;
        if (pos > wheels.size()) return wheels.size();
        return pos - 1;
    }

    // Resizes the board to fit the current number of wheels.
    // Rows are calculated with integer division + remainder to handle the
    // wrap from column 14 to the next row.
    // After resizing, all wheels are redrawn on top because changeSize()
    // moves the board to the front of the canvas z-order.
    private void updateBoardSize() {
        int count = wheels.isEmpty() ? 1 : wheels.size();

        int rows = count / MAX_COLUMNS;
        if (count % MAX_COLUMNS != 0) {
            rows += 1;// incomplete last row
        }

        int columns = (count < MAX_COLUMNS) ? count : MAX_COLUMNS;

        int totalWidth = columns * Wheel.CELL_SIZE + (columns - 1) * Wheel.GAP + 20;
        int totalHeight = rows * Wheel.CELL_SIZE + (rows - 1) * Wheel.GAP + 20;

        board.changeSize(totalHeight, totalWidth);
        // Bring wheels back to the front after the board was redrawn
        for (Wheel w : wheels) {
            w.makeVisible();
        }
    }
}
