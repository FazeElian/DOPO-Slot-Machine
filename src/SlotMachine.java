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
    /** Black base that overflows 5px on each side of the board. */
    private Rectangle base;
    /** Horizontal black arm connecting the board to the lever, anchored at the base level. */
    private Rectangle leverArm;
    /** Red rectangle that acts as the lever of the slot machine. */
    private Rectangle lever;
    /** List of active wheels in the machine, in logical position order. */
    private ArrayList<Wheel> wheels;
    /**
     * Indicates whether the last executed operation was successful.
     * Queried via {@link #ok()}.
     */
    private boolean ok;

    // Stored so updateBoardSize can reposition shapes relative to the board
    private int boardX;
    private int boardY;
    // Tracks current X of the lever to compute deltas on each resize
    private int leverX = 0;
    // Tracks current Y of the lever to compute vertical delta on each resize
    private int leverY = 0;
    // Tracks current Y of the base to compute vertical delta on each resize
    private int baseY = 0;
    // Tracks current Y of the arm to compute vertical delta on each resize
    private int leverArmY = 0;
    // Tracks current X of the lever arm to compute deltas on each resize
    private int leverArmX = 0;
    // Tracks whether the machine is currently visible, to decide if spins/swaps should animate
    private boolean visible = false;
    // List of indexes of the locked wheels
    private static ArrayList<Integer> lockedWheels = new ArrayList<>();

    // MINI-CYCLE 1 – Visual structure of the machine
    
    /**
     * Constructs an empty SlotMachine with a gray board positioned
     * to wrap exactly one wheel cell (with a 10px margin).
     * The board grows automatically when wheels are added via {@link #addWheel(int)}.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();

        // First cell position: pos * (CELL_SIZE + GAP) + GAP = 1 * 80 + 10 = 90px
        int firstWheelX = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px
        int firstWheelY = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px

        boardX = firstWheelX - 10; // 80 px - stored as field for lever repositioning
        boardY = firstWheelY - 10; // 80 px - stored as field for arm/base vertical calc

        // Initial size: one cell + 10px margin on each side
        int defaultWidth  = Wheel.CELL_SIZE + 20;
        int defaultHeight = Wheel.CELL_SIZE + 20;

        // Board
        this.board = new Rectangle();
        this.board.changeColor("gray");
        this.board.moveHorizontal(boardX);
        this.board.moveVertical(boardY);
        this.board.changeSize(defaultHeight, defaultWidth);

        // Base: black bar 10px tall, sticks out 5px on each side of the board,
        // anchored to the bottom edge of the board
        baseY = boardY + defaultHeight;
        this.base = new Rectangle();
        this.base.changeColor("black");
        this.base.changeSize(10, defaultWidth + 10);
        this.base.moveHorizontal(boardX - 5);
        this.base.moveVertical(baseY);

        // Arm: horizontally exits right edge of board, vertically at mid-board
        // so it connects with the bottom of the lever
        int armHeight = 8;
        int armWidth  = 20;
        leverArmY = boardY + (defaultHeight / 2) - (armHeight / 2);
        this.leverArm = new Rectangle();
        this.leverArm.changeColor("black");
        this.leverArm.changeSize(armHeight, armWidth);
        this.leverArm.moveHorizontal(boardX + defaultWidth);
        this.leverArm.moveVertical(leverArmY);
        leverArmX = boardX + defaultWidth;

        // Lever: starts at mid-board going upward, bottom edge aligned with arm
        int initialLeverHeight = defaultHeight / 2;
        leverX = boardX + defaultWidth + armWidth;
        leverY = leverArmY + armHeight - initialLeverHeight;
        this.lever = new Rectangle();
        this.lever.changeColor("red");
        this.lever.changeSize(defaultHeight / 2, 25);
        this.lever.moveHorizontal(leverX);
        this.lever.moveVertical(leverY);

        this.ok = true;
    }

    /**
     * Adds a new wheel at the given position.
     * Positions below 1 are corrected to 1; positions beyond the end are corrected to the last valid index to avoid gaps.
     * Grid position is derived from pos using: posX = ((pos-1) % MAX_COLUMNS) + 1
     * and posY = ((pos-1) / MAX_COLUMNS) + 1, so there is only one calculation
     * system and no external counters that could fall out of sync or return old data.
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
            ok = false;
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
            MessageUtil.showError("No hay ruedas para eliminar");
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
    /**
     * Lock a specific wheel so it cannot be spun.
     * @param wheel 1-based position of the wheel to lock
     */
    public void lock(int wheel) {
        if (wheel < 1 || wheel > wheels.size()) {
            MessageUtil.showError("No existe esa rueda, intenta de nuevo");
            ok = false;
            return;
        }
        if (lockedWheels.contains(wheel)) {
            MessageUtil.showError("Esa rueda ya está bloqueada");
            ok = false;
            return;
        }
        lockedWheels.add(wheel);
        ok = true;
    }

    /**
     * Unlock a specific wheel so it can be spun again.
     * @param wheel 1-based position of the wheel to unlock
     */
    public void unlock(int wheel) {
        if (!lockedWheels.contains(wheel)) {
            MessageUtil.showError("Esa rueda no estaba bloqueada");
            ok = false;
            return;
        }
        lockedWheels.remove(Integer.valueOf(wheel));
        ok = true;
    }

    // MINI-CYCLE 2 – Symbols

    /**
     * Adds a new symbol (color) to the global symbol list at the given position and
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
        if (index == -1) {
            if (pos < 1) {
                Wheel.symbols.add(0, color.toLowerCase());
            } else if (pos > Wheel.symbols.size()) {
                Wheel.symbols.add(color.toLowerCase());
            } else {
                Wheel.symbols.add(pos - 1, color.toLowerCase());
            }
            // Adjust currentIndex if the new symbol was inserted
            // before or at the current position
            for (int i = 0; i < wheels.size(); i++) {
                wheels.get(i).addSymbol(pos);
                ok = true;
            }
        } else {
            ok = false;
            MessageUtil.showError(color.toUpperCase() + " ya es un símbolo, elige uno nuevo");
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
            MessageUtil.showError("Ese símbolo: " + color.toUpperCase() + " no existe, añádelo e intenta de nuevo.");
            ok = false;
            return;
        }
        if (Wheel.symbols.size() > 1) {
            for (Wheel wheel : wheels) {
                wheel.delSymbol(color);
            }
            Wheel.symbols.remove(color);
            ok = true;
        } else {
            MessageUtil.showWarning("Solo queda un símbolo, no se puede eliminar");
            ok = false;
        }
    }

    /**
     * Returns the global list of registered symbols on the board.
     *
     * @return array of color names or its hexagesimal code if the user entered it on its creation
     */
    public String[] symbols() {
        return Wheel.symbols.toArray(String[]::new);
    }

    /**
     * Swap two wheels based on their 1-based positions.
     * Both positions must correspond to existing wheels; otherwise the
     * operation fails and shows an error.
     * @param wheel1 position of the first wheel
     * @param wheel2 position of the second wheel
     */
    public void swap(int wheel1, int wheel2) {
        if (wheel1 < 1 || wheel1 > wheels.size() || wheel2 < 1 || wheel2 > wheels.size()) {
            MessageUtil.showError("No existe esa rueda, intenta de nuevo");
            ok = false;
            return;
        }

        if (lockedWheels.contains(wheel1) || lockedWheels.contains(wheel2)) {
            MessageUtil.showError("Una de las ruedas está bloqueada, no puede intercambiarse");
            ok = false;
            return;
        }

        int i1 = wheel1 - 1;
        int i2 = wheel2 - 1;

        int distance = Math.abs(wheel2 - wheel1);
        int dir1 = (wheel2 > wheel1) ? 1 : -1;
        int dir2 = -dir1;

        Wheel w1 = wheels.get(i1);
        Wheel w2 = wheels.get(i2);

        for (int i = 0; i < distance; i++) {
            w1.accommodate(dir1);
            w2.accommodate(dir2);
        }

        wheels.set(i1, w2);
        wheels.set(i2, w1);
        ok = true;
    }
   // MINI-CYCLE 3 – Spin and query result

    /**
     * Spins a specific wheel at the given position.
     * Triggers the lever animation before spinning.
     *
     * @param wheelPos 1-based position of the wheel to spin
     */
    public void spin(int wheelPos) {
        if (lockedWheels.contains(wheelPos)) {
            MessageUtil.showError("Esta rueda está bloqueada, no puede girarse");
            ok = false;
            return;
        }

        animateLever();
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).spin();
            winnerAppearance();
            ok = true;
        } else {
            MessageUtil.showError("No existe esa rueda en la maquina");
            ok = false;
        }
    }

     /**
     * Spins all wheels simultaneously and check if is jackpot.
     * Triggers the lever animation before spinning.
     * Sets ok = false if there are no wheels.
     */
    public void spin() {
        animateLever();
        if (wheels.isEmpty()) {
            MessageUtil.showError("No hay ruedas por girar");
            ok = false;
            return;
        }
        if (!lockedWheels.isEmpty()){
            MessageUtil.showError("Algunas ruedas estan bloqueadas");
            ok = false;
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.spin();
        }
        winnerAppearance();
        ok = true;
    }

    /**
     * Changes the color of visible symbol of a wheel to the given one.
     * Then, checks if its jackpot.
     * 
     * @param wheelPos 1-based position of the wheel
     * @param symbol   color to display
     */
    public void placeSymbol(int wheelPos, String symbol) {
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).placeSymbol(symbol);
            winnerAppearance();
            ok = true;
        } else {
            MessageUtil.showError("No existe esa rueda, intenta de nuevo");
            ok = false;
        }
    }

    /**
     * Returns the current visible symbol of each wheel, in order.
     * Returns an empty array and shows an error if no symbols have been registered.
     *
     * @return array of visible color names, one per wheel
     */
    public String[] configuration() {
        if (!Wheel.symbols.isEmpty()) {
            String[] config = new String[wheels.size()];
            for (int i = 0; i < wheels.size(); i++) {
                config[i] = wheels.get(i).visibleSymbol();
            }
            ok = true;
            return config;
        } else {
            MessageUtil.showError("No existen símbolos aún");
            return new String[0];
        }
    }

    /**
     * Returns the number of unique symbols currently visible across all wheels.
     * Returns 0 and shows an error if no symbols have been registered.
     *
     * @return count of distinct visible symbols
     */
    public int distinctSymbols() {
        Set<String> uniqueSymbols = new HashSet<>();
        if (!Wheel.symbols.isEmpty()) {
            for (Wheel wheel : wheels) {
                uniqueSymbols.add(wheel.visibleSymbol());
            }
            ok = true;
            return uniqueSymbols.size();
        } else {
            MessageUtil.showError("No existen símbolos aún");
            return 0;
        }
    }

    /**
     * Returns true if all wheels are showing the same symbol.
     * Returns false if the wheels list is empty or no symbols have been registered.
     *
     * @return true on jackpot, false otherwise
     */
    public boolean isJackpot() {
        if (!Wheel.symbols.isEmpty()) {
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
        } else {
            MessageUtil.showError("No existen símbolos aún");
            ok = false;
            return false;
        }
    }
    /**
     * Spin a specific wheel a given number of steps.
     * If the machine is visible, the movement is animated step by step.
     * @param wheel 1-based position of the wheel
     * @param steps number of times to advance the symbol
     */
    public void spin(int wheel, int steps) {
        if (lockedWheels.contains(wheel)) {
            MessageUtil.showError("Esta rueda está bloqueada, no puede girarse");
            ok = false;
            return;
        }
        animateLever();
        int pos = adjustPosition(wheel);

        for (int i = 0; i < steps; i++) {
            wheels.get(pos).spin();
            if (visible) {
                pause();
            }
        }
        winnerAppearance();
        ok = true;
    }

    /**
     * Set a specific symbol configuration for all wheels.
     * @param setSymbols array of symbols, one per wheel in order
     */
    public void spin(String[] setSymbols) {
        if (!lockedWheels.isEmpty()){
            MessageUtil.showError("Algunas ruedas estan bloqueadas");
            ok = false;
            return;
        }

        if (setSymbols.length != wheels.size()) {
            MessageUtil.showError("No tiene los simbolos suficientes para las ruedas de la maquina");
            ok = false;
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).placeSymbol(setSymbols[i]);
        }
        winnerAppearance();
        ok = true;
    }
    // MINI-CYCLE 4 – Usability and visibility

    /**
     * Makes the board and all wheels visible.
     */
    public void makeVisible() {
        base.makeVisible();
        board.makeVisible();
        leverArm.makeVisible();
        lever.makeVisible();
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
        visible = true;
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
        base.makeInvisible();
        leverArm.makeInvisible();
        lever.makeInvisible();
        visible =  true;
        ok = true;
    }

    /** Finish - abort the application execution. */
    public void exit() {
        System.exit(0);
    }

    // Auxiliar Methods
    /** Returns true if the last operation was successful. */
    public boolean ok() {
        return ok;
    }

    // Converts a 1-based position to a 0-based index for the wheels list.
    // Returns 0 if pos <= 0, or wheels.size()-1 if pos > size.
    private int adjustPosition(int pos) {
        if (wheels.isEmpty()) return 0;
        if (pos <= 0) return 0;
        if (pos > wheels.size()) return wheels.size() - 1;
        return pos - 1;
    }

    // Pulls the lever down and back up to animate a spin.
    private void animateLever() {
        lever.slowMoveVertical(30);  // pull down
        lever.slowMoveVertical(-30); // return to original position
    }

    // Resizes the board and repositions base, arm and lever using stored Y fields
    // (baseY, leverArmY) and leverX so every move is a delta, never absolute.
    private void updateBoardSize() {
        int count = wheels.isEmpty() ? 1 : wheels.size();

        int rows = count / MAX_COLUMNS;
        if (count % MAX_COLUMNS != 0) {
            rows += 1; // incomplete last row
        }

        int columns = (count < MAX_COLUMNS) ? count : MAX_COLUMNS;

        int totalWidth  = columns * Wheel.CELL_SIZE + (columns - 1) * Wheel.GAP + 20;
        int totalHeight = rows    * Wheel.CELL_SIZE + (rows    - 1) * Wheel.GAP + 20;

        board.changeSize(totalHeight, totalWidth);

        // Base: follows the bottom edge of the board using a vertical delta
        int newBaseY = boardY + totalHeight;
        base.moveVertical(newBaseY - baseY);
        base.changeSize(30, totalWidth + 10);
        baseY = newBaseY;

        // Arm: sits just above the base (flush with the bottom of the board);
        // repositioned with deltas in both Y and X so it tracks the board's
        // growing width, not just its height
        int armHeight = 15;
        int armWidth  = 20;
        int newLeverArmY = boardY + (totalHeight / 2) - (armHeight / 2);
        int newLeverArmX = boardX + totalWidth;
        leverArm.moveVertical(newLeverArmY - leverArmY);
        leverArm.moveHorizontal(newLeverArmX - leverArmX);
        leverArm.changeSize(armHeight, armWidth);
        leverArmY = newLeverArmY;
        leverArmX = newLeverArmX;

        // Lever: repositioned horizontally with delta, resized to half board height,
        // bottom edge bottom-aligned with the arm's bottom edge (newLeverArmY + armHeight)
        int newLeverX = boardX + totalWidth + armWidth;
        int newLeverHeight = totalHeight / 2;
        int newLeverY = newLeverArmY + armHeight - newLeverHeight;
        lever.moveHorizontal(newLeverX - leverX);
        lever.moveVertical(newLeverY - leverY);
        lever.changeSize(newLeverHeight, 25);
        leverX = newLeverX;
        leverY = newLeverY;


        // Bring wheels back to the front after the board was redrawn
        if (visible){
            for (Wheel w : wheels) {
                w.makeVisible();
            }
        }
    }

    // Pauses execution briefly so the step-by-step movement can be visibly evidenced
    private void pause(){
        try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
    }

    private void winnerAppearance(){
        if (isJackpot() && visible) {
                board.changeColor("gold");
                makeVisible();
                MessageUtil.showSuccess("Has hecho JACKPOT, GANASTE");
            }
    } 

    
}