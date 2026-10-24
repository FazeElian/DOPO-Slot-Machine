package domain;

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
    /** The machine already has the maximum number of wheels. */
    public static final String MAX_WHEELS = "Ha alcanzado el máximo de ruedas posibles.";
    /** The wheel type (%s) doesn't exist. */
    public static final String INVALID_WHEEL_TYPE = "El tipo de rueda %s no existe.";
    /** The machine has no wheels. */
    public static final String NO_WHEELS = "La máquina no tiene ruedas.";
    /** There is no wheel at that position. */
    public static final String WHEEL_NOT_FOUND = "No existe esa rueda, intenta de nuevo.";
    /** The wheel at position %d can't be removed. */
    public static final String CANT_REMOVE = "La rueda %d no puede eliminarse.";
    /** The wheel can't be locked. */
    public static final String CANT_LOCK = "Esa rueda no puede bloquearse.";
    /** The wheel to unlock wasn't locked. */
    public static final String NOT_LOCKED = "Esa rueda no estaba bloqueada.";
    /** The wheel is locked: it can't be spun, changed nor locked again. */
    public static final String LOCKED_WHEEL = "Esa rueda está bloqueada.";
    /** A wheel can't be swapped with itself. */
    public static final String SAME_WHEEL = "No se puede intercambiar una rueda consigo misma.";
    /** One of the wheels to swap can't be swapped. */
    public static final String CANT_SWAP = "Alguna de esas ruedas no puede intercambiarse.";

    // Symbols
    /** The symbol (%s) is already registered. */
    public static final String SYMBOL_EXISTS = "%s ya es un símbolo, elige uno nuevo.";
    /** The symbol (%s) isn't registered. */
    public static final String SYMBOL_NOT_FOUND = "El símbolo %s no existe, añádelo e intenta de nuevo.";
    /** The only remaining symbol can't be removed. */
    public static final String LAST_SYMBOL = "Solo queda un símbolo, no se puede eliminar.";
    /** There are no registered symbols yet. */
    public static final String NO_SYMBOLS = "No existen símbolos aún.";
    /** The symbol type (%s) doesn't exist. */
    public static final String INVALID_SYMBOL_TYPE = "El tipo de símbolo %s no existe, usa uno de los disponibles.";

    // Configuration
    /** Fewer symbols were given than wheels on the machine. */
    public static final String NOT_ENOUGH_SYMBOLS = "No tiene los símbolos suficientes para las ruedas de la máquina.";
    /** Some symbols don't exist on their matching wheels. */
    public static final String SYMBOLS_MISMATCH = "Algunos símbolos no existen en sus ruedas correspondientes.";

    /**
     * Constructs the exception with the message that explains the error
     * @param message description of the error, usually one of the constants
     */
    public SlotMachineException(final String message) {
        super(message);
    }
}
