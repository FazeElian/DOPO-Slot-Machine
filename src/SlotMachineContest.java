import java.util.ArrayList;
import java.util.List;

/**
 * Solves the ICPC 2025 World Finals "Slot Machine" contest problem
 * (Problem I, Baku): given a slot machine with n wheels, each cycling
 * through the same n distinct symbols, this class figures out how to
 * rotate the wheels so that all of them end up showing the same
 * symbol at the same time (a jackpot) — without ever being able to
 * see which symbol any wheel is actually showing.
 *
 * The only feedback available while solving is the number of distinct
 * symbols currently visible across all wheels, obtained through
 * {@link SlotMachine#distinctSymbols()}. All reasoning about which
 * wheel matches which symbol is done indirectly, by observing how
 * that count changes after each rotation.
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineContest {
    public static SlotMachine sm; // SlotMachine created for the problem
    private static String[] setSymbols;
    /**
     * Default constructor for the SlotMachineContest solver class.
     */
    public SlotMachineContest() {
    }

    /**
     * Solves the ICPC 2025 World Finals "Slot Machine" contest problem
     * (Problem I) using the three-phase strategy: (1) separate all wheels
     * so each shows a distinct symbol, (2) discover the exact relative
     * permutation between wheels by exploiting collisions detected only
     * through distinctSymbols(), and (3) align every wheel to the same
     * symbol using the permutation found. The colors themselves are never
     * read directly — only the count of distinct symbols currently shown.
     *
     * Worst-case action count: Phase 1 ~ n*(n-1), Phase 2 ~ 2*C(n,2),
     * Phase 3 ~ n-1 — comfortably under the 10000-action limit for n <= 50.
     *
     * The SlotMachine created here stays invisible for the entire process.
     *
     * @param n number of wheels and symbols
     * @return the list of actions {wheel, steps} applied, in order
     */
    public static int[][] solve(int n) {
            sm = new SlotMachine(n);
            setSymbols = sm.configuration();
            List<int[]> actions = new ArrayList<>();

        if (sm.distinctSymbols() == 1) {
            return actions.toArray(new int[0][]);
        }

        //PHASE 1: separate all wheels
        for (int wheel = 2; wheel <= n; wheel++) {
            int bestK = sm.distinctSymbols();
            int bestOffset = 0;
            int stepsTaken = 0;

            for (int offset = 1; offset < n; offset++) {
                sm.spin(wheel, 1);
                actions.add(new int[]{wheel, 1});
                stepsTaken++;

                int k = sm.distinctSymbols();
                if (k > bestK) {
                    bestK = k;
                    bestOffset = offset;
                }
                if (k == n) break; // ceiling reached, can't improve further
            }

            int remaining = bestOffset - stepsTaken;
            if (remaining != 0) {
                sm.spin(wheel, remaining);
                actions.add(new int[]{wheel, remaining});
            }
        }
        // At this point sm.distinctSymbols() == n

        // PHASE 2: discover the permutation
        int[] relativeOffset = new int[n + 1]; // relativeOffset[1] = 0
        relativeOffset[1] = 0;
        boolean[] identified = new boolean[n + 1];
        identified[1] = true;

        for (int i = 1; i <= n - 1; i++) {
            sm.spin(1, 1);
            actions.add(new int[]{1, 1});

            for (int candidate = 2; candidate <= n; candidate++) {
                if (identified[candidate]) continue;

                sm.spin(candidate, -1);
                actions.add(new int[]{candidate, -1});

                if (sm.distinctSymbols() == n) {
                    relativeOffset[candidate] = i;
                    identified[candidate] = true;
                    break;
                } else {
                    sm.spin(candidate, 1); // wrong candidate: revert
                    actions.add(new int[]{candidate, 1});
                }
            }
        }

        //PHASE 3: align everyone to wheel 1's original symbol
        sm.spin(1, 1); // completes the cycle back to wheel 1's original position
        actions.add(new int[]{1, 1});

        for (int wheel = 2; wheel <= n; wheel++) {
            // every other wheel has a net displacement of exactly -1 from phase 2
            int delta = 1 - relativeOffset[wheel];
            if (delta!=0){
            sm.spin(wheel, delta);
            actions.add(new int[]{wheel, delta});
            }
        }

        return actions.toArray(new int[0][]);
    }

    /** Simulate the solution to the problem with a given n from the matrix returned by the 
     * steps of the solve(n) method 
     * 
     * @param n number of wheels and symbols
     */
    public static void simulate(int n){
        int[][] actions = solve(n);
        sm.makeVisible();
        sm.spin(setSymbols);
        for (int i=0; i < actions.length; i++){
            sm.spin(actions[i][0],actions[i][1]);
        }
        sm.makeInvisible();
    }
    
}