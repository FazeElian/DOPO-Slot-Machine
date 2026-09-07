import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class SlowMachineCC2Test.
 *
 * @author  DOPO-2026-02
 * @version 1.0
 */
public class SlowMachineCC2Test
{
    private SlotMachine slotMachine;

    /**
     * Default constructor for test class SlowMachineCC2Test
     */
    public SlowMachineCC2Test()
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
        slotMachine = new SlotMachine();
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

    /**
     * Verifies that attempting to swap two wheels fails when one of
     * them is locked, setting the machine status to not ok.
     */
    @Test
    public void shouldNotSwap()
    {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");

        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);

        slotMachine.lock(1);

        slotMachine.swap(1, 3);

        assertFalse(slotMachine.ok());
    }

    /**
     * Verifica que, tras bloquear una rueda con lock(), llamar a
     * spin(wheel, steps) no altera lo que esa rueda muestra en
     * configuration().
     */
    @Test
    public void shouldNotChangeConfigurationWhenWheelIsLocked()
    {
        slotMachine.addWheel(1);
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        slotMachine.placeSymbol(1, "red");

        slotMachine.lock(1);
        String before = slotMachine.configuration()[0];
        slotMachine.spin(1, 1);

        assertEquals(before, slotMachine.configuration()[0]);
    }

    /**
     * Verificar que, tras bloquear y luego desbloquear una rueda
     * (lock() seguido de unlock()), spin(wheel, steps) sí puede
     * cambiar lo que esa rueda muestra.
     */
    @Test
    public void shouldChangeConfigurationWhenWheelIsUnlockedAfterLock()
    {
        slotMachine.addWheel(1);
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "blue");
        slotMachine.placeSymbol(1, "red");

        slotMachine.lock(1);
        slotMachine.unlock(1);
        slotMachine.spin(1, 1);

        assertTrue(slotMachine.ok());
    }

    /**
     * QUE NO DEBERIA HACER: intercambiar dos ruedas cuando una de ellas
     * esta fija (lock). La operacion debe fallar y dejar ok() en false,
     * sin alterar la configuracion de ninguna rueda.
     */
    @Test
    public void shouldNotSwapWhenAWheelIsLocked()
    {
        slotMachine.lock(1);
        String[] before = slotMachine.configuration().clone();
        slotMachine.swap(1, 3);
        assertFalse(slotMachine.ok());
        assertArrayEquals(before, slotMachine.configuration());
    }

    /**
     * Prueba que una rueda existente pueda ser bloqueada correctamente.
     * La maquina debe permitir bloquear una rueda que existe.
     * La operacion debe realizarse correctamente y ok() debe retornar true.
     */
    @Test
    public void shouldLockWheel()
    {
        slotMachine.addWheel(1);
        slotMachine.lock(1);
        assertTrue(slotMachine.ok());
    }

    /* Adding a color that already exists on a wheel should fail */
    @Test
    public void addSymbolShouldFailWhenColorAlreadyExists()
    {
        slotMachine.addWheel(1);
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(1, "red");
        assertFalse(slotMachine.ok());
    }

    /**
     * Verifica que el intercambio de ruedas no altere el catalogo de
     * simbolos distintos reportado por la maquina.
     */
    @Test
    public void shouldKeepDistinctSymbolCountAfterSwapWithPlacedSymbols()
    {
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.placeSymbol(1, "red");
        slotMachine.placeSymbol(2, "blue");

        int before = slotMachine.distinctSymbols();
        slotMachine.swap(1, 2);
        int after = slotMachine.distinctSymbols();

        assertTrue(slotMachine.ok());
        assertEquals(before, after, "swap no deberia alterar el catalogo de simbolos");
    }

    /** Una rueda fijada no debe girar */
    @Test
    public void lockedWheelShouldNotSpin()
    {
        String[] before = slotMachine.configuration();
        slotMachine.lock(1);
        slotMachine.spin(1);
        String[] after = slotMachine.configuration();
        assertArrayEquals(before, after);
    }

    /**
     * Una rueda fija no debería poder girar.
     */
    @Test
    public void shouldNotAllowSpinningALockedWheel()
    {
        slotMachine.lock(1);
        slotMachine.spin(1);
        assertFalse(slotMachine.ok(), "una rueda fija no debería poder girar");
    }
}