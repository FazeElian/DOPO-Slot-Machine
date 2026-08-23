import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class SlotMachineTest.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.1
 */
public class SlotMachineTest
{
    private SlotMachine slotMachine;
    public final static int MAX_COLUMNS = 14;
    public final static int MAX_ROWS = 9;
    
    /**
     * Default constructor for test class SlotMachineTest
     */
    public SlotMachineTest()
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
         // initialize machine to test
         slotMachine = new SlotMachine();
         
         // Remove all elements from symbols list
         Wheel.symbols.clear();
    }
    
    
    // Tests for MINI-CYCLE 1: slotMachine(), addWheel(), delWheel()
    @Test
    public void shouldAddWheelInAValidPos() {
        slotMachine.addWheel(2);
        assertTrue(slotMachine.ok());
    }
    
    @Test
    public void shouldAddWheelInAInvalidPos() {
        slotMachine.addWheel(-1);
        assertTrue(slotMachine.ok());
    }
    
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
    public void shouldNotDelWheel() {
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void shouldDelWheel() {
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.delWheel(1);
        assertTrue(slotMachine.ok());
    }
    
    // Tests for MINI-CYCLE 2: addSymbol(), delSymbol(), symbols()
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
    
    @Test
    public void shouldNotDeleteIfNoSymbols () {
        // Check that the status changed after checking that symbols is a []
        slotMachine.delSymbol("red"); // This wheel doesn't exists
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void checkSymbolIsNotDuplicated () {
        // Add symbol
        slotMachine.addSymbol(1, "yellow");
        
        // Add another one with the same value
        slotMachine.addSymbol(1, "yellow");
        assertFalse(slotMachine.ok());
    }
    
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
    
    @Test
    public void shouldNotDeleteASingleSymbol() {
        // Add a single symbol that SHOULD'NT be deleted
        slotMachine.addSymbol(1, "red");
        
        // Try to delete it
        slotMachine.delSymbol("red");
        
        // Check is NOT OK due to a failed action (delete a symbol where there's only on symbols[])
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void shouldFailWhenDeletingNonexistentSymbol () {
        // This should return false because no symbols where added before delete this one
        slotMachine.delSymbol("red");
        
        // Check is false
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void checkReturnsASymbolsList () {
        // Check returns a [] because symbols has a [] inital value
        String[] expected = {};
        assertArrayEquals(expected, slotMachine.symbols());
    }
    
    // Tests for MINI-CYCLE 3
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
    
    @Test
    public void shouldNotSpinAnyWheel() {
        // Spin when there's no wheels or symbols neither
        slotMachine.spin();
        
        // Check is NOT OK
        assertFalse(slotMachine.ok());
    }
    
    @Test
    public void checkConfigurationArray () {
        // Get the machine config when there's no symbols
        String[] expected = {};
        assertArrayEquals(expected, slotMachine.configuration());
    }
    
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
    
    @Test
    public void checkDistinctSymbols () {
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
    
    @Test
    public void shouldNotBeJackpot () {
        // This should return false because the machine is empty        
        // Check is NOT OK (false)
        assertFalse(slotMachine.isJackpot());
    }
    
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
    @Test
    public void shouldSetOkToTrueWhenChangingVisibility() {
        // Check if is true when is visible
        slotMachine.makeVisible();
        assertTrue(slotMachine.ok());
    
        // Check is false when is NOT visible (Invisible)
        slotMachine.makeInvisible();
        assertTrue(slotMachine.ok());
    }
    
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
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }
}