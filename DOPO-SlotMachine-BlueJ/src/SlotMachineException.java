/**
 * Exception thrown when an operation of the slot machine can't be done.
 * Its constants hold every error message of the simulator; the ones with
 * %s or %d are completed with String.format before throwing.
 *
 * @author Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public class SlotMachineException extends Exception {
    // Wheels
    public static final String MAX_WHEELS = "Ha alcanzado el máximo de ruedas posibles.";
    public static final String INVALID_WHEEL_TYPE = "El tipo de rueda %s no existe.";
    public static final String NO_WHEELS_TO_DELETE = "No hay ruedas para eliminar";
    public static final String CANT_REMOVE = "La rueda %d no puede eliminarse.";
    public static final String WHEEL_NOT_FOUND = "No existe esa rueda, intenta de nuevo";
    public static final String NO_WHEELS = "No existen ruedas,intenta de nuevo";
    public static final String ALREADY_LOCKED = "Esa rueda ya está bloqueada";
    public static final String CANT_LOCK = "Esa rueda no puede bloquearse.";
    public static final String NOT_LOCKED = "Esa rueda no estaba bloqueada";
    public static final String SAME_WHEEL = "No se puede intercambiar una rueda consigo misma.";
    public static final String CANT_SWAP = "Alguna de esas ruedas no puede intercambiarse.";

    // Symbols
    public static final String SYMBOL_EXISTS = "%s ya es un símbolo, elige uno nuevo";
    public static final String SYMBOL_TO_DELETE_NOT_FOUND = "Ese símbolo: %s no existe, añádelo e intenta de nuevo.";
    public static final String LAST_SYMBOL = "Solo queda un símbolo, no se puede eliminar";
    public static final String SYMBOL_NOT_FOUND = "Ese símbolo no existe, añádelo e intenta de nuevo.";
    public static final String NO_SYMBOLS = "No existen símbolos aún";
    public static final String INVALID_SYMBOL_TYPE = "El tipo de símbolo %s no existe, usa uno de los disponibles.";

    // Spins and configuration
    public static final String SPIN_WHEEL_NOT_FOUND = "No existen esa rueda, intenta de nuevo";
    public static final String WHEEL_NOT_IN_MACHINE = "No existe esa rueda en la maquina";
    public static final String LOCKED_WHEEL = "Esta rueda está bloqueada, no puede girarse";
    public static final String NO_WHEELS_TO_SPIN = "No hay ruedas por girar";
    public static final String LOCKED_WHEEL_IN_SPIN = "La rueda %d está bloqueada, no se puede girar.";
    public static final String MACHINE_WITHOUT_WHEELS = "La máquina no tiene ruedas.";
    public static final String LOCKED_WHEEL_TO_PLACE = "Esta rueda esta bloqueada";
    public static final String NOT_ENOUGH_SYMBOLS = "No tiene los símbolos suficientes para las ruedas de la máquina.";
    public static final String LOCKED_WHEEL_IN_CONFIGURATION = "La rueda %d está bloqueada.";
    public static final String SYMBOLS_MISMATCH = "Algunos simbolos no existen en sus ruedas correspondientes";

    /**
     * Constructs the exception with the message that explains the error
     * @param message description of the error, usually one of the constants
     */
    public SlotMachineException(String message) {
        super(message);
    }
}
