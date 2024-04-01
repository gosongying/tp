package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AREA;

import java.util.Arrays;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Area;
import seedu.address.model.person.ContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

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
        String trimmedArgs = args.trim();
        String[] subArgs = trimmedArgs.split("\\s+");
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_AREA);
        if (subArgs.length < 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        String type = subArgs[0];
        if ((!type.equals("client")) && (!type.equals("housekeeper"))) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_AREA);


        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            name = argMultimap.getValue(PREFIX_NAME).get();
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            address = argMultimap.getValue(PREFIX_ADDRESS).get();
        }
        if (argMultimap.getValue(PREFIX_AREA).isPresent()) {
            area = argMultimap.getValue(PREFIX_AREA).get();
            if (!Area.isValidArea(area)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, Area.MESSAGE_CONSTRAINTS));
            }
        }

        String trimmedName = name.trim();
        String trimmedAddress = address.trim();
        String trimmedArea = area.trim();
        String[] nameKeywords = trimmedName.split("\\s+");
        String[] addressKeywords = trimmedAddress.split("\\s+");
        String[] areaKeywords = trimmedArea.split("\\s+");
        return new FindCommand(type, new ContainsKeywordsPredicate(Arrays.asList(nameKeywords),
                Arrays.asList(addressKeywords), Arrays.asList(areaKeywords)));
    }

}
