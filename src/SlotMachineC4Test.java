import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The SlotMachine test class for cycle 4: the different types of wheels
 * (normal, lefty, rebel, lazy) and of symbols (normal, ephemeral, shy,
 * dizzy). The machine is never made visible, so no window is opened.
 * Only the public methods the project already had are used, so what is
 * verified about the special symbols is what can be observed from
 * outside: their type and that they keep working as symbols.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineC4Test
{
    private SlotMachine slotMachine;

    /**
     * Default constructor for test class SlotMachineC4Test
     */
    public SlotMachineC4Test()
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

    // REQUIREMENT 16: handle different types of wheels and symbols

    /**
     * Verifies that addWheel(type, pos) creates a wheel of the class that
     * matches every offered type, and that null or "" create a normal one.
     */
    @Test
    public void shouldAddEveryTypeOfWheel() {
        slotMachine.addSymbol(1, "red");

        slotMachine.addWheel("normal", 1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.addWheel("rebel", 3);
        slotMachine.addWheel("lazy", 4);
        slotMachine.addWheel("", 5);
        slotMachine.addWheel(null, 6);

        assertTrue(slotMachine.ok());
        assertEquals(6, slotMachine.configuration().length);
        assertEquals(Wheel.class, slotMachine.getWheel(1).getClass());
        assertTrue(slotMachine.getWheel(2) instanceof LeftyWheel);
        assertTrue(slotMachine.getWheel(3) instanceof RebelWheel);
        assertTrue(slotMachine.getWheel(4) instanceof LazyWheel);
        assertEquals(Wheel.class, slotMachine.getWheel(5).getClass());
        assertEquals(Wheel.class, slotMachine.getWheel(6).getClass());
    }

    /**
     * Verifies that the type of a wheel is not case sensitive and ignores
     * surrounding spaces.
     */
    @Test
    public void shouldAddWheelIgnoringCaseAndSpacesOfTheType() {
        slotMachine.addWheel("  ReBeL ", 1);

        assertTrue(slotMachine.ok());
        assertTrue(slotMachine.getWheel(1) instanceof RebelWheel);
    }

    /**
     * Verifies that an unknown type of wheel adds nothing and sets the
     * machine status to not ok.
     */
    @Test
    public void shouldNotAddWheelOfUnknownType() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);

        slotMachine.addWheel("crazy", 2);

        assertFalse(slotMachine.ok());
        assertEquals(1, slotMachine.configuration().length);
    }

    /**
     * Verifies that addSymbol(type, pos, color) registers in the catalog
     * a symbol of the class that matches every offered type.
     */
    @Test
    public void shouldAddEveryTypeOfSymbol() {
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        slotMachine.addSymbol("dizzy", 4, "yellow");
        slotMachine.addSymbol("", 5, "aqua");
        slotMachine.addSymbol(null, 6, "pink");

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green", "yellow", "aqua", "pink"},
            slotMachine.symbols());
        assertEquals(Symbol.class, Symbol.symbols.get(0).getClass());
        assertTrue(Symbol.symbols.get(1) instanceof EphemeralSymbol);
        assertTrue(Symbol.symbols.get(2) instanceof ShySymbol);
        assertTrue(Symbol.symbols.get(3) instanceof DizzySymbol);
        assertEquals(Symbol.class, Symbol.symbols.get(4).getClass());
        assertEquals(Symbol.class, Symbol.symbols.get(5).getClass());
    }

    /**
     * Verifies that an unknown type of symbol adds nothing and sets the
     * machine status to not ok.
     */
    @Test
    public void shouldNotAddSymbolOfUnknownType() {
        slotMachine.addSymbol(1, "red");

        slotMachine.addSymbol("ghost", 2, "blue");

        assertFalse(slotMachine.ok());
        assertEquals(1, slotMachine.symbols().length);
    }

    /**
     * Verifies that a color can't be registered twice, even if the second
     * time it is given with a different type.
     */
    @Test
    public void shouldNotAddSameColorWithAnotherType() {
        slotMachine.addSymbol(1, "red");

        slotMachine.addSymbol("shy", 2, "red");

        assertFalse(slotMachine.ok());
        assertEquals(1, slotMachine.symbols().length);
        assertEquals(Symbol.class, Symbol.symbols.get(0).getClass());
    }

    /**
     * Verifies that a symbol of a special type reaches every wheel,
     * whether the wheel was created before or after the symbol.
     */
    @Test
    public void shouldGiveSpecialSymbolsToEveryWheel() {
        slotMachine.addWheel(1);                    // created before the symbol
        slotMachine.addSymbol("ephemeral", 1, "blue");
        slotMachine.addWheel(2);                    // created after the symbol

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"blue", "blue"}, slotMachine.configuration());
    }

    // REQUIREMENT 17: lefty and rebel wheels

    /**
     * Verifies that a lefty wheel with a wheel on its left copies the
     * symbol that wheel is showing when it is spun.
     */
    @Test
    public void leftyShouldCopyItsLeftNeighborWhenSpun() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.placeSymbol(1, "green");

        slotMachine.spin(2);

        assertTrue(slotMachine.ok());
        // A normal wheel would have advanced from red to blue
        assertEquals("green", slotMachine.configuration()[1]);
    }

    /**
     * Verifies that a lefty wheel also copies its neighbor when it is
     * spun by steps, no matter the amount or the direction of the steps.
     */
    @Test
    public void leftyShouldCopyItsLeftNeighborWhenSpunBySteps() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.placeSymbol(1, "blue");

        slotMachine.spin(2, -2);

        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[1]);
    }

    /**
     * Verifies that a lefty wheel without a wheel on its left (it is the
     * first one) spins like a normal wheel.
     */
    @Test
    public void leftyShouldSpinNormallyWithoutLeftNeighbor() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel("lefty", 1);

        slotMachine.spin(1);

        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that when all the wheels are spun, a lefty wheel copies
     * the symbol its neighbor has after that neighbor's own spin.
     */
    @Test
    public void leftyShouldCopyItsNeighborWhenAllWheelsSpin() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.placeSymbol(2, "green");

        slotMachine.spin();

        // Wheel 1 advances red -> blue and the lefty copies it
        assertArrayEquals(new String[]{"blue", "blue"}, slotMachine.configuration());
        assertTrue(slotMachine.isJackpot());
    }

    /**
     * Verifies that a lefty wheel follows the wheel that is on its left
     * at the moment of the spin, even after the wheels were swapped.
     */
    @Test
    public void leftyShouldCopyItsNewNeighborAfterSwap() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel("lefty", 3);
        slotMachine.placeSymbol(1, "green");
        slotMachine.placeSymbol(2, "blue");

        slotMachine.swap(1, 2); // now: blue, green, lefty
        slotMachine.spin(3);

        assertEquals("green", slotMachine.configuration()[2]);
    }

    /**
     * Verifies that a rebel wheel doesn't let itself be locked: the
     * operation fails and the wheel stays unlocked.
     */
    @Test
    public void rebelShouldNotBeLocked() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel("rebel", 1);

        slotMachine.lock(1);

        assertFalse(slotMachine.ok());
        assertFalse(slotMachine.getWheel(1).isLocked());
    }

    /**
     * Verifies that a rebel wheel doesn't let itself be swapped, whether
     * it is given as the first or as the second wheel of the swap.
     */
    @Test
    public void rebelShouldNotBeSwapped() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel("rebel", 1);
        slotMachine.addWheel(2);
        slotMachine.placeSymbol(2, "blue");
        Wheel rebel = slotMachine.getWheel(1);

        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());

        slotMachine.swap(2, 1);
        assertFalse(slotMachine.ok());

        assertSame(rebel, slotMachine.getWheel(1));
        assertArrayEquals(new String[]{"red", "blue"}, slotMachine.configuration());
    }

    /**
     * Verifies that a rebel wheel doesn't let itself be removed.
     */
    @Test
    public void rebelShouldNotBeRemoved() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);
        slotMachine.addWheel("rebel", 2);

        slotMachine.delWheel(2);

        assertFalse(slotMachine.ok());
        assertEquals(2, slotMachine.configuration().length);
        assertTrue(slotMachine.getWheel(2) instanceof RebelWheel);
    }

    /**
     * Verifies that a rebel wheel can still be spun and have a symbol
     * placed, like a normal wheel.
     */
    @Test
    public void rebelShouldStillSpinAndPlaceSymbols() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel("rebel", 1);

        slotMachine.spin(1);
        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);

        slotMachine.placeSymbol(1, "green");
        assertTrue(slotMachine.ok());
        assertEquals("green", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that the restrictions of a rebel wheel don't affect the
     * normal wheels next to it.
     */
    @Test
    public void normalWheelsNextToARebelShouldStillBeLockedSwappedAndRemoved() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addWheel(1);
        slotMachine.addWheel("rebel", 2);
        slotMachine.addWheel(3);

        slotMachine.lock(1);
        assertTrue(slotMachine.ok());
        slotMachine.unlock(1);

        slotMachine.swap(1, 3);
        assertTrue(slotMachine.ok());

        slotMachine.delWheel(3);
        assertTrue(slotMachine.ok());
        assertEquals(2, slotMachine.configuration().length);
    }

    // REQUIREMENT 18: ephemeral and shy symbols

    /**
     * Verifies that an ephemeral symbol can shrink as many times as
     * needed without failing: it stops at a point and never reaches a
     * size of zero, which the figure doesn't accept.
     */
    @Test
    public void ephemeralShouldNeverFailNoMatterHowManyTimesItShrinks() {
        Symbol ephemeral = new EphemeralSymbol("blue");

        for (int spin = 1; spin <= 30; spin++) {
            ephemeral.spun();
        }

        assertEquals("blue", ephemeral.getName());
    }

    /**
     * Verifies that a wheel whose only symbol is ephemeral can be spun
     * well past the moment the symbol becomes a point, and that the
     * symbol is still the one the wheel reports.
     */
    @Test
    public void ephemeralShouldStillBeReportedAfterBecomingAPoint() {
        slotMachine.addSymbol("ephemeral", 1, "blue");
        slotMachine.addWheel(1);

        slotMachine.spin(1, 5);     // already a point
        assertTrue(slotMachine.ok());
        slotMachine.spin(1, 20);    // stays as a point

        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that an ephemeral symbol keeps its place in the cycle of
     * the wheel while it shrinks: the wheel goes through its symbols in
     * the same order as always.
     */
    @Test
    public void ephemeralShouldKeepItsPlaceInTheWheelWhileItShrinks() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);

        String[] expected = {"blue", "green", "red"};
        for (int spin = 0; spin < 12; spin++) {
            slotMachine.spin(1);

            assertTrue(slotMachine.ok());
            assertEquals(expected[spin % 3], slotMachine.configuration()[0]);
        }
    }

    /**
     * Verifies that an ephemeral symbol that has become a point still
     * counts for the distinct symbols and for the jackpot.
     */
    @Test
    public void shrunkEphemeralShouldStillCountForJackpot() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.spin(1, 10);    // wheel 1 ends on red, its blue is a point
        slotMachine.spin(2, 10);

        slotMachine.spin(new String[]{"blue", "blue"});

        assertTrue(slotMachine.ok());
        assertEquals(1, slotMachine.distinctSymbols());
        assertTrue(slotMachine.isJackpot());
    }

    /**
     * Verifies that an ephemeral symbol doesn't make a spin on a locked
     * wheel succeed: the spin is still rejected.
     */
    @Test
    public void ephemeralShouldNotChangeTheRulesOfALockedWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addWheel(1);
        slotMachine.lock(1);

        slotMachine.spin(1);

        assertFalse(slotMachine.ok());
        assertEquals("red", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that a shy symbol can be selected over and over without
     * failing, whether or not its figure is on screen.
     */
    @Test
    public void shyShouldNeverFailNoMatterHowManyTimesItIsSelected() {
        Symbol shy = new ShySymbol("blue");

        for (int time = 1; time <= 10; time++) {
            shy.selected();
        }

        assertEquals("blue", shy.getName());
    }

    /**
     * Verifies that every time the wheel stops at a shy symbol, hidden
     * or not, the wheel reports it as its current symbol.
     */
    @Test
    public void shyShouldBeReportedEveryTimeItIsSelectedBySpinning() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("shy", 2, "blue");
        slotMachine.addWheel(1);

        for (int time = 1; time <= 4; time++) {
            slotMachine.spin(1);    // blue: hidden on odd times, shown on even ones
            assertTrue(slotMachine.ok());
            assertEquals("blue", slotMachine.configuration()[0]);

            slotMachine.spin(1);    // red
            assertEquals("red", slotMachine.configuration()[0]);
        }
    }

    /**
     * Verifies that a shy symbol can also be selected with placeSymbol
     * as many times as wanted.
     */
    @Test
    public void shyShouldBeReportedEveryTimeItIsSelectedByPlacingIt() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("shy", 2, "blue");
        slotMachine.addWheel(1);

        for (int time = 1; time <= 3; time++) {
            slotMachine.placeSymbol(1, "blue");
            assertTrue(slotMachine.ok());
            assertEquals("blue", slotMachine.configuration()[0]);

            slotMachine.placeSymbol(1, "red");
        }
    }

    /**
     * Verifies that hiding is only visual: a shy symbol selected for the
     * first time (so it is hidden) is still the one its wheel reports,
     * and it counts for the distinct symbols and for the jackpot.
     */
    @Test
    public void hiddenShyShouldStillCountForConfigurationAndJackpot() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("shy", 2, "blue");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        slotMachine.spin(new String[]{"blue", "blue"});

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"blue", "blue"}, slotMachine.configuration());
        assertEquals(1, slotMachine.distinctSymbols());
        assertTrue(slotMachine.isJackpot());
    }

    // REQUIREMENT 19: new types proposed (lazy wheel and dizzy symbol)

    /**
     * Verifies that a lazy wheel only answers to every other spin: it
     * advances with the first one, ignores the second one, advances with
     * the third one, and so on. An ignored spin is not an error.
     */
    @Test
    public void lazyShouldOnlyAdvanceEveryOtherSpin() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel("lazy", 1);

        slotMachine.spin(1);
        assertEquals("blue", slotMachine.configuration()[0]);

        slotMachine.spin(1);
        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);

        slotMachine.spin(1);
        assertEquals("green", slotMachine.configuration()[0]);

        slotMachine.spin(1);
        assertEquals("green", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that a lazy wheel spun by steps only advances with the
     * odd steps: with 3 steps it moves 2 positions.
     */
    @Test
    public void lazyShouldSkipHalfOfTheStepsWhenSpunBySteps() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel("lazy", 1);

        slotMachine.spin(1, 3);

        assertTrue(slotMachine.ok());
        assertEquals("green", slotMachine.configuration()[0]);
    }

    /**
     * Verifies that a lazy wheel doesn't refuse a placeSymbol: it is only
     * lazy with the spins.
     */
    @Test
    public void lazyShouldAlwaysAcceptAPlacedSymbol() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel("lazy", 1);

        slotMachine.placeSymbol(1, "blue");
        assertEquals("blue", slotMachine.configuration()[0]);
        slotMachine.placeSymbol(1, "red");
        assertEquals("red", slotMachine.configuration()[0]);
        assertTrue(slotMachine.ok());
    }

    /**
     * Verifies that a dizzy symbol can flip over and over without
     * failing.
     */
    @Test
    public void dizzyShouldNeverFailNoMatterHowManyTimesItFlips() {
        Symbol dizzy = new DizzySymbol("yellow");

        for (int spin = 1; spin <= 10; spin++) {
            dizzy.spun();
        }

        assertEquals("yellow", dizzy.getName());
    }

    /**
     * Verifies that a dizzy symbol keeps its place in the cycle of the
     * wheel while it flips on every spin.
     */
    @Test
    public void dizzyShouldKeepItsPlaceInTheWheelWhileItFlips() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("dizzy", 2, "yellow");
        slotMachine.addWheel(1);

        String[] expected = {"yellow", "red"};
        for (int spin = 0; spin < 6; spin++) {
            slotMachine.spin(1);

            assertTrue(slotMachine.ok());
            assertEquals(expected[spin % 2], slotMachine.configuration()[0]);
        }
    }

    /**
     * Verifies that a dizzy symbol, flipped or not, still counts for the
     * jackpot.
     */
    @Test
    public void dizzyShouldStillCountForJackpot() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("dizzy", 2, "yellow");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        slotMachine.spin(1);    // wheel 1: yellow, flipped once
        slotMachine.placeSymbol(2, "yellow");   // wheel 2: yellow, not flipped

        assertTrue(slotMachine.isJackpot());
    }

    // Wheels and symbols of different types working together

    /**
     * Verifies that a wheel with one symbol of every type goes through
     * all of them in order, like a wheel of normal symbols.
     */
    @Test
    public void wheelShouldCycleThroughSymbolsOfEveryType() {
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        slotMachine.addSymbol("dizzy", 4, "yellow");
        slotMachine.addWheel(1);

        String[] expected = {"blue", "green", "yellow", "red"};
        for (int spin = 0; spin < 8; spin++) {
            slotMachine.spin(1);

            assertTrue(slotMachine.ok());
            assertEquals(expected[spin % 4], slotMachine.configuration()[0]);
        }
    }

    /**
     * Verifies that the special wheels work with special symbols: a
     * lazy wheel still skips every other spin and a lefty wheel still
     * copies its neighbor, even when the symbol is shy.
     */
    @Test
    public void specialWheelsShouldWorkWithSpecialSymbols() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("shy", 2, "blue");
        slotMachine.addSymbol("ephemeral", 3, "green");
        slotMachine.addWheel("lazy", 1);
        slotMachine.addWheel("lefty", 2);

        slotMachine.spin(1);    // lazy awake: red -> blue
        slotMachine.spin(1);    // lazy asleep: stays on blue
        slotMachine.spin(2);    // lefty copies blue

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"blue", "blue"}, slotMachine.configuration());
        assertTrue(slotMachine.isJackpot());
    }

    /**
     * Verifies that a symbol of a special type can be deleted like any
     * other, from the catalog and from the wheels.
     */
    @Test
    public void shouldDeleteASymbolOfASpecialType() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol("shy", 2, "blue");
        slotMachine.addWheel(1);
        slotMachine.placeSymbol(1, "blue");

        slotMachine.delSymbol("blue");

        assertTrue(slotMachine.ok());
        assertArrayEquals(new String[]{"red"}, slotMachine.symbols());
        assertEquals("red", slotMachine.configuration()[0]);
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