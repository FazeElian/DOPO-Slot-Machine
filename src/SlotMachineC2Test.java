import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

/**
 * The SlotMachine test class for cycle 2.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.1
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
        int notRegisteredWheelPos = 2;
        slotMachine.lock(notRegisteredWheelPos);
        
        // Get the wheel that was locked
        Wheel w = slotMachine.getWheel(notRegisteredWheelPos);

        // Check is null because wasn't added
        assertNull(w);

        // Check the action wasn't succesfull
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies that locking an existing wheel succeeds, leaving the
     * machine status as ok.
     */
    @Test
    public void shouldLockASpin() {
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Try to lock the first wheel
        slotMachine.lock(2);
        
        // Get the wheel that was locked
        Wheel w = slotMachine.getWheel(2);
        
        // Check the action was succesfull & it's locked state was changed
        assertTrue(w.isLocked());
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
        Wheel w1 = slotMachine.getWheel(1);
        assertTrue(w1.isLocked());
        // Try to unlock a wheel that wasn't locked
        slotMachine.unlock(2);
        Wheel w2 = slotMachine.getWheel(2);
        assertFalse(w2.isLocked());
        // Check the action wasn't succesfull
        assertFalse(slotMachine.ok());
    }
    
    /**
     * Verifies unlock() in two situations: unlocking a previously
     * locked wheel succeeds and leaves the machine status as ok;
     * afterward, attempting to unlock a different wheel that was
     * never locked fails, setting the machine status to not ok.
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
        assertFalse(slotMachine.getWheel(1).isLocked());
        
        // Check the action was succesfull
        assertTrue(slotMachine.ok());
        
        // Try to unlock a wheel that wasn't locked
        slotMachine.unlock(2);
        assertFalse(slotMachine.getWheel(2).isLocked());
        
        // Check the action wasn't succesfull
        assertFalse(slotMachine.ok());
    }

    // MINI-CYCLE 2: swap()
    /**
     * Verifies that attempting to swap a wheel with itself (duplicate
     * position) fails, setting the machine status to not ok. Note:
     * despite its name, this test does not involve a locked wheel —
     * it only exercises the duplicate-index validation in swap().
     */
    @Test
    public void shouldNotSwap() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        Wheel w = slotMachine.getWheel(2);
        // Try to swap two wheels at the same position
        slotMachine.swap(2, 2);
        
        Wheel wResult = slotMachine.getWheel(2);
        assertEquals(w,wResult);
        // Check that the action wasn't succesfull due to a index duplication on the wheels to swap
        assertFalse(slotMachine.ok());
    }

    /**
     * Verifies swap() together with the locking rules on spin(): first
     * confirms that spinning a locked wheel fails; then, once the
     * wheel is unlocked and spun, verifies that swapping two unlocked
     * wheels succeeds, leaving the machine status as ok and producing
     * the expected configuration order.
     */
    @Test
    public void shouldSwap() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);
        
        // Lock the first one
        slotMachine.lock(1);
        
        // Try to spin the first one so it's symbols is blue and later validate with symbols()
        // With this the other wheels have a red color shape
        slotMachine.spin(1);
        
        // Validate can't be spinned
        assertFalse(slotMachine.ok());
        
        // Unlock it
        slotMachine.unlock(1);
        
        // Spin the first one once it's unlocked
        slotMachine.spin(1);
        
        // Try to swap the 1st and the 3rd
        slotMachine.swap(1, 3);
        
        // Check that the action was succesfull due to the wheel to spin is locked
        assertTrue(slotMachine.ok());
        
        // Check the symbols order after the swap (1 & 3) blue, red, red -> red, red, blue
        String[] expected = new String[]{"red", "red", "blue"};
        assertArrayEquals(expected, slotMachine.configuration());
    }
    
    // MINI-CYCLE 3
    /**
     * Verifies spin(int, int) in two situations: spinning an unlocked
     * wheel by steps succeeds; and, after locking and then unlocking a
     * different wheel, spinning that wheel by steps also succeeds,
     * leaving the machine status as ok. In this scenario spinning
     * wheel 1 by 3 steps results in a jackpot against wheel 2's symbol.
     */
    @Test
    public void shouldSpinByStepsAWheel() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Spin the 2nd one (by default all are unlocked) 1 time
        slotMachine.spin(2, 1);
        
        // Check is ok
        assertTrue(slotMachine.ok());

        // Lock the first one
        slotMachine.lock(1);
        
        // Unlock it then
        slotMachine.unlock(1);
        
        // Spin the first wheel, which is now unlocked
        slotMachine.spin(1, 3); // Spin it 3 times, so it becomes blue as the 1st one
        
        // Now both of the wheels have a blue shape, so we can also check that was spinned correctly using steps by checking if was a jackpoo (it should)
        assertTrue(slotMachine.isJackpot());
        
        // Check that the action was succesful due to the wheel to spin is locked
        assertTrue(slotMachine.ok());
    }
    
    /**
     * Verifies that attempting to spin a wheel that is currently locked
     * fails, setting the machine status to not ok.
     */
    @Test
    public void shouldNotSpinByStepsAWheel() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        // Lock the first one
        slotMachine.lock(1);
        String[] symbols2 = slotMachine.configuration();

        // Spin the first wheel 3 times
        slotMachine.spin(1, 3);

        // Check that the action wasn't succesful due to the wheel to spin is locked
        assertFalse(slotMachine.ok());
        assertTrue(Arrays.equals(symbols2, slotMachine.configuration()));
        
    }
    
    /**
     * Verifies that setting a full symbol configuration via spin(String[])
     * fails in two situations: first, when at least one wheel is locked
     * (even though the requested colors aren't registered symbols, which
     * on its own would already prevent any wheel from updating); second,
     * when the amount of symbols exceeds the number of wheels, using
     * "symbols2" (3 elements for only 2 wheels).
     */
    @Test
    public void shouldNotSpinWheelsSettingSymbols() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
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
        slotMachine.spin(symbols2);
        
        // Check that the action can't be done because the amount of elements on symbols2 exceeds the limit
        // Waits for 2 symbols because there's only 2 wheels but 3 symbols where sent as argument
        assertFalse(slotMachine.ok());
        
        // Check that the symbols2 setted by the user should have the same wheels (symbols) configuration as configuration() array
        // The Arrays.equals() is used instead of .equals() because last one compares references ,not contents, so the assertion is always true
        // regardless of the actual configuration
        assertFalse(Arrays.equals(symbols2, slotMachine.configuration()));
    }
    
    /**
     * Verifies that setting a full symbol configuration via
     * spin(String[]) succeeds when no wheel is locked and the amount
     * of symbols matches the number of wheels on the board, and that
     * the resulting configuration matches exactly what was requested.
     */
    @Test
    public void shouldSpinWheelsSettingSymbols() {
        // Add some symbols
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        
        // Add some wheels
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        
        // Lock & unlock the 2nd wheel so the full action can be executed
        slotMachine.lock(2);
        slotMachine.unlock(2);
        
        // Add symbols
        String[] symbols = new String[] {"blue", "red"};
        slotMachine.spin(symbols);
        
        // Check that the action can be done because there's no wheels locked & the config equals the symbols that want to be setted
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