import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;

public class SlotMachine {
    public final static int MAX_COLUMNS = 14;
    public final static int MAX_ROWS = 9;

    private Rectangle board;
    private ArrayList<Wheel> wheels;
    private boolean ok;

    private int lastWheelPosX = 1;
    private int lastWheelPosY = 1;

    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.board = new Rectangle();
        this.board.changeColor("gray");

        int firstWheelX = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px
        int firstWheelY = (Wheel.CELL_SIZE + Wheel.GAP) + Wheel.GAP; // 90 px

        int boardX = firstWheelX - 10; // 80 px
        int boardY = firstWheelY - 10; // 80 px

        this.board.moveHorizontal(boardX);
        this.board.moveVertical(boardY);

        int defaultWidth = Wheel.CELL_SIZE + 20;
        int defaultHeight = Wheel.CELL_SIZE + 20;

        this.board.changeSize(defaultHeight, defaultWidth);
        this.board.makeVisible();
        this.ok = true;
    }

  public void addWheel(int pos) {
    if (pos < 1) {
        pos = 1;
    }
    if (pos > wheels.size() + 1) {
        pos = wheels.size() + 1;
    }
    if (pos > wheels.size()) {
        if (!wheels.isEmpty()) {
            if (lastWheelPosX != MAX_COLUMNS) {
                lastWheelPosX += 1;
            } else {
                lastWheelPosX = 1;
                lastWheelPosY += 1;
            }
        }

        if (lastWheelPosY > MAX_ROWS) {
            MessageUtil.showError("Ha alcanzado el máximo de ruedas posibles.");
            if (lastWheelPosX == 1) {
                lastWheelPosX = MAX_COLUMNS;
                lastWheelPosY -= 1;
            } else {
                lastWheelPosX -= 1;
            }
            return;
        }

        Wheel newWheel = new Wheel(lastWheelPosX, lastWheelPosY);
        if (!wheels.isEmpty()){
            copySymbols(wheels.get(0), newWheel);
        }
        wheels.add(newWheel);
        updateBoardSize();

        } else {

            int posX = ((pos - 1) % MAX_COLUMNS) + 1;
            int posY = ((pos - 1) / MAX_COLUMNS) + 1;

            Wheel newWheel = new Wheel(posX, posY);
            wheels.add(pos - 1, newWheel);
            

            for (int i = pos; i < wheels.size(); i++) {
                wheels.get(i).accommodate(1);
            }

            updateBoardSize();
        }
    }

    /**
 * Copia todos los símbolos de una wheel "molde" a una wheel nueva,
 * respetando el orden original.
 */
    private void copySymbols(Wheel source, Wheel target) {
        for (String symbol : source.symbols()) {
            target.addSymbol(Integer.MAX_VALUE, symbol);
        }
    
    }
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }

        int index = adjustPosition(pos);
        Wheel removedWheel = wheels.remove(index);
        removedWheel.makeInvisible();

        for (int i = index; i < wheels.size(); i++) {
            wheels.get(i).accommodate(-1);
        }

        updateBoardSize();
        ok = true;
    }

    public void addSymbol(int pos, String color) {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).addSymbol(pos, color);
            ok = true;
        }
    }

    public void delSymbol(String color) {
        boolean removed = false;
        for (Wheel wheel : wheels) {
            wheel.delSymbol(color);
            removed = true;
        }
        ok = removed;
    }

    public void placeSymbol(int wheelPos, String symbol) {
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).placeSymbol(symbol);
            ok = true;
        } else {
            ok = false;
        }
    }

    public void spin(int wheelPos) {
        int index = adjustPosition(wheelPos);
        if (index >= 0 && index < wheels.size()) {
            wheels.get(index).spin();
            ok = true;
        } else {
            ok = false;
        }
    }

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

    public String[] symbols() {
        String[] allSymbols;
        allSymbols = wheels.get(0).symbols();
        ok = true;
        return allSymbols;
    }

    public int distinctSymbols() {
        Set<String> uniqueSymbols = new HashSet<>();
        for (Wheel wheel : wheels) {
                uniqueSymbols.add(wheel.visibleSymbol());
            }
        ok = true;
        return uniqueSymbols.size();
    }

    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).visibleSymbol();
        }
        ok = true;
        return config;
    }

    public boolean isJackpot() {
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
    }

    public void makeVisible() {
        board.makeVisible();
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
        ok = true;
    }

    public void makeInvisible() {
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        board.makeInvisible();
        ok = true;
    }

    public void exit() {
        System.exit(0);
    }

    public boolean ok() {
        return ok;
    }

    // Regla de ajuste de posición en base 1
    private int adjustPosition(int pos) {
        if (pos < 1) return 0;
        if (pos > wheels.size()) return wheels.size();
        return pos - 1;
    }

    /**
     * Actualiza el ancho del tablero según la cantidad de ruedas actuales.
     * Si la lista está vacía, mantiene el ancho por defecto para 1 rueda.
     */
    private void updateBoardSize() {
        int count = wheels.isEmpty() ? 1 : wheels.size();

        int rows = count / MAX_COLUMNS;
        if (count % MAX_COLUMNS != 0) {
            rows += 1;
        }

        int columns = (count < MAX_COLUMNS) ? count : MAX_COLUMNS;

        int totalWidth = columns * Wheel.CELL_SIZE + (columns - 1) * Wheel.GAP + 20;
        int totalHeight = rows * Wheel.CELL_SIZE + (rows - 1) * Wheel.GAP + 20;

        board.changeSize(totalHeight, totalWidth);

        for (Wheel w : wheels) {
            w.makeVisible();
        }
    }
}