package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Shared tests of cycle 4 written by other groups. Only the ones that work
 * with this project were kept, adapted to JUnit 5. The machine is never
 * made visible, so no window is opened.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineCC4Test
{
    private SlotMachine machine;

    /**
     * Default constructor for test class SlotMachineCC4Test
     */
    public SlotMachineCC4Test()
    {
    }

    /**
     * Sets up the test fixture: a normal, a lefty and a rebel wheel,
     * each with the symbols red, green and blue.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        machine = new SlotMachine();
        String[] types = {"normal", "lefty", "rebel"};
        for (int w = 1; w <= 3; w++) {
            machine.addWheel(types[w - 1], w);
            machine.addSymbol(1, "red");
            machine.addSymbol(2, "green");
            machine.addSymbol(3, "blue");
        }
    }

    // RojasH

    /**
     * Verifica que se puedan agregar simbolos de los tres tipos.
     */
    @Test
    public void shouldAddSymbolsOfEveryType()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        assertTrue(slotMachine.ok());
        assertEquals(3, slotMachine.symbols().length);
    }

    /**
     * Verifica que la rueda rebel no se deje bloquear, intercambiar
     * ni eliminar, y que la maquina quede igual despues de intentarlo.
     */
    @Test
    public void shouldRebelWheelRefuseLockSwapAndDelete()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel("rebel", 1);
        slotMachine.addWheel(2);
        slotMachine.placeSymbol(1, "red");
        slotMachine.placeSymbol(2, "blue");
        slotMachine.lock(1);
        assertFalse(slotMachine.ok());
        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
        assertEquals(2, slotMachine.configuration().length);
        assertEquals("red", slotMachine.configuration()[0]);
        assertEquals("blue", slotMachine.configuration()[1]);
    }

    /**
     * Verifica que la maquina funcione con los tres tipos de rueda y los
     * tres tipos de simbolo a la vez: todos los giros son exitosos y la
     * lefty copia siempre a la rueda de su izquierda.
     */
    @Test
    public void shouldSpinMachineWithEveryWheelAndSymbolType()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        slotMachine.addWheel("normal", 1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.addWheel("rebel", 3);
        for (int i = 0; i < 10; i++) {
            slotMachine.spin();
            assertTrue(slotMachine.ok());
            String[] config = slotMachine.configuration();
            assertEquals(config[0], config[1]);
        }
    }

    // GRUPO 6: CañonA - PaezP

    /**
     * Verifica que una rueda rebel no se pueda intercambiar.
     */
    @Test
    public void acordingCaPpShouldNotSwapRebelWheel()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addWheel("normal", 1);
        slotMachine.addWheel("rebel", 2);
        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());
    }

    // GRUPO 4: Gomez - Rojas

    /**
     * Verifica que la lefty copie a la rueda normal de su izquierda.
     */
    @Test
    public void gomRojLeftyDeberiaCopiarALaRuedaNormalDeSuIzquierda()
    {
        machine.placeSymbol(1, "blue");
        machine.spin(2);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }

    /**
     * Verifica que la rebel no se deje bloquear, intercambiar ni eliminar.
     */
    @Test
    public void gomRojRebelNoDeberiaBloquearseIntercambiarseNiEliminarse()
    {
        String[] original = machine.configuration();
        machine.lock(3);
        assertFalse(machine.ok());
        machine.swap(3, 1);
        assertFalse(machine.ok());
        machine.delWheel(3);
        assertFalse(machine.ok());
        assertArrayEquals(original, machine.configuration());
        assertEquals(3, machine.configuration().length);
    }

    // GRUPO 2: Davila - Orozco

    /**
     * Verifica que la rebel rechace bloquearse, eliminarse e intercambiarse.
     */
    @Test
    public void accordingToDoOLrebelWheelRejectsLockDeleteAndSwap()
    {
        SlotMachine m = new SlotMachine(3);
        m.addWheel("rebel", 4);
        assertTrue(m.ok());
        m.lock(4);
        assertFalse(m.ok());
        m.delWheel(4);
        assertFalse(m.ok());
        m.swap(1, 4);
        assertFalse(m.ok());
    }

    /**
     * Verifica que la lefty copie a su vecina de la izquierda.
     */
    @Test
    public void accordingToDoOLleftyWheelCopiesItsLeftNeighbour()
    {
        SlotMachine m = new SlotMachine(3);
        m.addWheel("lefty", 4);
        m.spin();
        String[] shown = m.configuration();
        assertEquals(shown[2], shown[3]);
    }

    // GRUPO 4: AnzolaA - CastañedaC

    /**
     * The normal wheel is changed first. Then the LeftyWheel
     * is spun so that it copies the current state of its
     * immediate left wheel.
     */
    @Test
    public void shouldMakeLeftyWheelCopyImmediateLeftWheel()
    {
        String[] firstConfiguration = machine.configuration();
        machine.spin(1, 1);
        String[] secondConfiguration = machine.configuration();
        assertNotEquals(firstConfiguration[0], secondConfiguration[0],
            "The normal wheel should have changed state.");
        machine.spin(2);
        String[] finalConfiguration = machine.configuration();
        assertEquals(finalConfiguration[0], finalConfiguration[1],
            "The LeftyWheel must copy the symbol shown by the wheel immediately to its left.");
    }

    /**
     * The RebelWheel must remain in the machine after
     * all the restricted operations are attempted.
     */
    @Test
    public void shouldKeepRebelWheelUnaffectedByRestrictedOperations()
    {
        String[] before = machine.configuration();
        machine.lock(3);
        assertFalse(machine.ok(), "The RebelWheel cannot be locked.");
        machine.swap(1, 3);
        assertFalse(machine.ok(), "The RebelWheel cannot participate in a swap.");
        machine.delWheel(3);
        assertFalse(machine.ok(), "The RebelWheel cannot be deleted.");
        String[] after = machine.configuration();
        assertEquals(3, after.length, "The RebelWheel must remain in the machine.");
        assertArrayEquals(before, after, "The configuration must remain unchanged.");
    }

    // G02: Quintero - Rodriguez

    /**
     * Test that a shy symbol behaves correctly when selected.
     */
    @Test
    public void shouldSelectShySymbol()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addSymbol("shy", 1, "green");
        slotMachine.addSymbol("normal", 2, "red");
        slotMachine.addWheel("normal", 1);
        slotMachine.spin(1);
        assertTrue(slotMachine.ok());
    }

    /**
     * Test that a Rebel wheel cannot lock.
     */
    @Test
    public void shouldNotLockRebelWheel()
    {
        SlotMachine slotMachine = new SlotMachine();
        slotMachine.addWheel("rebel", 1);
        slotMachine.lock(1);
        assertFalse(slotMachine.ok());
    }
}
