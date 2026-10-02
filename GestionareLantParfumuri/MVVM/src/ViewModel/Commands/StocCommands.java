package ViewModel.Commands;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class StocCommands implements EventHandler<ActionEvent> {
    private final Runnable action;
    private final CanExecuteEvaluator evaluator;

    public interface CanExecuteEvaluator {
        boolean canExecute();
    }

    public StocCommands(Runnable action, CanExecuteEvaluator evaluator) {
        this.action = action;
        this.evaluator = evaluator;
    }

    public boolean canExecute() {
        return  evaluator.canExecute();
    }

    @Override
    public void handle(ActionEvent event) {
        if (canExecute() && action != null) {
            action.run();
        }
    }
}
