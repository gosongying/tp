package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Client;
import seedu.address.model.person.Housekeeper;

public class DeleteHousekeeperCommand extends DeleteCommand {
    public static final String MESSAGE_DELETE_HOUSEKEEPER_SUCCESS = "Deleted Housekeeper: %1$s";

    public DeleteHousekeeperCommand(Index targetIndex) {
        super(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Housekeeper> lastShownList = model.getFilteredHousekeeperList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_HOUSEKEEPER_DISPLAYED_INDEX);
        }

        Housekeeper housekeeperToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteHousekeeper(housekeeperToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_HOUSEKEEPER_SUCCESS,
                Messages.formatHousekeeper(housekeeperToDelete)));
    }
}
