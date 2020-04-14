package seedu.address.logic.commands.studentdisplay;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.academics.TypicalAssessments.getTypicalAcademics;
import static seedu.address.testutil.admin.TypicalDates.getTypicalAdmin;
import static seedu.address.testutil.event.TypicalEvents.getTypicalEventHistory;
import static seedu.address.testutil.notes.TypicalNotes.getTypicalNotesManager;
import static seedu.address.testutil.student.TypicalStudents.getTypicalTeaPet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.student.DefaultStudentDisplayCommand;
import seedu.address.logic.commands.student.StudentAddCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.student.StudentBuilder;



public class DefaultStudentDisplayCommandTest {
    private Model model;
    private Model expectedModel;



    @BeforeEach
    public void setUp() {

        model = new ModelManager(getTypicalTeaPet(), getTypicalAcademics(), getTypicalAdmin(), getTypicalNotesManager(),
                getTypicalEventHistory(), new UserPrefs());
        expectedModel = new ModelManager(model.getTeaPet(), model.getAcademics(), model.getAdmin(),
                model.getNotesManager(), getTypicalEventHistory(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new DefaultStudentDisplayCommand(), model,
                DefaultStudentDisplayCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        Student alice = new StudentBuilder().withName("Alice").build();
        Student bob = new StudentBuilder().withName("Bob").build();
        StudentAddCommand addAliceCommand = new StudentAddCommand(alice);
        StudentAddCommand addBobCommand = new StudentAddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        StudentAddCommand addAliceCommandCopy = new StudentAddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different student -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

}
