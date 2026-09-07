

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The SlotMachine test class for cycle 2.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineC2Test
{
    private SlotMachine slotMachine;
    public final static int MAX_COLUMNS = 14;
    public final static int MAX_ROWS = 9;
    
    /**
     * Default constructor for test class SlotMachineC2Test
     */
    public SlotMachineC2Test()
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
    
    // MINI-CYCLE 1: lock() & unlock()
    /**
     * Verifies that attempting to lock a wheel that doesn't exist yet
     * fails, setting the machine status to not ok.
     */
    @Test
    public void shouldNotLockASpin() {
        // Add a wheel at the 1st pos
        slotMachine.addWheel(1);
        
        // Try to lock a wheel not registered yet
        slotMachine.lock(2);
        
        // Check the action wasn't succesful
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that locking an existing wheel succeeds, leaving the
     * machine status as ok.
     */
    @Test
    public void shouldLockASpin() {
        // Add a wheel at the 1st pos
        slotMachine.addWheel(1);
        
        // Try to lock the wheel that was just added
        slotMachine.lock(1);
        
        // Check the action was succesful
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that attempting to unlock a wheel that was never locked
     * fails, setting the machine status to not ok.
     */
    @Test
    public void shouldNotUnlockASpin() {
        // Add a wheel at the 1st pos
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Lock the wheel that was just added
        slotMachine.lock(1);
        
        // Try to unlock a wheel that wasn't locked
        slotMachine.unlock(2);
        
        // Check the action wasn't succesful
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that unlocking a previously locked wheel succeeds,
     * leaving the machine status as ok.
     */
    @Test
    public void shouldUnlockASpin() {
        // Add a wheel at the 1st pos
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Lock the wheel that was just added
        slotMachine.lock(1);
        
        // Try to unlock a wheel that was locked
        slotMachine.unlock(1);
        
        // Check the action wasn't succesful
        assertTrue(slotMachine.ok());
    }

    // MINI-CYCLE 2: swap()
    /**
     * Verifies that attempting to swap two wheels fails when one of
     * them is locked, setting the machine status to not ok.
     */
    @Test
    public void shouldNotSwap() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Lock the first one
        slotMachine.lock(1);
        
        // Try to swap the 1st and the 3rd
        slotMachine.swap(1, 3);
        
        // Check that the action wasn't succesful due to the wheel to spin is locked
        assertFalse(slotMachine.ok());
    }

    /**
     * Verifies that swapping two wheels succeeds once neither of them
     * is locked, leaving the machine status as ok.
     */
    @Test
    public void shouldSwap() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Lock the first one
        slotMachine.lock(1);
        
        // Unlock it
        slotMachine.unlock(1);
        
        // Try to swap the 1st and the 3rd
        slotMachine.swap(1, 3);
        
        // Check that the action was succesful due to the wheel to spin is locked
        assertTrue(slotMachine.ok());
    }
    
    // MINI-CYCLE 3
    /**
     * Verifies that a wheel can be spun successfully once it has been
     * locked and then unlocked, leaving the machine status as ok.
     */
    @Test
    public void shouldSpinAWheel() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        // Lock the first one
        slotMachine.lock(1);
        
        // Unlock it then
        slotMachine.unlock(1);
        
        // Spin the second wheel which is not locked
        slotMachine.spin(1);
        
        // Check that the action was succesful due to the wheel to spin is locked
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that attempting to spin a wheel that is currently locked
     * fails, setting the machine status to not ok.
     */
    @Test
    public void shouldNotSpinAWheel() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        // Lock the first one
        slotMachine.lock(1);
        
        // Spin the first wheel
        slotMachine.spin(1);
        
        // Check that the action wasn't succesful due to the wheel to spin is locked
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that setting a full symbol configuration via
     * spin(String[]) fails in two situations: when at least one wheel
     * is locked, and when the amount of symbols provided doesn't match
     * the number of wheels on the board.
     */
    @Test
    public void shouldNotSpinWheelsSettingSymbols() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Lock at least 1, in this case the 2nd one
        slotMachine.lock(2);
        
        // Add symbols
        String[] symbols = new String[] {"yellow", "green"};
        slotMachine.spin(symbols);
        
        // Check that the action can't be done because there's a wheel locked
        assertFalse(slotMachine.ok());
        
        // Unlock that wheel to test another situation
        slotMachine.unlock(2);
        
        // Add symbols
        String[] symbols2 = new String[] {"yellow", "aqua", "green"};
        slotMachine.spin(symbols);
        
        // Check that the action can't be done because the amount of elements on symbols2 exceeds the limit
        // Waits for 2 symbols because there's only 2 wheels but 3 symbols where sent as argument
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that setting a full symbol configuration via
     * spin(String[]) succeeds when no wheel is locked and the amount
     * of symbols matches the number of wheels, and that the resulting
     * configuration matches exactly what was requested.
     */
    @Test
    public void shouldSpinWheelsSettingSymbols() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Add symbols
        String[] symbols = new String[] {"blue", "red"};
        slotMachine.spin(symbols);
        
        // Check that the action can't be done because there's a wheel locked
        assertTrue(slotMachine.ok());
        assertArrayEquals(symbols, slotMachine.configuration());
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