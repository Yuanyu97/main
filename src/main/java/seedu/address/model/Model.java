package seedu.address.model;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.academics.Assessment;
import seedu.address.model.academics.ReadOnlyAcademics;
import seedu.address.model.notes.Notes;
import seedu.address.model.notes.ReadOnlyNotes;
import seedu.address.model.student.Student;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Student> PREDICATE_SHOW_ALL_STUDENTS = unused -> true;
    Predicate<Assessment> PREDICATE_SHOW_ALL_ASSESSMENTS = unused -> true;
    Predicate<Notes> PREDICATE_SHOW_ALL_NOTES = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    // ==================== ADDRESS BOOK START ====================
    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a student with the same identity as {@code student} exists in the address book.
     */
    boolean hasStudent(Student student);

    /**
     * Deletes the given student.
     * The student must exist in the address book.
     */
    void deleteStudent(Student target);

    /**
     * Adds the given student.
     * {@code student} must not already exist in the address book.
     */
    void addStudent(Student student);

    /**
     * Replaces the given student {@code target} with {@code editedStudent}.
     * {@code target} must exist in the address book.
     * The student identity of {@code editedStudent} must not be the same as another existing student
     * in the address book.
     */
    void setStudent(Student target, Student editedStudent);

    /** Returns an unmodifiable view of the filtered student list */
    ObservableList<Student> getFilteredStudentList();

    /**
     * Updates the filter of the filtered student list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredStudentList(Predicate<Student> predicate);
    // ==================== ADDRESS BOOK END ====================

    // ==================== ACADEMICS START ====================
    /**
     * Returns the user prefs' academics file path.
     */
    Path getAcademicsFilePath();

    /**
     * Sets the user prefs' academics file path.
     */
    void setAcademicsFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAcademics(ReadOnlyAcademics academics);

    /** Returns the Academics */
    ReadOnlyAcademics getAcademics();

    /**
     * Returns true if an assessment with the same identity as {@code assessment} exists in the Academics
     * record.
     */
    boolean hasAssessment(Assessment assessment);

    /**
     * Returns the assessment that has been deleted based on the index.
     */
    void deleteAssessment(Assessment target);

    /**
     * Adds the given assessment. {@code assessment} must not exist in the assessment list.
     */
    void addAssessment(Assessment assessment);

    /**
     * Replaces the assessment at the specified index.
     */
    void setAssessment(Assessment target, Assessment assessment);

    /**
     * Submits students' submission for the specified assessment.
     */
    void submitAssessment(Assessment target, List<String> students);

    /** Returns an unmodifiable view of the filtered academics list */
    ObservableList<Assessment> getFilteredAcademicsList();

    /** Returns a list of homework assessments */
    ObservableList<Assessment> getHomeworkList();

    /** Returns a list of homework assessments */
    ObservableList<Assessment> getExamList();

    /**
     * Updates the filter of the filtered academics list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredAcademicsList(Predicate<Assessment> predicate);
    // ==================== ACADEMICS END ====================

    // ==================== NOTES START ====================
    /**
     * Returns the user prefs' notes file path.
     */
    Path getNotesFilePath();

    /**
     * Sets the user prefs' notes file path.
     */
    void setNotesFilePath(Path notesFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setNotesManager(ReadOnlyNotes notes);

    /** Returns the NotesManager */
    ReadOnlyNotes getNotesManager();

    /**
     * Returns true if a note with the same identity as {@code note} exists in the Notes
     * record.
     */
    boolean hasNote(Notes note);

    /**
     * Returns the note that has been deleted based on the index.
     */
    void deleteNote(Notes toBeDeleted);

    /**
     * Adds the given note. {@code note} must not exist in the notes list.
     */
    void addNote(Notes note);

    /**
     * Replaces the note at the specified index.
     */
    void setNote(Notes toBeChanged, Notes editedNote);

    /** Returns an unmodifiable view of the filtered notes list */
    ObservableList<Notes> getFilteredNotesList();

    /**
     * Updates the filter of the filtered academics list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredNotesList(Predicate<Notes> predicate);
    // ==================== NOTES END ====================
}
