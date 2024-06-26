package housekeeping.hub.logic.parser;

import static housekeeping.hub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import housekeeping.hub.logic.commands.ListClientCommand;
import housekeeping.hub.logic.commands.ListCommand;
import housekeeping.hub.logic.commands.ListHousekeeperCommand;
import housekeeping.hub.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        String[] type = trimmedArgs.split("\\s+");

        if (type.length != 1) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        if (!trimmedArgs.equals("client") && !trimmedArgs.equals("housekeeper")) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        if (trimmedArgs.equals("client")) {
            return new ListClientCommand();
        } else {
            // As the type is either "client" or "housekeeper", we can safely assume that the type is "housekeeper"
            return new ListHousekeeperCommand();
        }
    }

}
