package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Solves the ICPC 2025 World Finals problem I "Slot Machine": rotate n
 * wheels of n symbols until all show the same one (a jackpot), seeing only
 * {@link SlotMachine#distinctSymbols()}, never the symbols themselves.
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.1
 */
public class SlotMachineContest {
    /** SlotMachine created for the problem. */
    public static SlotMachine sm;
    /** Initial configuration of sm, replayed by {@link #simulate(int)}. */
    private static String[] setSymbols;
    /**
     * Default constructor for the SlotMachineContest solver class.
     */
    public SlotMachineContest() {
        // Nothing to initialize: every method of the solver is static
    }

    /**
     * Solves the problem in three phases, explained in the body: separate
     * the wheels, discover their relative permutation and align them all.
     * The machine stays invisible. Worst case ~2n(n-1)+n actions, under the
     * 10000-action limit for n &lt;= 50.
     * @param n number of wheels and symbols
     * @return the list of actions {wheel, steps} applied, in order
     */
    public static int[][] solve(final int n) {
        sm = new SlotMachine(n);
        setSymbols = sm.configuration();
        final List<int[]> actions = new ArrayList<>();

        if (sm.distinctSymbols() == 1) {
            return actions.toArray(new int[0][]);
        }

        // PHASE 1: separate all wheels so each shows a distinct symbol
        // (worst case ~ n*(n-1) actions)
        for (int wheel = 2; wheel <= n; wheel++) {
            int bestK = sm.distinctSymbols();
            int bestOffset = 0;
            int stepsTaken = 0;

            for (int offset = 1; offset < n; offset++) {
                sm.spin(wheel, 1);
                actions.add(new int[]{wheel, 1});
                stepsTaken++;

                final int k = sm.distinctSymbols();
                if (k > bestK) {
                    bestK = k;
                    bestOffset = offset;
                }
                if (k == n) { // ceiling reached, can't improve further
                    break;
                }
            }

            final int remaining = bestOffset - stepsTaken;
            if (remaining != 0) {
                sm.spin(wheel, remaining);
                actions.add(new int[]{wheel, remaining});
            }
        }
        // At this point sm.distinctSymbols() == n

        // PHASE 2: discover the exact relative permutation between wheels,
        // detecting collisions only through distinctSymbols()
        // (worst case ~ 2*C(n,2) actions)
        final int[] relativeOffset = new int[n + 1]; // relativeOffset[1] = 0
        relativeOffset[1] = 0;
        final boolean[] identified = new boolean[n + 1];
        identified[1] = true;

        for (int i = 1; i <= n - 1; i++) {
            sm.spin(1, 1);
            actions.add(new int[]{1, 1});

            for (int candidate = 2; candidate <= n; candidate++) {
                if (identified[candidate]) {
                    continue;
                }

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

        // PHASE 3: align everyone to wheel 1's original symbol using the
        // permutation found; the colors are never read directly
        // (~ n-1 actions)
        sm.spin(1, 1); // completes the cycle back to wheel 1's original position
        actions.add(new int[]{1, 1});

        for (int wheel = 2; wheel <= n; wheel++) {
            // every other wheel has a net displacement of exactly -1 from phase 2
            final int delta = 1 - relativeOffset[wheel];
            if (delta!=0){
            sm.spin(wheel, delta);
            actions.add(new int[]{wheel, delta});
            }
        }

        return actions.toArray(new int[0][]);
    }

    /**
     * Shows the solution step by step: replays on a visible machine the
     * actions returned by {@link #solve(int)}.
     *
     * @param n number of wheels and symbols
     */
    public static void simulate(final int n){
        final int[][] actions = solve(n);
        sm.makeVisible();
        sm.spin(setSymbols);
        for (int i=0; i < actions.length; i++){
            sm.spin(actions[i][0],actions[i][1]);
        }
        sm.makeInvisible();
    }
    
}