package ox.engine;

import ox.engine.internal.OxAction;

import java.util.ArrayList;
import java.util.List;

public class OxActionLeakDetector {

    private static List<OxAction> pendingActions = new ArrayList<>();
    private static List<OxAction> executedActions = new ArrayList<>();

    protected static void addAction(OxAction action) {
        pendingActions.add(action);
    }

    protected static void markAsExecuted(OxAction action) {
        executedActions.add(action);
    }

    protected static List<OxAction> getPendingActions() {
        return pendingActions;
    }

}
