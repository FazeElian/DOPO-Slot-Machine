package domain;

import presentation.MessageUtil;
import presentation.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
/**
 * A slot machine: a gray board with a grid of up to 14 x 9 {@link Wheel}s.
 * Symbols are registered globally in {@link Symbol#symbols} and every wheel
 * owns its own copies. All public positions are <b>1-based</b>.
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public final class SlotMachine {

    /** Maximum number of columns in the wheel grid. */
    public final static int MAX_COLUMNS = 14;
    /** Maximum number of rows in the wheel grid. */
    public final static int MAX_ROWS = 9;
    /** Type name of the default wheels and symbols. */
    private static final String NORMAL = "normal";

    /** Gray rectangle that serves as the visual background for all wheels. */
    private Rectangle board;
    /** Black base that overflows 5px on each side of the board. */
    private Rectangle base;
    /** Horizontal black arm connecting the board to the lever. */
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

    /** X of the board, kept so updateBoardSize can reposition the shapes. */
    private int boardX;
    /** Y of the board, kept so updateBoardSize can reposition the shapes. */
    private int boardY;
    /** Current X of the lever, used to compute deltas on each resize. */
    private int leverX;
    /** Current Y of the lever, used to compute deltas on each resize. */
    private int leverY;
    /** Current Y of the base, used to compute deltas on each resize. */
    private int baseY;
    /** Current Y of the lever arm, used to compute deltas on each resize. */
    private int leverArmY;
    /** Current X of the lever arm, used to compute deltas on each resize. */
    private int leverArmX;
    /** Whether the machine is visible, so spins and swaps are animated. */
    private boolean visible;

    // MINI-CYCLE 1 – Visual structure of the machine
    
    /**
     * Constructs an empty SlotMachine with a gray board positioned
     * to wrap exactly one wheel cell (with a 10px margin).
     * The board grows automatically when wheels are added via {@link #addWheel(int)}.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        // A new machine starts with an empty catalog of symbols
        Symbol.symbols.clear();

        // First cell position: pos * (CELL_SIZE + GAP) + GAP = 1 * 80 + 10 = 90px
        final int firstWheelX = Wheel.CELL_SIZE + Wheel.GAP + Wheel.GAP; // 90 px
        final int firstWheelY = Wheel.CELL_SIZE + Wheel.GAP + Wheel.GAP; // 90 px

        boardX = firstWheelX - 10; // 80 px - stored as field for lever repositioning
        boardY = firstWheelY - 10; // 80 px - stored as field for arm/base vertical calc

        // Initial size: one cell + 10px margin on each side
        final int defaultWidth  = Wheel.CELL_SIZE + 20;
        final int defaultHeight = Wheel.CELL_SIZE + 20;

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
        final int armHeight = 8;
        final int armWidth  = 20;
        leverArmY = boardY + (defaultHeight / 2) - (armHeight / 2);
        this.leverArm = new Rectangle();
        this.leverArm.changeColor("black");
        this.leverArm.changeSize(armHeight, armWidth);
        this.leverArm.moveHorizontal(boardX + defaultWidth);
        this.leverArm.moveVertical(leverArmY);
        leverArmX = boardX + defaultWidth;

        // Lever: starts at mid-board going upward, bottom edge aligned with arm
        final int initialLeverHeight = defaultHeight / 2;
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
     * Constructs a SlotMachine with n wheels and n distinct symbols, both
     * random. Symbols are random hex colors (e.g. "#3fae1c") so there are
     * always n distinct ones. Used to test SlotMachineContest.solve(int).
     *
     * @param n number of wheels and symbols to create
     */
    public SlotMachine(final int n) {
        this(); // reuse the default constructor's visual setup (board, base, lever)

        final Random rand = new java.util.Random();
        final Set<String> usedColors = new java.util.HashSet<>();

        // Generate n distinct random hex colors and register them as symbols
        int added = 0;
        while (added < n) {
            final String color = String.format("#%06x", rand.nextInt(0x1000000));
            if (usedColors.add(color)) {
                addSymbol(added + 1, color);
                added++;
            }
        }
        final String[] colors = symbols();

        // Add n wheels to the board
        for (int i = 1; i <= n; i++) {
            addWheel(i); // Creates a NORMAL (default) wheel type
        }

        // Build and apply a random initial configuration
        final String[] initialConfig = new String[n];
        for (int i = 0; i < n; i++) {
            initialConfig[i] = colors[rand.nextInt(n)];
        }
        spin(initialConfig);
    }

    /**
     * Adds a new normal wheel at the given position.
     *
     * @param pos 1-based position where the new wheel is inserted
     */
    public void addWheel(final int pos) {
        addWheel(NORMAL, pos);
    }
    
    /**
     * Adds a new wheel at pos, corrected to 1..size+1 to avoid gaps; the
     * wheels after it are shifted one cell to the right.
     * @param type "normal", "lefty", "rebel" or "lazy" (null or "" is a
     *        normal wheel); any other value shows an error, sets ok = false
     *        and adds no wheel
     * @param pos  1-based position where the new wheel is inserted
     */
    public void addWheel(final String type, final int pos) {
        // Positions out of range are corrected to 1..size+1
        final int position = Math.max(1, Math.min(pos, wheels.size() + 1));

        try {
            // Grid limit: 14 x 9 = 126 wheels
            if (wheels.size() >= MAX_COLUMNS * MAX_ROWS) {
                throw new SlotMachineException(SlotMachineException.MAX_WHEELS);
            }

            // The grid cell is derived only from pos, so there are no
            // external counters that could fall out of sync
            final int posX = ((position - 1) % MAX_COLUMNS) + 1;
            final int posY = ((position - 1) / MAX_COLUMNS) + 1;

            Wheel newWheel;
            final String t = (type == null) ? "" : type.trim().toLowerCase();
            switch(t) {
                case "":
                case NORMAL: newWheel = new Wheel(posX, posY); break;
                case "lefty": newWheel = new LeftyWheel(posX, posY); break;
                case "rebel": newWheel = new RebelWheel(posX, posY); break;
                case "lazy": newWheel = new LazyWheel(posX, posY); break;
                default:
                    throw new SlotMachineException(String.format(SlotMachineException.INVALID_WHEEL_TYPE, type));
            }

            if(position > wheels.size()) {
                wheels.add(newWheel);
            } else {
                wheels.add(position - 1, newWheel);
                // Move every wheel after the new one one position to the right
                for (int i = position; i < wheels.size(); i++) {
                    wheels.get(i).accommodate(1);
                }
            }

            updateBoardSize();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Removes the wheel at the given position and shifts all subsequent wheels
     * one position to the left with accommodate(-1).
     *
     * @param pos 1-based position of the wheel to remove
     */
    public void delWheel(final int pos) {
        try {
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }

            final int index = adjustPosition(pos);
            final Wheel w = wheels.get(index);
            if (!w.canBeRemoved()) {
                throw new SlotMachineException(String.format(SlotMachineException.CANT_REMOVE, index + 1));
            }
            final Wheel removedWheel = wheels.remove(index);
            removedWheel.makeInvisible(); // remove from screen before shifting
            // Move every wheel after the removed one one position to the left
            for (int i = index; i < wheels.size(); i++) {
                wheels.get(i).accommodate(-1); 
            }

            updateBoardSize();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }
    /**
     * Lock a specific wheel so it cannot be spun. If the wheel doesn't
     * exist, is already locked or refuses to be locked (a rebel wheel),
     * shows an error and sets ok = false.
     * @param wheel 1-based position of the wheel to lock
     */
    public void lock(final int wheel) {
        try {
            if (wheel < 1 || wheel > wheels.size()) {
                throw new SlotMachineException(SlotMachineException.WHEEL_NOT_FOUND);
            }
            final Wheel w = wheels.get(wheel-1);
            w.lock(); // may propagate a SlotMachineException from the wheel
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Unlock a specific wheel so it can be spun again.
     * If the position doesn't exist, shows an error and sets ok = false.
     * @param wheel 1-based position of the wheel to unlock
     */
    public void unlock(final int wheel) {
        try {
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }
            if (wheel < 1 || wheel > wheels.size()) {
                throw new SlotMachineException(SlotMachineException.WHEEL_NOT_FOUND);
            }
            final Wheel w =  wheels.get(wheel-1);
            w.unlock();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    // MINI-CYCLE 2 – Symbols
    /**
     * Adds a new normal symbol (color) at the given position.
     *
     * @param pos   1-based position in the global symbol list
     * @param color color name to add
     */
    public void addSymbol(final int pos, final String color) {
        addSymbol(NORMAL, pos, color);
    }

    /**
     * Registers a new symbol of the given type at pos (its color lowercased)
     * and adds it to every wheel. If the color exists or the type is
     * unknown, shows an error and sets ok = false.
     * @param type  "normal", "ephemeral" or "shy" (null or "" is normal)
     * @param pos   1-based position in the global symbol list
     * @param color color name to add
     */
    public void addSymbol(final String type, final int pos, final String color) {
        final String name = color.toLowerCase();
        try {
            // indexOf returns -1 if the symbol is not yet registered
            if (Symbol.indexOf(name) != -1) {
                throw new SlotMachineException(String.format(SlotMachineException.SYMBOL_EXISTS, color.toUpperCase()));
            }
            int index;
            if (pos < 1) {
                index = 0;
            } else if (pos > Symbol.symbols.size()) {
                index = Symbol.symbols.size();
            } else {
                index = pos - 1;
            }
                        
            final String t = (type == null) ? "" : type.trim().toLowerCase();
            Symbol symbol;
            switch (t) {
                case "":
                case NORMAL: symbol = new Symbol(name); break;
                case "ephemeral": symbol = new EphemeralSymbol(name); break;
                case "shy": symbol = new ShySymbol(name); break;
                case "dizzy": symbol = new DizzySymbol(name); break;
                default:
                    throw new SlotMachineException(String.format(SlotMachineException.INVALID_SYMBOL_TYPE, type));
            }
            
            Symbol.symbols.add(index, symbol);
            // Every wheel inserts its own copy of the Symbol at the same index
            for (final Wheel wheel : wheels) {
                wheel.addSymbol(index, symbol);
            }
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Removes a color from the global symbol list and from every wheel.
     * If the color does not exist, shows an error and sets ok = false.
     *
     * @param color color name to remove
     */
    public void delSymbol(final String color) {
        final String c = color.trim().toLowerCase();
        try {
            final int index = Symbol.indexOf(c);
            if (index == -1) {
                throw new SlotMachineException(String.format(SlotMachineException.SYMBOL_NOT_FOUND, color.toUpperCase()));
            }
            if (Symbol.symbols.size() <= 1) {
                throw new SlotMachineException(SlotMachineException.LAST_SYMBOL);
            }
            for (final Wheel wheel : wheels) {
                wheel.delSymbol(c);
            }
            Symbol.symbols.remove(index);
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                // Deleting the last symbol is only a warning, the other cases are errors
                if (SlotMachineException.LAST_SYMBOL.equals(e.getMessage())) {
                    MessageUtil.showWarning(e.getMessage());
                } else {
                    MessageUtil.showError(e.getMessage());
                }
            }
        }
    }

    /**
     * Returns the global list of registered symbols on the board.
     *
     * @return array of color names (or hex codes, as they were entered)
     */
    public String[] symbols() {
        final String[] names = new String[Symbol.symbols.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = Symbol.symbols.get(i).getName();
        }
        return names;
    }

    /**
     * Swap two wheels based on their 1-based positions.
     * Both positions must correspond to existing wheels; otherwise the
     * operation fails and shows an error.
     * @param wheel1 position of the first wheel
     * @param wheel2 position of the second wheel
     */
    public void swap(final int wheel1, final int wheel2) {
        try {
            if (wheel1 == wheel2) {
                throw new SlotMachineException(SlotMachineException.SAME_WHEEL);
            }
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }

            final int i1 = adjustPosition(wheel1);
            final int i2 = adjustPosition(wheel2);

            if (i1 == i2) {
                throw new SlotMachineException(SlotMachineException.SAME_WHEEL);
            }

            final Wheel w1 = wheels.get(i1);
            final Wheel w2 = wheels.get(i2);

            if (!w1.canBeSwapped() || !w2.canBeSwapped()) {
                throw new SlotMachineException(SlotMachineException.CANT_SWAP);
            }

            // Same formula used by addWheel to calculate posX/posY (1-based)
            final int posX1 = ((wheel1 - 1) % MAX_COLUMNS) + 1;
            final int posY1 = ((wheel1 - 1) / MAX_COLUMNS) + 1;
            final int posX2 = ((wheel2 - 1) % MAX_COLUMNS) + 1;
            final int posY2 = ((wheel2 - 1) / MAX_COLUMNS) + 1;

            animateSwap(w1, w2, posX1, posY1, posX2, posY2);

            // Each wheel is moved to the other's position, then the list
            // is updated to reflect the swap
            w1.setLocation(posX2, posY2);
            w2.setLocation(posX1, posY1);

            final Wheel temp = wheels.get(i1);
            wheels.set(i1, wheels.get(i2));
            wheels.set(i2, temp);
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }
        // Small visual "hop" so the movement is noticeable before the final setLocation
       private void animateSwap(final Wheel w1, final Wheel w2, final int posX1, final int posY1, final int posX2, final int posY2) {
            final int hop = 10;
            final int deltaX = (posX2 - posX1) * (Wheel.CELL_SIZE + Wheel.GAP);
            final int deltaY = (posY2 - posY1) * (Wheel.CELL_SIZE + Wheel.GAP);

            w1.slowMoveVertical(-hop);
            w1.slowMoveHorizontal(deltaX);
            w1.slowMoveVertical(hop + deltaY);

            w2.slowMoveVertical(hop);
            w2.slowMoveHorizontal(-deltaX);
            w2.slowMoveVertical(-hop - deltaY);
        }
   // MINI-CYCLE 3 – Spin and query result

    /**
     * Spins a specific wheel at the given position.
     * Triggers the lever animation before spinning.
     *
     * @param wheelPos 1-based position of the wheel to spin
     */
    public void spin(final int wheelPos) {
        animateLever();
        try {
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }

            final int index = adjustPosition(wheelPos);
            final Wheel w = wheels.get(index);
            w.setLeftNeighbor(index > 0 ? wheels.get(index - 1) : null);
            w.spin(); // may propagate a SlotMachineException from the wheel
            winnerAppearance();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

     /**
     * Spins all wheels simultaneously and check if is jackpot.
     * Triggers the lever animation before spinning.
     * Sets ok = false if there are no wheels.
     */
    public void spin() {
        animateLever();
        try {
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }
            for (int i = 0; i < wheels.size(); i++) {
                // Handled per wheel: a locked wheel must not stop the others from spinning
                try {
                    final Wheel wheel = wheels.get(i);
                    wheel.setLeftNeighbor(i > 0 ? wheels.get(i - 1) : null);
                    wheel.spin(); // may propagate a SlotMachineException from the wheel
                } catch (SlotMachineException e) {
                    if (visible) {
                        MessageUtil.showError(e.getMessage());
                    }
                }
            }
            winnerAppearance();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Shows the given symbol on a wheel and then checks for a jackpot. If
     * there are no wheels, the wheel is locked or the symbol doesn't exist,
     * shows an error and sets ok = false.
     * @param wheelPos 1-based position of the wheel
     * @param symbol   color to display
     */
    public void placeSymbol(final int wheelPos, final String symbol) {
        try {
            if (wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }
            final int index = adjustPosition(wheelPos);
            final Wheel w = wheels.get(index);

            if (index < 0 || index >= wheels.size()) {
                throw new SlotMachineException(SlotMachineException.WHEEL_NOT_FOUND);
            }
            w.placeSymbol(symbol); // may propagate a SlotMachineException from the wheel
            winnerAppearance();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Returns the current visible symbol of each wheel, in order.
     * Returns an empty array, shows an error and sets ok = false if no
     * symbols have been registered.
     *
     * @return array of visible color names, one per wheel
     */
    public String[] configuration() {
        try {
            if (Symbol.symbols.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_SYMBOLS);
            }
            final String[] config = new String[wheels.size()];
            for (int i = 0; i < wheels.size(); i++) {
                config[i] = wheels.get(i).visibleSymbol();
            }
            ok = true;
            return config;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
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
        final Set<String> uniqueSymbols = new HashSet<>();
        try {
            if (Symbol.symbols.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_SYMBOLS);
            }
            for (final Wheel wheel : wheels) {
                uniqueSymbols.add(wheel.visibleSymbol());
            }
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
        // Empty when the exception was thrown, so it returns 0
        return uniqueSymbols.size();
    }

    /**
     * Returns true if all wheels are showing the same symbol.
     * Returns false if the wheels list is empty or no symbols have been registered.
     *
     * @return true on jackpot, false otherwise
     */
    public boolean isJackpot() {
        try {
            if (Symbol.symbols.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_SYMBOLS);
            }
            if (wheels.isEmpty() || wheels.size()==1) {
                ok = true;
                return false;
            }
            final String first = wheels.get(0).visibleSymbol();
            for (final Wheel wheel : wheels) {
                if (!wheel.visibleSymbol().equals(first)) {
                    ok = true;
                    return false;
                }
            }
            ok = true;
            return true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
            return false;
        }
    }
    /**
     * Spins a wheel a given number of steps, animated if the machine is
     * visible. Each step counts as a spin for its symbols (an ephemeral
     * shrinks once per step) and selects that step's symbol.
     * @param wheel 1-based position of the wheel
     * @param steps number of times to advance the symbol
     */
    public void spin(final int wheel, final int steps) {
        try {
            if (this.wheels.isEmpty()) {
                throw new SlotMachineException(SlotMachineException.NO_WHEELS);
            }

            animateLever();
            final int pos = adjustPosition(wheel);

            final Wheel w = wheels.get(pos);
            w.setLeftNeighbor(pos > 0 ? wheels.get(pos - 1) : null);

            final int direction = (steps < 0) ? -1 : 1;
            final int totalSteps = Math.abs(steps);

            for (int i = 0; i < totalSteps; i++) {
                wheels.get(pos).spin(direction); // may propagate a SlotMachineException from the wheel
                if (visible) {
                    pause();
                }
            }
            winnerAppearance();
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
    }

    /**
     * Set a specific symbol configuration for all wheels.
     * @param setSymbols array of symbols, one per wheel in order
     */
    public void spin(final String[] setSymbols) {
        try {
            if (setSymbols.length != wheels.size()) {
                throw new SlotMachineException(SlotMachineException.NOT_ENOUGH_SYMBOLS);
            }
            Wheel w;
            for (int i = 0; i < wheels.size(); i++) {
                // Handled per wheel: an error on one wheel must not stop the others
                try {
                    w = wheels.get(i);
                    w.placeSymbol(setSymbols[i]); // may propagate a SlotMachineException from the wheel
                } catch (SlotMachineException e) {
                    if (visible) {
                        MessageUtil.showError(e.getMessage());
                    }
                }
            }
            winnerAppearance();

            if (!Arrays.equals(configuration(), setSymbols)) {
                throw new SlotMachineException(SlotMachineException.SYMBOLS_MISMATCH);
            }
            ok = true;
        } catch (SlotMachineException e) {
            ok = false;
            if (visible) {
                MessageUtil.showError(e.getMessage());
            }
        }
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
        for (final Wheel wheel : wheels) {
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
        for (final Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        board.makeInvisible();
        base.makeInvisible();
        leverArm.makeInvisible();
        lever.makeInvisible();
        visible = false;
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
    private int adjustPosition(final int pos) {
        if (wheels.isEmpty()) {
            return 0;
        }
        if (pos <= 0) {
            return 0;
        }
        if (pos > wheels.size()) {
            return wheels.size() - 1;
        }
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
        final int count = wheels.isEmpty() ? 1 : wheels.size();

        int rows = count / MAX_COLUMNS;
        if (count % MAX_COLUMNS != 0) {
            rows += 1; // incomplete last row
        }

        final int columns = (count < MAX_COLUMNS) ? count : MAX_COLUMNS;

        final int totalWidth  = columns * Wheel.CELL_SIZE + (columns - 1) * Wheel.GAP + 20;
        final int totalHeight = rows    * Wheel.CELL_SIZE + (rows    - 1) * Wheel.GAP + 20;

        board.changeSize(totalHeight, totalWidth);

        // Base: follows the bottom edge of the board using a vertical delta
        final int newBaseY = boardY + totalHeight;
        base.moveVertical(newBaseY - baseY);
        base.changeSize(30, totalWidth + 10);
        baseY = newBaseY;

        // Arm: sits just above the base (flush with the bottom of the board);
        // repositioned with deltas in both Y and X so it tracks the board's
        // growing width, not just its height
        final int armHeight = 15;
        final int armWidth  = 20;
        final int newLeverArmY = boardY + (totalHeight / 2) - (armHeight / 2);
        final int newLeverArmX = boardX + totalWidth;
        leverArm.moveVertical(newLeverArmY - leverArmY);
        leverArm.moveHorizontal(newLeverArmX - leverArmX);
        leverArm.changeSize(armHeight, armWidth);
        leverArmY = newLeverArmY;
        leverArmX = newLeverArmX;

        // Lever: repositioned horizontally with delta, resized to half board height,
        // bottom edge bottom-aligned with the arm's bottom edge (newLeverArmY + armHeight)
        final int newLeverX = boardX + totalWidth + armWidth;
        final int newLeverHeight = totalHeight / 2;
        final int newLeverY = newLeverArmY + armHeight - newLeverHeight;
        lever.moveHorizontal(newLeverX - leverX);
        lever.moveVertical(newLeverY - leverY);
        lever.changeSize(newLeverHeight, 25);
        leverX = newLeverX;
        leverY = newLeverY;


        // Bring wheels back to the front after the board was redrawn
        if (visible){
            for (final Wheel w : wheels) {
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
                if (visible) {
                    MessageUtil.showSuccess("Has hecho JACKPOT, GANASTE");
                }
            }
    } 

    /**
     * Returns the wheel at the given 1-based position.
     * @param pos 1-based position of the wheel
     * @return the wheel, or null if the position does not exist
     */
    public Wheel getWheel(final int pos) {
        if (pos < 1 || pos > wheels.size()) {
            return null;
        }
        return wheels.get(pos - 1);
    }
}
