package test;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Acceptance test for the slot machine. The machine is made visible and a
 * full game is played step by step with every type of wheel (normal, lefty,
 * rebel, lazy) and of symbol (normal, ephemeral, shy, dizzy), checking the
 * state of the machine after every step. The messages the machine shows
 * (the rebel refusing to be locked and the jackpot) must be closed to go
 * on. It is meant to be run from VS Code, so it is excluded from the Maven
 * build.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class AcceptanceTest
{
    private static final int PAUSE = 1500;

    private SlotMachine slotMachine;

    /**
     * Default constructor for test class AcceptanceTest
     */
    public AcceptanceTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        // A new machine starts with an empty catalog of symbols
        slotMachine = new SlotMachine();
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        slotMachine.makeInvisible();
    }

    /**
     * A user builds a machine with one wheel and one symbol of every type
     * and plays until getting a jackpot, seeing how every special wheel and
     * symbol behaves along the way.
     */
    @Test
    public void accordingPoIbShouldPlayAGameWithEveryTypeUntilJackpot() {
        // 1. One symbol and one wheel of every type, set on four different symbols.
        //    The shy symbol hides the first time it is selected
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        slotMachine.addSymbol("dizzy", 4, "yellow");
        slotMachine.addWheel("normal", 1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.addWheel("rebel", 3);
        slotMachine.addWheel("lazy", 4);
        slotMachine.makeVisible();
        slotMachine.spin(new String[]{"red", "blue", "green", "yellow"});
        assertArrayEquals(new String[]{"red", "blue", "green", "yellow"}, slotMachine.configuration());
        assertTrue(shownOn(3).isHidden());
        pause();

        // 2. The normal wheel goes to the ephemeral symbol, which shrinks on the spin
        slotMachine.spin(1, 1);
        assertEquals("blue", slotMachine.configuration()[0]);
        assertTrue(shownOn(1).getWidth() < Symbol.SYMBOL_SIZE);
        pause();

        // 3. The lefty wheel copies the symbol of the wheel on its left
        slotMachine.spin(2, 1);
        assertArrayEquals(new String[]{"blue", "blue", "green", "yellow"}, slotMachine.configuration());
        pause();

        // 4. The lazy wheel moves on the first spin and ignores the second one
        slotMachine.spin(4, 1);
        assertEquals("red", slotMachine.configuration()[3]);
        pause();
        slotMachine.spin(4, 1);
        assertTrue(slotMachine.ok());
        assertEquals("red", slotMachine.configuration()[3]);
        pause();

        // 5. The normal wheel goes to the shy symbol, which hides, and then to
        //    the dizzy one, narrower and upside down after the odd spins
        slotMachine.spin(1, 1);
        assertEquals("green", slotMachine.configuration()[0]);
        assertTrue(shownOn(1).isHidden());
        pause();
        slotMachine.spin(1, 1);
        assertEquals("yellow", slotMachine.configuration()[0]);
        assertEquals(DizzySymbol.WIDTH, shownOn(1).getWidth());
        assertTrue(shownOn(1).getHeight() < 0);
        pause();

        // 6. The rebel wheel refuses to be locked (the machine shows an error)
        slotMachine.lock(3);
        assertFalse(slotMachine.ok());
        assertFalse(slotMachine.getWheel(3).isLocked());
        pause();

        // 7. The normal wheel is locked and the whole machine spins: the locked
        //    wheel stays, the lefty copies it and the lazy one moves again
        slotMachine.lock(1);
        slotMachine.spin();
        assertArrayEquals(new String[]{"yellow", "yellow", "yellow", "blue"}, slotMachine.configuration());
        assertTrue(slotMachine.getWheel(1).isLocked());
        assertEquals(2, slotMachine.distinctSymbols());
        assertFalse(slotMachine.isJackpot());
        pause();

        // 8. The normal wheel is unlocked and swapped with the lefty one
        slotMachine.unlock(1);
        slotMachine.swap(1, 2);
        assertTrue(slotMachine.ok());
        assertTrue(slotMachine.getWheel(1) instanceof LeftyWheel);
        assertArrayEquals(new String[]{"yellow", "yellow", "yellow", "blue"}, slotMachine.configuration());
        pause();

        // 9. Every wheel is set on red: jackpot (the machine shows the message)
        slotMachine.spin(new String[]{"red", "red", "red", "red"});
        assertArrayEquals(new String[]{"red", "red", "red", "red"}, slotMachine.configuration());
        assertTrue(slotMachine.isJackpot());
        assertTrue(slotMachine.ok());
        pause();
    }

    // Symbol the wheel at the given 1-based position is showing
    private Symbol shownOn(final int wheel) {
        return slotMachine.getWheel(wheel).getShownSymbol();
    }

    // Pauses so the user can follow every step of the game
    private void pause() {
        try {
            Thread.sleep(PAUSE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
