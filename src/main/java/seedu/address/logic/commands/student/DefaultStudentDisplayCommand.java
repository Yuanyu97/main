package seedu.address.logic.commands.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command which displays a concise version of students' list.
 */
public class DefaultStudentDisplayCommand extends StudentCommand {

    public static final String MESSAGE_SUCCESS = "The student list now displays DEFAULT details." + '\n'
            + "[HELP ON STUDENT COMMANDS]\n"
            + "DISPLAY DETAILED LIST: student detailed \n"
            + "ADD STUDENT: student add name/NAME phone/[PHONE] email/[EMAIL] adr/[ADDRESS] temp/[TEMPERATURE] att/[ATTENDANCE] "
            + "nok/[NAME-RELATIONSHIP-PHONE] tag/[TAG]  \n"
            + "EDIT STUDENT: student edit INDEX name/[NAME] phone/[PHONE] email/[EMAIL] adr/[ADDRESS] "
            + "temp/[TEMPERATURE] att/[ATTENDANCE] nok/[NAME-RELATIONSHIP-PHONE] tag/[TAG] \n"
            + "DELETE STUDENT: student delete INDEX\n"
            + "FIND STUDENT: student find NAME";

    /**
     * Creates an DefaultStudentDisplayCommand
     */
    public DefaultStudentDisplayCommand() {
    }

    /**
     * Overriden execute command which returns a new CommandResult containing success message.
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult
     * @throws CommandException
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        return new CommandResult(String.format(MESSAGE_SUCCESS));
    }

    @Override
    public boolean equals(Object other) {
        return true;
    }
}
