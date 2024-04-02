package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_CLIENTS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_HOUSEKEEPERS;

import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Booking;
import seedu.address.model.person.BookingList;
import seedu.address.model.person.Client;
import seedu.address.model.person.Housekeeper;
import seedu.address.model.person.HousekeepingDetails;

/**
 * Encapsulates booking actions (add, delete, list) for a housekeeper.
 */
public class BookingCommand extends Command {

    public static final String COMMAND_WORD = "booking";
    public static final String ACTION_WORD_ADD = "add";
    public static final String ACTION_WORD_DELETE = "delete";
    public static final String ACTION_WORD_LIST = "list";
    public static final String MESSAGE_INVALID_ACTION = "Invalid action. Action words include {add, delete, list}.";

    public static final String MESSAGE_USAGE = "Please refer to the command formats:\n\n" + COMMAND_WORD + " " + ACTION_WORD_ADD
            + ": adds a booking for the housekeeper identified "
            + "by the index number used in the displayed housekeeper list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[DATE] [TIME]\n"
            + "Example: " + COMMAND_WORD + " " + ACTION_WORD_ADD + " 1 2024-05-12 am\n\n"
            + COMMAND_WORD + " " + ACTION_WORD_DELETE
            + ": deletes the specified booking for the housekeeper identified "
            + "by the index numbers used in the booking list and displayed housekeeper list respectively.\n"
            + "Parameters: HOUSEKEEPER_INDEX (must be a positive integer) BOOKING_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + ACTION_WORD_DELETE + " 1 1\n\n"
            + COMMAND_WORD + " " + ACTION_WORD_LIST
            + ": lists all bookings for the housekeeper identified "
            + "by the index number used in the displayed housekeeper list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + ACTION_WORD_LIST + " 1\n";

    public static final String ADD_MESSAGE_CONSTRAINT = "If client does not have housekeeping details, "
            + "please set housekeeping details first using 'set'.";

    private String actionWord;
    private Index index;
    private int bookingToDeleteIndex;
    private String bookedDateAndTime;
    private String type;
    private HousekeepingDetails housekeepingDetails;

    /**
     * Constructs a BookingCommand for the "add" action.
     *
     * @param actionWord "add"
     * @param index of housekeeper to add booking to
     * @param bookedDateAndTime in the form of a string
     */
    public BookingCommand(String type, String actionWord, Index index, String bookedDateAndTime) {
        requireNonNull(index);
        this.type = type;
        this.actionWord = actionWord;
        this.index = index;
        this.bookedDateAndTime = bookedDateAndTime;
    }

    /**
     * Constructs a BookingCommand for the "delete" action.
     *
     * @param actionWord "delete"
     * @param index of housekeeper to delete booking from
     * @param bookingToDeleteIndex of booking to delete
     */
    public BookingCommand(String type, String actionWord, Index index, int bookingToDeleteIndex) {
        requireNonNull(index);
        this.type = type;
        this.actionWord = actionWord;
        this.index = index;
        this.bookingToDeleteIndex = bookingToDeleteIndex;
    }

    /**
     * Constructs a BookingCommand for the "list" action.
     *
     * @param actionWord "list"
     * @param index of housekeeper whose bookings to list
     */
    public BookingCommand(String type, String actionWord, Index index) {
        requireNonNull(index);
        this.type = type;
        this.actionWord = actionWord;
        this.index = index;
    }

    public BookingCommand(String type, String actionWord, Index index, HousekeepingDetails housekeepingDetails) {
        requireNonNull(index);
        this.type = type;
        this.actionWord = actionWord;
        this.index = index;
        this.housekeepingDetails = housekeepingDetails;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (type.equals("client")) {
            switch (actionWord) {
            case "add":
                return clientAdd(model);
            case "delete":
                return clientDelete(model);
            case "set":
                return clientSet(model);
            case "remove":
                return clientRemove(model);
            default:
                throw new CommandException(MESSAGE_INVALID_ACTION);
            }
        } else if (type.equals("housekeeper")) {
            switch (actionWord) {
            case "add":
                return housekeeperAdd(model);
            case "delete":
                return housekeeperDelete(model);
            case "list":
                return housekeeperList(model);
            default:
                throw new CommandException(MESSAGE_INVALID_ACTION);
            }
        } else {
            throw new CommandException(MESSAGE_INVALID_ACTION);
        }
    }

