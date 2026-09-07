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
    public void accordingIcPgShouldNotSwap()
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
     * Verifies that a wheel can be spun successfully once it has been
     * locked and then unlocked, leaving the machine status as ok.
     */
    @Test
    public void accordingIcPgshouldSpinAWheel() {
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
     * Verifica que, tras bloquear una rueda con lock(), llamar a
     * spin(wheel, steps) no altera lo que esa rueda muestra en
     * configuration().
     */
    @Test
    public void accordingCgCpShouldNotChangeConfigurationWhenWheelIsLocked()
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
    public void accordingCgCpShouldChangeConfigurationWhenWheelIsUnlockedAfterLock()
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
    public void accordingFsGcShouldNotSwapWhenAWheelIsLocked()
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
    public void accordingBaGqShouldLockWheel()
    {
        slotMachine.addWheel(1);
        slotMachine.lock(1);
        assertTrue(slotMachine.ok());
    }

    /* Adding a color that already exists on a wheel should fail */
    @Test
    public void accordingCgHnAddSymbolShouldFailWhenColorAlreadyExists()
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
    public void accordingClPcShouldKeepDistinctSymbolCountAfterSwap()
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
    public void accordingJcSrShouldNotSpinLockedWheel()
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
    public void accordingGmLaShouldNotAllowSpinningALockedWheel()
    {
        slotMachine.lock(1);
        slotMachine.spin(1);
        assertFalse(slotMachine.ok(), "una rueda fija no debería poder girar");
    }
}