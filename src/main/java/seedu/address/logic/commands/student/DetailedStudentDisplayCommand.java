package seedu.address.logic.commands.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_NOTES;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command which displays a comprehensive version of students' list.
 */
public class DetailedStudentDisplayCommand extends StudentCommand {

    public static final String MESSAGE_SUCCESS = "The Student list now displays ALL details";

    /**
     * Creates an DetailedStudentDisplayCommand
     */
    public DetailedStudentDisplayCommand() {
    }

    /**
     * The execute command which returns a new CommandResult containing success message.
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult
     * @throws CommandException
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        model.updateFilteredNotesList(PREDICATE_SHOW_ALL_NOTES);
        return new CommandResult(String.format(MESSAGE_SUCCESS));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DetailedStudentDisplayCommand); // instanceof handles nulls
    }
}
