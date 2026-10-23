import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Shared unit tests for SlotMachineContest, compatible across group
 * implementations since they verify invariants any correct solution
 * to the contest problem must satisfy, rather than implementation
 * details specific to one group's algorithm.
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineContestCTest
{
    // Group: CarreñoG-CastañoP

    /**
     * solve should never propose an action on a wheel that doesn't exist.
     */
    @Test
    public void ShouldOnlyUseExistingWheels() {
        int n = 8;
        for (int[] action : SlotMachineContest.solve(n)) {
            assertTrue(action[0] >= 1 && action[0] <= n);
        }
    }

    /**
     * An action that spins 0 steps moves nothing: it shouldn't take up a turn.
     */
    @Test
    public void ShouldNotIncludeZeroStepActions() {
        for (int[] action : SlotMachineContest.solve(8)) {
            assertNotEquals(0, action[1]);
        }
    }

    /**
     * The contest problem allows a maximum of 10000 actions, even with
     * the largest machine size the problem statement allows.
     */
    @Test
    public void ShouldNotExceedTheActionLimit() {
        assertTrue(SlotMachineContest.solve(50).length <= 10000);
    }

    // Group: CañonA-PaezP

    /**
     * Verifies that every returned action has the expected {wheel, steps}
     * structure (an array of length 2).
     */
    @Test
    public void accordingCaPpshouldReturnValidMovesStructure() {
        int n = 3;
        int[][] moves = SlotMachineContest.solve(n);

        assertNotNull(moves);
        for (int[] move : moves) {
            assertEquals(2, move.length);
        }
    }

    // Group: Murillo

    /**
     * What should it do? Ensure the returned action list is not null,
     * stays within the 10000-action limit, and never includes a
     * zero-step spin (a spin that doesn't change any wheel's state).
     * What shouldn't it do? Return a null list or include 0-step spins.
     */
    @Test
    public void accordingMurtestSolveDontHaceSpin0() {
        int n = 4;
        int[][] moves = SlotMachineContest.solve(n);

        assertNotNull(moves);
        assertTrue(moves.length <= 10000, "Exceeded the 10,000-action limit for n = " + n);

        for (int[] move : moves) {
            assertNotEquals(0, move[1]);
        }
    }

    // Group: Cuervo-Infante

    /**
     * Verifies that solving a machine with a single wheel returns an
     * empty action plan, since a lone wheel is trivially always a
     * jackpot (there is nothing else to match it against).
     */
    @Test
    public void accordingCcIcsolveShouldReturnAnEmptyPlanForOneWheel() {
        int[][] result = SlotMachineContest.solve(1);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}