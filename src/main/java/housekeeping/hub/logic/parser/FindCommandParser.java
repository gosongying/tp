package housekeeping.hub.logic.parser;

import static housekeeping.hub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static housekeeping.hub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static housekeeping.hub.logic.parser.CliSyntax.PREFIX_AREA;
import static housekeeping.hub.logic.parser.CliSyntax.PREFIX_NAME;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import housekeeping.hub.logic.commands.FindClientCommand;
import housekeeping.hub.logic.commands.FindCommand;
import housekeeping.hub.logic.commands.FindHousekeeperCommand;
import housekeeping.hub.logic.parser.exceptions.ParseException;
import housekeeping.hub.model.person.Area;
import housekeeping.hub.model.person.ContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private String name = "";
    private String address = "";
    private String area = "";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        String[] subArgs = trimmedArgs.split("\\s+");
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_AREA);
        if (subArgs.length < 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String type = ParserUtil.parseType(subArgs[0]);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_AREA);

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            name = argMultimap.getValue(PREFIX_NAME).get();
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            address = argMultimap.getValue(PREFIX_ADDRESS).get();
        }
        if (argMultimap.getValue(PREFIX_AREA).isPresent()) {
            area = argMultimap.getValue(PREFIX_AREA).get();
        }

        String trimmedName = name.trim();
        String trimmedAddress = address.trim();
        String trimmedArea = area.trim();
        String[] nameKeywords = trimmedName.split("\\s+");
        String[] addressKeywords = trimmedAddress.split("\\s+");
        String[] areaKeywords = trimmedArea.split("\\s+");

        if (argMultimap.getValue(PREFIX_AREA).isPresent()) {
            for (int i = 0; i < areaKeywords.length; i++) {
                if (!Area.isValidArea(areaKeywords[i])) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, Area.MESSAGE_CONSTRAINTS));
                }
            }
        }

        if (nameKeywords[0].isEmpty() && addressKeywords[0].isEmpty() && areaKeywords[0].isEmpty()) {
            throw new ParseException(FindCommand.MESSAGE_NOT_FOUND);
        }

        if (type.equals("client")) {
            return new FindClientCommand(new ContainsKeywordsPredicate(Arrays.asList(nameKeywords),
                    Arrays.asList(addressKeywords), Arrays.asList(areaKeywords)));
        } else {
            // As type is ensured to be either client or housekeeper before, it is safe to assume that the type is
            // housekeeper, so we can directly return a FindHousekeeperCommand object.
            return new FindHousekeeperCommand(new ContainsKeywordsPredicate(Arrays.asList(nameKeywords),
                    Arrays.asList(addressKeywords), Arrays.asList(areaKeywords)));
        }
    }

}
