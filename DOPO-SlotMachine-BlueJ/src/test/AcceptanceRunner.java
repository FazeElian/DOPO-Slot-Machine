package test;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import javax.swing.JOptionPane;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Entry point of lib/AcceptanceTest.jar: runs {@link AcceptanceTest} with
 * JUnit and shows the result in a dialog, so the acceptance test can be
 * started with a double click, without an IDE.
 *
 * @author  Oscar Poveda, Elian Ibarra
 * @version 1.0
 */
public final class AcceptanceRunner
{
    private AcceptanceRunner()
    {
    }

    /**
     * Runs the acceptance test and shows whether it passed.
     *
     * @param args not used
     */
    public static void main(final String[] args)
    {
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(AcceptanceTest.class))
            .build();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        final Launcher launcher = LauncherFactory.create();
        launcher.execute(request, listener);

        final TestExecutionSummary summary = listener.getSummary();
        if (summary.getTotalFailureCount() == 0 && summary.getTestsSucceededCount() > 0) {
            JOptionPane.showMessageDialog(null,
                "La prueba de aceptación pasó.",
                "Prueba de aceptación", JOptionPane.INFORMATION_MESSAGE);
        } else {
            final StringBuilder message = new StringBuilder("La prueba de aceptación falló.");
            for (final TestExecutionSummary.Failure failure : summary.getFailures()) {
                message.append("\n\n").append(failure.getException());
            }
            JOptionPane.showMessageDialog(null, message.toString(),
                "Prueba de aceptación", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }
}
