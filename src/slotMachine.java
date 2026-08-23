import java.util.ArrayList;

public class SlotMachine {
    private Rectangle board;
    private ArrayList<Wheel> wheels;
    private int lastWheelPosX = 1;
    private int lastWheelPosY = 1;

    public SlotMachine() {
        board = new Rectangle();
        board.changeSize(400, 650);
        board.makeVisible();
    }

    /**
     * 
     * @param pos position of the wheel on the array that goes
     * from 0 - 145 (145 = 9*14), being 9 the max amount of rows
     * and 14 the max amount of columns
     */
    public void addWheel(int pos) {
        if (pos < 1) pos = 1;

        if (wheels.size() >= 1) {
            // Limit of objects on the board columns (14)
            if (lastWheelPosX != 14) {
                // Move it 1 unit to the right
                lastWheelPosX += 1;

                Wheel w = new Wheel(lastWheelPosX, lastWheelPosY);
                wheels.add(pos - 1, w);
            } else {
                // Move it 1 unit to the bottom
                lastWheelPosY += 1;

                // Move to the left corner of the row
                lastWheelPosX = 1;

                // Limit of objects on the board rows (9)
                if (lastWheelPosY > 9) {
                    MessageUtil.showError("No es posible añadir otra rueda.");
                } else {
                    Wheel w = new Wheel(lastWheelPosX, lastWheelPosY);
                    if (pos > wheels.size() - 1) {
                        wheels.add(w);
                    } else {
                        wheels.add(pos - 1, w);
                    }
                }
            }
        } else{
            Wheel w = new Wheel(lastWheelPosX, lastWheelPosY);
        }
    }

    /**
     * 
     * @param pos
     */
    public void delWheel(int pos) {
        wheels.remove(pos-1);
    }
}