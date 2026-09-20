import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for SlotMachineContest: verifies that solve(n) produces a
 * valid action sequence reaching jackpot within the action limit, and
 * that simulate(n) makes the resolved machine visible.
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */

public class SlotMachineContestTest
{
    /**
     * Default constructor for test class SlotMachineContestTest
     */
    public SlotMachineContestTest()
    {
    }

    /**
     * Sets up the test case.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() {
        Wheel.symbols.clear();
    }

    /**
     * Verifies that solve(n) reaches jackpot for a small n (3),
     * the lower bound of the contest's valid range.
     */
    @Test
    public void shouldReachJackpotForSmallN() {
        SlotMachineContest.solve(3);
        assertTrue(SlotMachineContest.sm.isJackpot());
    }

    /**
     * Verifies that solve(n) reaches jackpot for a mid-range n (10),
     * a typical case away from either edge of the valid range.
     */
    @Test
    public void shouldReachJackpotForMidN() {
        SlotMachineContest.solve(10);
        assertTrue(SlotMachineContest.sm.isJackpot());
    }

    /**
     * Verifies that solve(n) reaches jackpot for n = 50, the maximum
     * size allowed by the contest and the hardest case to solve.
     */
    @Test
    public void shouldReachJackpotForMaxN() {
        SlotMachineContest.solve(50);
        assertTrue(SlotMachineContest.sm.isJackpot());
    }

    /**
     * Verifies that the number of actions used to solve n = 50 never
     * exceeds the contest's hard limit of 10000 actions.
     */
    @Test
    public void shouldStayUnderActionLimit() {
        int[][] actions = SlotMachineContest.solve(50);
        assertTrue(actions.length <= 10000,
            "Used " + actions.length + " actions, exceeds the 10000 limit");
    }

    /**
     * Verifies that every action returned by solve(n) references a
     * wheel position within the valid 1-based range [1, n].
     */
    @Test
    public void shouldOnlyReferenceValidWheelPositions() {
        int n = 20;
        int[][] actions = SlotMachineContest.solve(n);
        for (int[] action : actions) {
            int wheel = action[0];
            assertTrue(wheel >= 1 && wheel <= n,
                "Invalid wheel index: " + wheel);
        }
    }

    /**
     * Verifies that solve(n) reliably reaches jackpot across many runs,
     * since SlotMachine(n) randomizes its starting configuration each time.
     */
    @Test
    public void shouldReachJackpotAcrossMultipleRandomRuns() {
        for (int i = 0; i < 20; i++) {
            SlotMachineContest.solve(15);
            assertTrue(SlotMachineContest.sm.isJackpot(),
                "Failed to reach jackpot on run " + i);
            Wheel.symbols.clear();
        }
    }

    /**
     * Verifies that calling solve(n) with a different n creates a fresh
     * SlotMachine sized for that n, instead of reusing the previous one.
     */
    @Test
    public void shouldCreateNewMachineWhenNChanges() {
        SlotMachineContest.solve(5);
        SlotMachine first = SlotMachineContest.sm;
        
        Wheel.symbols.clear();
        SlotMachineContest.solve(8);
        SlotMachine second = SlotMachineContest.sm;

        assertNotSame(first, second);
        assertEquals(8, second.configuration().length);
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