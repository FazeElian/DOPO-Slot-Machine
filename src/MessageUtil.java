import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * JOptionPane utility class for global application alerts
 * MessageUtil
 */
public class MessageUtil {
    public final static int DIALOG_WIDTH = 400;
    public final static int DIALOG_HEIGHT = 280;

    /**
     * Shows a popup message based on the specified message
     * type, rendering its title and content on it.
     * @param message message to display
     * @param title
     * @param messageType
     */
    private static void showMessage(String message, String title, int messageType) {
        // Create message label
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Wrap the label in a panel that fills all the available space
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(messagePanel, messageType);
        JDialog dialog = optionPane.createDialog(title);

        // Set size with global static variables
        dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);

        // Centers the dialog message on the screen
        dialog.setLocationRelativeTo(null);

        // Make it visible on the screen
        dialog.setVisible(true);
    }

    /**
     * Displays a success dialog for successful user actions.
     * @param message message to display
     */
    public static void showSuccess(String message) {
        showMessage(
            message, 
            "Operación ejecutada con éxito",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a error dialog when an operation fails
     * @param message message to display
     */
    public static void showError(String message) {
        showMessage(
            message, 
            "ERROR",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a warning dialog for non-critical alerts
     * @param message message to display
     */
    public static void showWarning(String message) {
        showMessage(
            message, 
            "ADVERTENCIA",
            JOptionPane.WARNING_MESSAGE
        );
    }
    // public static void main(String[] args) {
    //     MessageUtil.showSuccess("Rueda creada con éxito!");
    // }
}