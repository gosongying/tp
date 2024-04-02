package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_CLIENTS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Area;
import seedu.address.model.person.Client;
import seedu.address.model.person.Email;
import seedu.address.model.person.HousekeepingDetails;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Type;
import seedu.address.model.tag.Tag;

public class EditClientCommand extends EditCommand {
    public static final String MESSAGE_EDIT_CLIENT_SUCCESS = "Edited Client: %1$s";
    public static final String MESSAGE_DUPLICATE_CLIENT = "This client already exists in the address book.";

    public EditClientCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        super(index, editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CLIENT_DISPLAYED_INDEX);
        }

        Client personToEdit = lastShownList.get(index.getZeroBased());
        Client editedClient = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedClient) && model.hasClient(editedClient)) {
            throw new CommandException(MESSAGE_DUPLICATE_CLIENT);
        }

        model.setClient(personToEdit, editedClient);

        return new CommandResult(String.format(MESSAGE_EDIT_CLIENT_SUCCESS, Messages.formatClient(editedClient)));
    }

    /**
     * Creates and returns a {@code Client} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    @Override
    protected Client createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Type updatedType = editPersonDescriptor.getType().orElse(personToEdit.getType());
        Area updatedArea = editPersonDescriptor.getArea().orElse(personToEdit.getArea());
        HousekeepingDetails updatedDetails =
                editPersonDescriptor.getDetails().orElse(personToEdit.getDetails());

        return new Client(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedType,
                updatedDetails, updatedArea);
    }
}
