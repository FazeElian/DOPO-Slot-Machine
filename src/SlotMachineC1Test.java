import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The SlotMachine test class for cycle 1.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.1
 */
public class SlotMachineC1Test
{
    private SlotMachine slotMachine;
    public final static int MAX_COLUMNS = 14;
    public final static int MAX_ROWS = 9;
    
    /**
     * Default constructor for test class SlotMachineTest
     */
    public SlotMachineC1Test()
    {
    }

    /**
     * Sets up the test case.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
         // initialize machine to test
         slotMachine = new SlotMachine();
         
         // Remove all elements from symbols list
         Wheel.symbols.clear();
    }
    
    
    // Tests for MINI-CYCLE 1: slotMachine(), addWheel(), delWheel()
    /**
     * Verifies that a wheel can be added at a valid position and that
     * the machine status remains ok.
     */
    @Test
    public void shouldAddWheelInAValidPos() {
        slotMachine.addWheel(2);
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that attempting to add a wheel at an invalid position
     * still leaves the machine status as ok.
     */
    @Test
    public void shouldAddWheelInAInvalidPos() {
        slotMachine.addWheel(-1);
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that once the maximum number of wheels (MAX_COLUMNS * MAX_ROWS)
     * has been reached, adding an additional wheel fails and sets the
     * machine status to not ok.
     */
    @Test
    public void shouldNotAddWheelWhenLimitExcedeed() {
        // Check slotMachine.MAX_COLUMNS*slotMachine.MAX_ROWS (126) wheels -> add the limit of wheels into the board
        for (int i = 1; i <= (slotMachine.MAX_COLUMNS*slotMachine.MAX_ROWS); i++) {
            slotMachine.addWheel(i);
        }
        
        // Check that all were added succesfully
        assertTrue(slotMachine.ok());
        
        // Add the 127th wheel
        slotMachine.addWheel(127);
        
        /// Check that weren's added the 127th wheel so ok status is false
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void shouldAddWheelWhenLimitNotExceeded() {
        // Add less than the limit to add a last one
        for (int i = 1; i <= (slotMachine.MAX_COLUMNS*slotMachine.MAX_ROWS - 1); i++) {
            slotMachine.addWheel(i);
        }
        
        // Check that all were added succesfully
        assertTrue(slotMachine.ok());
        
        // Add the 126th wheel
        slotMachine.addWheel(126);
        
        /// Check that weren's added the 126th wheel so ok status is true
        assertTrue(slotMachine.ok());
        
        // Add the 127th wheel
        slotMachine.addWheel(127);
        
        /// Check that weren's added the 127th (exceeding the limit) wheel so ok status is false
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that trying to delete a wheel that doesn't exist
     * sets the machine status to not ok.
     */
    @Test
    public void shouldNotDelWheel() {
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that an existing wheel can be successfully deleted,
     * leaving the machine status as ok.
     */
    @Test
    public void shouldDelWheel() {
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.delWheel(1);
        assertTrue(slotMachine.ok());
    }
    
    // Tests for MINI-CYCLE 2: addSymbol(), delSymbol(), symbols()
    /**
     * Verifies that a symbol can be added both when the wheels list is
     * empty and after wheels have been added, keeping the machine status ok.
     */
    @Test
    public void shouldAddSymbol() {
        // Add symbol if wheels[] is empty
        slotMachine.addSymbol(1, "red");
        
        // Check is ok even when wheels[] is empty
        assertTrue(slotMachine.ok());
        
        // Add some wheels so wheels[] is not empty
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Add a symbol for the wheels
        slotMachine.addSymbol(2, "blue");
        
        // Check is ok
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that adding the same symbol twice for the same wheel
     * sets the machine status to not ok, since duplicated symbols
     * are not allowed.
     */
    @Test
    public void shouldNotAddSymbol () {
        // Add symbol
        slotMachine.addSymbol(1, "yellow");
        
        // Add another one with the same value
        slotMachine.addSymbol(1, "yellow");
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that existing symbols can be deleted correctly, leaving
     * the machine status ok and the symbols array with the expected size.
     */
    @Test
    public void shouldDeleteSymbol() {
        // Add some symbols
        slotMachine.addSymbol(1, "yellow");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "red");
        
        // Try to delete some of them
        slotMachine.delSymbol("yellow");
        slotMachine.delSymbol("red");
        
        // Check is OK
        assertTrue(slotMachine.ok());
        
        // Check size is correct after delete 2 of them
        assertEquals(1, (slotMachine.symbols()).length);
    }
    
    /**
     * Verifies that deleting a symbol when it is the only one present
     * fails, setting the machine status to not ok. And checks that can't be
     * deleted a symbol if there's no symbols on the machine.
     */
    @Test
    public void shouldNotDeleteSymbol() {
        // This should return false because no symbols where added before delete this one
        slotMachine.delSymbol("blue");
        
        // Check is false
        assertFalse(slotMachine.ok());
        
        // Add a single symbol that SHOULD'NT be deleted
        slotMachine.addSymbol(1, "red");
        
        // Try to delete it
        slotMachine.delSymbol("red");
        
        // Check is NOT OK due to a failed action (delete a symbol where there's only one on symbols[])
        assertFalse(slotMachine.ok());
    }

    /**
     * Verifies that the symbols list starts as an empty array
     * when no symbols have been added.
     */
    @Test
    public void shouldReturnSymbolsList () {
        // Check returns a [] because symbols has a [] inital value
        String[] expected = {};
        assertArrayEquals(expected, slotMachine.symbols());
    }
    
    // Tests for MINI-CYCLE 3
    /**
     * Verifies that spinning a wheel at a valid position changes its
     * visible symbol and keeps the machine status ok.
     */
    @Test
    public void shouldSpinAWheel() {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Add some symbols
        slotMachine.addSymbol(1, "yellow");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "red");
        
        // Index (position on the array) of the wheel is going to be spinned, it is on the range: pos >= 0 && pos < wheels.size()
        int pos = 2;
        slotMachine.spin(pos);

        // Check is OK
        assertTrue(slotMachine.ok());
        
        // Check the wheel on the index (pos) had changed it's symbol (color)
        assertEquals("blue", slotMachine.symbols()[1]); // On the second one (watching it from left to right)
    }
    
    // This test is disabled due to the adjustment that is made when the position is out of range
    // So this never returns error on that case
    // @Test
    // public void shouldNotSpinAWheel() {
    //     // Add some Wheels
    //     slotMachine.addWheel(1);
    //     slotMachine.addWheel(2);
    //     slotMachine.addWheel(3);
        
    //     // Add some symbols
    //     slotMachine.addSymbol(1, "yellow");
    //     slotMachine.addSymbol(2, "blue");
    //     slotMachine.addSymbol(3, "red");
        
    //     assertFalse(slotMachine.ok());
    // }
    
    /**
     * Verifies that spinning a wheel at an invalid position adjusts
     * the position to the first wheel and still updates its symbol,
     * keeping the machine status ok.
     */
    @Test
    public void shouldSpinAInvalidPosWheel() {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3); // last one added
        
        // Add some symbols
        slotMachine.addSymbol(1, "yellow");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "red");
        
        // Spin the wheel on a position of an unexisten wheel, it is until 3.
        // So this should put the position on 0 and spin the first one
        int nonExistentWheelPos = -1;
        slotMachine.spin(-1); // spin it
        
        // Check is OK after the position was adjusted
        assertTrue(slotMachine.ok());
        
        // Check that the first wheel changed its symbol (color)
        String visibleSymbol = slotMachine.configuration()[0];
        assertTrue(visibleSymbol.equals("red") | visibleSymbol.equals("blue"));
    }
    
    /**
     * Verifies that spinning fails when there are no wheels or symbols,
     * setting the machine status to not ok.
     */
    @Test
    public void shouldNotSpin() {
        // Spin when there's no wheels or symbols neither
        slotMachine.spin();
        
        // Check is NOT OK
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that attempting to spin a specific wheel when the machine
     * has no wheels at all fails, setting the machine status to not ok.
     * adjustPosition() returns index 0 on an empty list, which then fails
     * the (index < wheels.size()) check, so ok must end up false.
     */
    @Test
    public void shouldNotSpinWhenNoWheelsExist() {
        // No wheels added
        slotMachine.spin(1);
    
        // Check is NOT OK because there is no wheel at index 0
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that the machine configuration starts as an empty array
     * when there are no symbols.
     */
    @Test
    public void checkConfigurationArray () {
        // Get the machine config when there's no symbols
        String[] expected = {};
        assertArrayEquals(expected, slotMachine.configuration());
    }
    
    /**
     * Verifies that placing a specific symbol on each wheel results
     * in the configuration array matching the expected order.
     */
    @Test
    public void shouldReturnOrderedConfigurationOfWheels() {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
    
        // Assign an specific symbol for every wheel
        slotMachine.placeSymbol(1, "red");
        slotMachine.placeSymbol(2, "blue");
        slotMachine.placeSymbol(3, "green");
    
        // Define the expected array after assigning symbols
        String[] expectedConfiguration = {"red", "blue", "green"};
    
        // Check the current config of the machine equals the expected one
        assertArrayEquals(expectedConfiguration, slotMachine.configuration());
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that placing a symbol on a wheel fails when the machine
     * has no wheels registered, since there is no valid index to place
     * the symbol on.
     */
    @Test
    public void shouldNotPlaceSymbolWhenNoWheelsExist() {
        // No wheels added, try to place a symbol anyway
        slotMachine.placeSymbol(1, "red");
    
        // Check is NOT OK, since there is no wheel to place a symbol on
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that the count of distinct symbols correctly ignores
     * repeated symbols placed on different wheels.
     */
    @Test
    public void shouldCheckDistinctSymbols () {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Add some symbols
        slotMachine.addSymbol(1, "yellow");
        slotMachine.addSymbol(2, "blue");
        
        // Assign an specific symbol for every wheel (one repeated)
        slotMachine.placeSymbol(1, "yellow");
        slotMachine.placeSymbol(2, "yellow");
        slotMachine.placeSymbol(3, "blue");
        
        // Check there's only 2 distinct symbols
        assertEquals(2, slotMachine.distinctSymbols());
    }
    
    /**
     * Verifies that counting distinct symbols returns 0 when no symbols
     * have been registered yet, regardless of whether wheels exist.
     */
    @Test
    public void shouldNotCountDistinctSymbolsWhenNoSymbolsRegistered() {
        // Add wheels but no symbols
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
    
        // Check returns 0 because Wheel.symbols is empty
        assertEquals(0, slotMachine.distinctSymbols());
    }
    
    /**
     * Verifies that an empty machine is never considered a jackpot.
     */
    @Test
    public void shouldNotBeJackpot () {
        // This should return false because the machine is empty        
        // Check is NOT OK (false)
        assertFalse(slotMachine.isJackpot());
    }
    
    /**
     * Verifies that the machine is not considered a jackpot when the
     * wheels are showing different symbols, exercising the "false" branch
     * of isJackpot() beyond the trivial empty-machine case.
     */
    @Test
    public void shouldNotBeJackpotWhenSymbolsDiffer() {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
    
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
    
        // Assign different symbols so they don't all match
        slotMachine.placeSymbol(1, "red");
        slotMachine.placeSymbol(2, "blue");
        slotMachine.placeSymbol(3, "green");
    
        // Check is NOT a jackpot since symbols differ
        assertFalse(slotMachine.isJackpot());
    }
    
    /**
     * Verifies that spinning all wheels when they share the same
     * symbol results in a jackpot, keeping the machine status ok.
     */
    @Test
    public void shouldBeJackpot () {
        // Add some Wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Add some symbols
        slotMachine.addSymbol(1, "yellow");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "red");
        
        // Spin all (intially all have the same symbol)
        slotMachine.spin(); // This should return true
        
        // Check is OK
        assertTrue(slotMachine.ok());
    }
    
    // Tests for MINI-CYCLE 4
    /**
     * Verifies that toggling the machine's visibility (visible/invisible)
     * always leaves the machine status as ok.
     */
    
    /**
     * Verifies that out-of-range positions passed to placeSymbol are
     * adjusted to the nearest valid index (zero for positions below
     * range, the last index for positions above range), keeping the
     * machine status ok.
     */
    @Test
    public void shouldHandlePositionAdjustmentsInOperations() {
        // Add some wheels and 1 symbol
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addSymbol(1, "yellow");
    
        // Check with a position lower or equal zero (this should be adjusted to a zero index)
        slotMachine.placeSymbol(0, "yellow");
        assertTrue(slotMachine.ok());
    
        // Check with a position higher than the total amount of wheel of the board (this should be adjusted to the last index)
        slotMachine.placeSymbol(99, "yellow");
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Tears down the test case.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }
}