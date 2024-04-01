package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.LeadsFilterPredicate;

/**
 * Generates a list of leads based on the client's predicted next housekeeping date.
 */
public class LeadsCommand extends Command {

    public static final String COMMAND_WORD = "leads";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Generates a list of leads based on the client's "
            + "predicted next housekeeping date.\n"
            + "Example: " + COMMAND_WORD;

    private final LeadsFilterPredicate predicate = new LeadsFilterPredicate();

    public LeadsCommand() {}

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredClientList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredClientList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LeadsCommand)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
