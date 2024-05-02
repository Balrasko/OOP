import logic.*;

import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.SwingWorker;

public class LiveResultsUpdater extends SwingWorker<Void, String> {
    private final JEditorPane resultsDisplay; // Field for storing the editor pane

    public LiveResultsUpdater(JEditorPane resultsPane) {
        this.resultsDisplay = resultsPane; // Assign the constructor parameter to the field
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            String results = VotingManager.getFormattedResults();
            publish(results);
            Thread.sleep(10000); // Corrected to refresh every 10 seconds (10000 milliseconds)
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        if (!chunks.isEmpty()) {
            String latestResults = chunks.get(chunks.size() - 1);
            resultsDisplay.setText(latestResults);
        }
    }
}