    private CommandResult clientAdd(Model model) throws CommandException {
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Booking booking = new Booking(bookedDateAndTime);

        Client clientToEdit = lastShownList.get(index.getZeroBased());
        if (clientToEdit.hasHousekeepingDetails()) {
            HousekeepingDetails details = clientToEdit.getDetails();
            details.setBooking(booking);

            EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();
            editPersonDescriptor.setDetails(details);

            Command editClientCommand = new EditClientCommand(index, editPersonDescriptor);
            return editClientCommand.execute(model);
        } else {
            throw new CommandException(ADD_MESSAGE_CONSTRAINT);
        }
    }

    private CommandResult clientDelete(Model model) throws CommandException {
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Client clientToEdit = lastShownList.get(index.getZeroBased());
        HousekeepingDetails details = clientToEdit.getDetails();
        details.deleteBooking();

        EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();
        editPersonDescriptor.setDetails(details);

        Command editClientCommand = new EditClientCommand(index, editPersonDescriptor);
        return editClientCommand.execute(model);
    }

    private CommandResult clientSet(Model model) throws CommandException {
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();
        editPersonDescriptor.setDetails(housekeepingDetails);


        Command editClientCommand = new EditClientCommand(index, editPersonDescriptor);
        return editClientCommand.execute(model);
    }

    private CommandResult clientRemove(Model model) throws CommandException {
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();
        editPersonDescriptor.setDetails(HousekeepingDetails.empty);

        Command editClientCommand = new EditClientCommand(index, editPersonDescriptor);
        return editClientCommand.execute(model);
    }

    private CommandResult housekeeperList(Model model) throws CommandException {
        List<Housekeeper> lastShownListList = model.getFilteredHousekeeperList();

        if (index.getZeroBased() >= lastShownListList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Housekeeper housekeeperToListBooking = lastShownListList.get(index.getZeroBased());
        String listResult = housekeeperToListBooking.listBooking();
        return new CommandResult(listResult);
    }

    private CommandResult housekeeperDelete(Model model) throws CommandException {
        List<Housekeeper> lastShownListDelete = model.getFilteredHousekeeperList();

        if (index.getZeroBased() >= lastShownListDelete.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Housekeeper housekeeperToDeleteBooking = lastShownListDelete.get(index.getZeroBased());
        if (!housekeeperToDeleteBooking.isValidDeleteIndex(bookingToDeleteIndex)) {
            throw new CommandException((BookingList.MESSAGE_INVALID_DELETE));
        }

        if (bookingToDeleteIndex == 0) {
            throw new CommandException(BookingList.MESSAGE_INVALID_DELETE);
        }

        String deleteResult = housekeeperToDeleteBooking.deleteBooking(bookingToDeleteIndex);
        return new CommandResult(deleteResult);
    }

    private CommandResult housekeeperAdd(Model model) throws CommandException {
        List<Housekeeper> lastShownListAdd = model.getFilteredHousekeeperList();

        if (index.getZeroBased() >= lastShownListAdd.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        try {
            Housekeeper housekeeperToAddBooking = lastShownListAdd.get(index.getZeroBased());
            if (housekeeperToAddBooking.hasDuplicateBooking(bookedDateAndTime)) {
                throw new CommandException(housekeeperToAddBooking.getName() + " " + BookingList.MESSAGE_DUPLICATE);
            }
            String addResult = housekeeperToAddBooking.addBooking(bookedDateAndTime);

            // edit housekeeper with updated booking list
            EditCommand.EditPersonDescriptor editHousekeeperDescriptor = new EditCommand.EditPersonDescriptor();
            editHousekeeperDescriptor.setBookingList(housekeeperToAddBooking.getBookingList());
            EditHousekeeperCommand command = new EditHousekeeperCommand(index, editHousekeeperDescriptor);
            Housekeeper editedHousekeeper = command.createEditedPerson(housekeeperToAddBooking, editHousekeeperDescriptor);


            model.setHousekeeper(housekeeperToAddBooking, editedHousekeeper);
            model.updateFilteredHousekeeperList(PREDICATE_SHOW_ALL_HOUSEKEEPERS);

            return new CommandResult(String.format(addResult, Messages.formatHousekeeper(housekeeperToAddBooking)));
        } catch (DateTimeParseException e) {
            throw new CommandException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof BookingCommand)) {
            return false;
        }

        BookingCommand otherBookingCommand = (BookingCommand) other;
        return actionWord.equals(otherBookingCommand.actionWord)
                && index.equals(otherBookingCommand.index)
                && (bookingToDeleteIndex == otherBookingCommand.bookingToDeleteIndex)
                && bookedDateAndTime.equals(otherBookingCommand.bookedDateAndTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("actionWord", actionWord)
                .add("housekeeperIndex", index)
                .add("bookingToDeleteIndex", bookingToDeleteIndex)
                .add("bookedDateAndTime", bookedDateAndTime)
                .toString();
    }
}
