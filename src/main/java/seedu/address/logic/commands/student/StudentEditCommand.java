package seedu.address.logic.commands.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.student.StudentAddCommand.MESSAGE_DUPLICATE_STUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ATTENDANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TEMPERATURE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_NOTES;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.notes.NotesEditCommand.EditNotesDescriptor;
import seedu.address.model.Model;
import seedu.address.model.notes.Notes;
import seedu.address.model.student.Address;
import seedu.address.model.student.Attendance;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.NextOfKin;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.Temperature;

import seedu.address.model.tag.Tag;


/**
 * Edits the details of an existing student in the address book.
 */
public class StudentEditCommand extends StudentCommand {

    public static final String MESSAGE_USAGE = "This Command edits the details of the student identified. \n"
            + "Parameters: [STUDENT INDEX] "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_NOK + "NOK] "
            + "[" + PREFIX_TEMPERATURE + "TEMPERATURE] "
            + "[" + PREFIX_ATTENDANCE + "ATTENDANCE] "
            + "[" + PREFIX_TAG + "TAG]...\n";

    public static final String MESSAGE_EDIT_STUDENT_SUCCESS = "Edited Student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final Index index;
    private final EditStudentDescriptor editStudentDescriptor;

    /**
     * @param index of the student in the filtered student list to edit
     * @param editStudentDescriptor details to edit the student with
     */
    public StudentEditCommand(Index index, EditStudentDescriptor editStudentDescriptor) {
        requireNonNull(index);
        requireNonNull(editStudentDescriptor);

        this.index = index;
        this.editStudentDescriptor = new EditStudentDescriptor(editStudentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);

        if (editStudentDescriptor.getName().isPresent()
                && model.hasStudentName(editStudentDescriptor.getName().get().fullName)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        if (!studentToEdit.isSameStudent(editedStudent) && model.hasStudent(editedStudent)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        model.setStudent(studentToEdit, editedStudent);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);

        // Edit Student's Name on Notes
        if (editStudentDescriptor.getName().isPresent()) {
            String prevStudentName = studentToEdit.getName().toString();
            System.out.println(prevStudentName);
            String newStudentName = editStudentDescriptor.getName().get().toString();
            System.out.println(newStudentName);
            List<Notes> lastShownNotes = model.getFilteredNotesList();
            for (int i = 0; i < lastShownNotes.size(); i++) {
                if (lastShownNotes.get(i).getStudent().equals(prevStudentName)) {
                    Notes noteToEdit = lastShownNotes.get(i);
                    EditNotesDescriptor editNotesDescriptor = new EditNotesDescriptor();
                    editNotesDescriptor.setStudent(newStudentName);
                    editNotesDescriptor.setContent(noteToEdit.getContent());
                    editNotesDescriptor.setPriority(noteToEdit.getPriority());
                    Notes editedNote = createEditedNote(noteToEdit, editNotesDescriptor);
                    model.setNote(noteToEdit, editedNote);
                }
            }
            model.updateFilteredNotesList(PREDICATE_SHOW_ALL_NOTES);
        }

        model.updateStudentToAssessments(studentToEdit.getName().fullName, editedStudent.getName().fullName);
        return new CommandResult(String.format(MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent));
    }

    /**
     * Creates and returns a {@code Student} with the details of {@code studentToEdit}
     * edited with {@code editStudentDescriptor}.
     */
    private static Student createEditedStudent(Student studentToEdit, EditStudentDescriptor editStudentDescriptor) {
        assert studentToEdit != null;

        Name updatedName = editStudentDescriptor.getName().orElse(studentToEdit.getName());
        Phone updatedPhone = editStudentDescriptor.getPhone().orElse(studentToEdit.getPhone());
        Email updatedEmail = editStudentDescriptor.getEmail().orElse(studentToEdit.getEmail());
        Address updatedAddress = editStudentDescriptor.getAddress().orElse(studentToEdit.getAddress());
        Temperature updatedTemperature = editStudentDescriptor.getTemperature().orElse(studentToEdit.getTemperature());
        Attendance updatedAttendance = editStudentDescriptor.getAttendance().orElse(studentToEdit.getAttendance());
        Set<Tag> updatedTags = editStudentDescriptor.getTags().orElse(studentToEdit.getTags());
        NextOfKin updatedNok = editStudentDescriptor.getNok().orElse(studentToEdit.getNok());

        return new Student(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTemperature,
                updatedAttendance, updatedNok, updatedTags);
    }

    /**
     * Creates and returns a {@code Notes} with the details of {@code noteToEdit}
     * edited with {@code editNotesDescriptor}.
     */
    private static Notes createEditedNote(Notes noteToEdit, EditNotesDescriptor editNotesDescriptor) {
        assert noteToEdit != null;

        String updatedStudent = editNotesDescriptor.getStudent().orElse(noteToEdit.getStudent());
        String updatedContent = editNotesDescriptor.getContent().orElse(noteToEdit.getContent());
        String updatedPriority = editNotesDescriptor.getPriority().orElse(noteToEdit.getPriority());
        String unchangedDateTime = noteToEdit.getDateTime();

        return new Notes(updatedStudent, updatedContent, updatedPriority, unchangedDateTime);
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentEditCommand)) {
            return false;
        }

        // state check
        StudentEditCommand e = (StudentEditCommand) other;
        return index.equals(e.index)
                && editStudentDescriptor.equals(e.editStudentDescriptor);
    }

    /**
     * Stores the details to edit the student with. Each non-empty field value will replace the
     * corresponding field value of the student.
     */
    public static class EditStudentDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Temperature temperature;
        private Attendance attendance;
        private Set<Tag> tags;
        private NextOfKin nok;

        public EditStudentDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditStudentDescriptor(EditStudentDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTemperature(toCopy.temperature);
            setAttendance(toCopy.attendance);
            setTags(toCopy.tags);
            setNok(toCopy.nok);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, nok, temperature, attendance, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setTemperature(Temperature temperature) {
            this.temperature = temperature;
        }

        public Optional<NextOfKin> getNok() {
            return Optional.ofNullable(nok);
        }

        public void setNok(NextOfKin nok) {
            this.nok = nok;
        }

        public Optional<Temperature> getTemperature() {
            return Optional.ofNullable(temperature);
        }

        public void setAttendance(Attendance attendance) {
            this.attendance = attendance;
        }

        public Optional<Attendance> getAttendance() {
            return Optional.ofNullable(attendance);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStudentDescriptor)) {
                return false;
            }

            // state check
            EditStudentDescriptor e = (EditStudentDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getTemperature().equals(e.getTemperature())
                    && getAttendance().equals(e.getAttendance())
                    && getTags().equals(e.getTags());
        }
    }
}
