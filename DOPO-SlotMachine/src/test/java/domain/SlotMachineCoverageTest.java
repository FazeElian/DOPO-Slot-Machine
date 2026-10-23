package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for the domain package: error cases and branches of
 * SlotMachine, Wheel and SlotMachineContest that the cycle
 * tests don't reach. The machine is never made visible, so no message
 * dialog is opened.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineCoverageTest
{
    private SlotMachine slotMachine;

    /**
     * Default constructor for test class SlotMachineCoverageTest
     */
    public SlotMachineCoverageTest()
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

    // SlotMachine: lock & unlock

    /**
     * Verifies that a wheel that doesn't exist or is already locked
     * can't be locked.
     */
    @Test
    public void shouldNotLockMissingOrAlreadyLockedWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);

        slotMachine.lock(0);
        assertFalse(slotMachine.ok());
        slotMachine.lock(99);
        assertFalse(slotMachine.ok());

        slotMachine.lock(1);
        assertTrue(slotMachine.ok());
        slotMachine.lock(1);
        assertFalse(slotMachine.ok());
        assertTrue(slotMachine.getWheel(1).isLocked());
    }

    /**
     * Verifies that unlock fails when the machine has no wheels or the
     * wheel doesn't exist.
     */
    @Test
    public void shouldNotUnlockWithoutWheelsOrMissingWheel() {
        slotMachine.unlock(1);
        assertFalse(slotMachine.ok());

        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);
        slotMachine.unlock(0);
        assertFalse(slotMachine.ok());
        slotMachine.unlock(99);
        assertFalse(slotMachine.ok());
    }

    // SlotMachine: symbols

    /**
     * Verifies that a symbol added at a position below 1 is inserted
     * first in the catalog.
     */
    @Test
    public void shouldAddSymbolFirstWhenPositionIsBelowOne() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(0, "blue");
        slotMachine.addSymbol(-5, "green");

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"green", "blue", "red"}, slotMachine.symbols());
    }

    // SlotMachine: swap

    /**
     * Verifies that swap fails without wheels and when both positions
     * end up being the same wheel after being adjusted.
     */
    @Test
    public void shouldNotSwapWithoutWheelsOrSameAdjustedWheel() {
        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());

        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);
        // 99 is adjusted to the last wheel and 0 to the first: both are wheel 1
        slotMachine.swap(1, 99);
        assertFalse(slotMachine.ok());
        slotMachine.swap(0, 1);
        assertFalse(slotMachine.ok());
    }

    // SlotMachine: spins and placeSymbol

    /**
     * Verifies that spinning one wheel, with or without steps, fails
     * when the machine has no wheels.
     */
    @Test
    public void shouldNotSpinAWheelWithoutWheels() {
        slotMachine.addSymbol(1, "red");

        slotMachine.spin(1);
        assertFalse(slotMachine.ok());
        slotMachine.spin(1, 3);
        assertFalse(slotMachine.ok());
    }

    /**
     * Verifies that spinning all the wheels skips the locked one and
     * still spins the others.
     */
    @Test
    public void shouldSpinAllExceptLockedWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "green");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.lock(1);

        slotMachine.spin();

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"red", "green"}, slotMachine.configuration());
    }

    /**
     * Verifies that a symbol can't be placed on a locked wheel.
     */
    @Test
    public void shouldNotPlaceSymbolOnLockedWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "green");
        slotMachine.addWheel(1);
        slotMachine.lock(1);

        slotMachine.placeSymbol(1, "green");

        assertFalse(slotMachine.ok());
        assertEquals("red", slotMachine.configuration()[0]);
    }

    // SlotMachine: queries

    /**
     * Verifies that there's no jackpot without symbols (an error) nor
     * with less than two wheels (not an error).
     */
    @Test
    public void shouldNotBeJackpotWithoutSymbolsOrWithLessThanTwoWheels() {
        assertFalse(slotMachine.isJackpot());
        assertFalse(slotMachine.ok());

        slotMachine.addSymbol(1, "red");
        assertFalse(slotMachine.isJackpot());
        assertTrue(slotMachine.ok());

        slotMachine.addWheel(1);
        assertFalse(slotMachine.isJackpot());
        assertTrue(slotMachine.ok());
    }

    /**
     * Verifies that getWheel returns null for positions that don't exist.
     */
    @Test
    public void shouldReturnNullForWheelOutOfRange() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);

        assertNull(slotMachine.getWheel(0));
        assertNull(slotMachine.getWheel(2));
        assertNotNull(slotMachine.getWheel(1));
    }

    /**
     * Verifies that every wheel can be deleted, leaving the machine
     * empty, and that deleting from an empty machine fails.
     */
    @Test
    public void shouldDeleteEveryWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        slotMachine.delWheel(1);
        slotMachine.delWheel(1);
        assertTrue(slotMachine.ok());
        assertEquals(0, slotMachine.configuration().length);

        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
    }

    // Wheel

    /**
     * Verifies that a wheel ignores deleting a symbol it doesn't have
     * and deleting its last symbol.
     */
    @Test
    public void wheelShouldIgnoreDeletingMissingOrLastSymbol() {
        slotMachine.addSymbol(1, "red");
        Wheel wheel = new Wheel(1, 1);

        wheel.delSymbol("pink");
        assertEquals("red", wheel.visibleSymbol());
        wheel.delSymbol("red");
        assertEquals("red", wheel.visibleSymbol());
    }

    /**
     * Verifies that a wheel keeps showing the right symbol when a symbol
     * before, after or at its current position is deleted.
     */
    @Test
    public void wheelShouldKeepTheRightSymbolWhenSymbolsAreDeleted() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "green");
        slotMachine.addSymbol(3, "blue");
        slotMachine.addSymbol(4, "yellow");
        slotMachine.addWheel(1);
        slotMachine.placeSymbol(1, "blue");

        // Before and after the current symbol: it keeps showing blue
        slotMachine.delSymbol("red");
        assertEquals("blue", slotMachine.configuration()[0]);
        slotMachine.delSymbol("yellow");
        assertEquals("blue", slotMachine.configuration()[0]);

        // The shown symbol is the first one: it shows the next one
        slotMachine.placeSymbol(1, "green");
        slotMachine.delSymbol("green");
        assertEquals("blue", slotMachine.configuration()[0]);

        // The shown symbol isn't the first one: it shows the previous one
        slotMachine.addSymbol(2, "red");
        slotMachine.placeSymbol(1, "red");
        slotMachine.delSymbol("red");
        assertEquals("blue", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that a wheel created without registered symbols can be
     * spun and shows no symbol.
     */
    @Test
    public void wheelWithoutSymbolsShouldNotFail() throws SlotMachineException {
        Wheel wheel = new Wheel(1, 1);

        wheel.spin();

        assertNull(wheel.getShownSymbol());
    }

    /**
     * Verifies that wheels move to the next row when a wheel is inserted
     * before the end of a full row, and back to the previous row when a
     * wheel before them is deleted.
     */
    @Test
    public void wheelsShouldMoveBetweenRows() {
        slotMachine.addSymbol(1, "red");
        for (int i = 1; i <= SlotMachine.MAX_COLUMNS; i++) {
            slotMachine.addWheel(i);
        }

        // The wheel at the end of row 1 moves to the start of row 2
        slotMachine.addWheel(1);
        assertTrue(slotMachine.ok());
        assertEquals(SlotMachine.MAX_COLUMNS + 1, slotMachine.configuration().length);

        // The wheel at the start of row 2 moves back to the end of row 1
        slotMachine.delWheel(1);
        assertTrue(slotMachine.ok());
        assertEquals(SlotMachine.MAX_COLUMNS, slotMachine.configuration().length);
    }

    // SlotMachineContest

    /**
     * Verifies that the contest solver can be instantiated.
     */
    @Test
    public void shouldCreateTheContestSolver() {
        assertNotNull(new SlotMachineContest());
    }
}
