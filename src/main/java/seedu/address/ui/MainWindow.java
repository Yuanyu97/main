package seedu.address.ui;

import static seedu.address.commons.core.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.academics.AcademicsExportCommand;
import seedu.address.logic.commands.admin.AdminFetchCommand;
import seedu.address.logic.commands.admin.AdminSaveCommand;
import seedu.address.logic.commands.event.EventExportCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.notes.NotesExportCommand;
import seedu.address.logic.commands.student.DefaultStudentDisplayCommand;
import seedu.address.logic.commands.student.DetailedStudentDisplayCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.admin.exceptions.DateNotFoundException;
import seedu.address.model.admin.exceptions.DuplicateDateException;
import seedu.address.model.event.EventScheduleView;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.ui.academics.AcademicsExporter;
import seedu.address.ui.academics.AcademicsPanel;
import seedu.address.ui.admin.DateListPanel;
import seedu.address.ui.event.EventExporter;
import seedu.address.ui.event.SchedulePage;
import seedu.address.ui.event.SchedulePanel;
import seedu.address.ui.notes.NotesExporter;
import seedu.address.ui.notes.NotesPanel;
import seedu.address.ui.teapet.StudentListPanel;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static ResultDisplay resultDisplay;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private StudentListPanel studentListPanel;
    private AcademicsPanel academicsPanel;
    private AcademicsPanel academicsHomeworkPanel;
    private AcademicsPanel academicsExamPanel;
    private AcademicsPanel academicsReportPanel;
    private DateListPanel dateListPanel;
    private NotesPanel notesPanel;
    private SchedulePage schedulePage;
    private SchedulePanel schedulePanel;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane mainPanelPlaceholder;

    @FXML
    private StackPane notesPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private Menu studentList;
    @FXML
    private Menu studentAdmin;
    @FXML
    private Menu studentAcademics;
    @FXML
    private Menu personalSchedule;

    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
        schedulePage = new SchedulePage();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {

        schedulePanel = new SchedulePanel(logic.getVEvents());
        mainPanelPlaceholder.getChildren().add(schedulePanel.getRoot());

        academicsPanel = new AcademicsPanel(logic.getFilteredAcademicsList());
        mainPanelPlaceholder.getChildren().add(academicsPanel.getRoot());

        academicsHomeworkPanel = new AcademicsPanel(logic.getHomeworkList());
        mainPanelPlaceholder.getChildren().add(academicsHomeworkPanel.getRoot());

        academicsExamPanel = new AcademicsPanel(logic.getExamList());
        mainPanelPlaceholder.getChildren().add(academicsExamPanel.getRoot());

        dateListPanel = new DateListPanel(logic.getFilteredDateList());
        mainPanelPlaceholder.getChildren().add(dateListPanel.getRoot());

        notesPanel = new NotesPanel(logic.getFilteredNotesList());
        notesPanelPlaceholder.getChildren().add(notesPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getTeaPetFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        studentListPanel = new StudentListPanel(logic.getFilteredStudentList());
        mainPanelPlaceholder.getChildren().add(studentListPanel.getRoot());

        onStudentList();
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     *  Handles the Personal Schedule based on the view mode.
     */
    @FXML
    public void handleSchedule() {
        schedulePanel.update();
        if (logic.getEventScheduleView().equals(EventScheduleView.DAILY)) {
            schedulePanel.setDaily();
        } else if (logic.getEventScheduleView().equals(EventScheduleView.WEEKLY)) {
            schedulePanel.setWeekly();
        }
        schedulePanel.setDisplayedDateTime(logic.getEventScheduleLocalDateTime());
        schedulePanel.getRoot().toFront();
        onSchedule();
    }

    /**
     *  Opens the Personal Schedule Page
     */
    @FXML
    public void openWeeklySchedule() {
        schedulePanel.update();
        schedulePanel.setWeekly();
        schedulePanel.setDisplayedDateTime(logic.getEventScheduleLocalDateTime());
        schedulePanel.getRoot().toFront();
        onSchedule();
    }

    void show() {
        primaryStage.show();

    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public StudentListPanel getStudentListPanel() {
        return studentListPanel;
    }

    public DateListPanel getDateListPanel() {
        return dateListPanel;
    }

    public AcademicsPanel getAcademicsPanel() {
        return academicsPanel;
    }

    /**
     * Opens the student window or focuses on it if it's already opened.
     */
    @FXML
    public void handleStudentDetailed() {
        studentListPanel = new StudentListPanel(logic.getFilteredStudentList(), "detailed");
        mainPanelPlaceholder.getChildren().add(studentListPanel.getRoot());
        studentListPanel.getRoot().toFront();
        onStudentList();

    }

    /**
     * Opens the student window or focuses on it if it's already opened.
     */
    @FXML
    public void handleStudentAdmin() {
        studentListPanel = new StudentListPanel(logic.getFilteredStudentList(), "admin");
        mainPanelPlaceholder.getChildren().add(studentListPanel.getRoot());
        studentListPanel.getRoot().toFront();
        onStudentAdmin();
    }

    /**
     * Opens the student window or focuses on it if it's already opened.
     */
    @FXML
    public void handleStudentDefault() {
        studentListPanel = new StudentListPanel(logic.getFilteredStudentList());
        mainPanelPlaceholder.getChildren().add(studentListPanel.getRoot());
        studentListPanel.getRoot().toFront();
        onStudentList();
    }

    /**
     * Opens the date window or focuses on it if it's already opened.
     */
    @FXML
    public void handleDates() {
        dateListPanel = new DateListPanel(logic.getFilteredDateList());
        mainPanelPlaceholder.getChildren().add(dateListPanel.getRoot());
        dateListPanel.getRoot().toFront();
        onStudentAdmin();
    }

    /**
     * Opens the academics window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAcademics() {
        academicsPanel = new AcademicsPanel(logic.getFilteredAcademicsList());
        mainPanelPlaceholder.getChildren().add(academicsPanel.getRoot());
        academicsPanel.getRoot().toFront();
        onStudentAcademics();
    }

    /**
     * Opens the academics homework window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAcademicsHomework() {
        academicsHomeworkPanel = new AcademicsPanel(logic.getHomeworkList(), "homework");
        mainPanelPlaceholder.getChildren().add(academicsHomeworkPanel.getRoot());
        academicsHomeworkPanel.getRoot().toFront();
        onStudentAcademics();
    }

    /**
     * Opens the academics exam window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAcademicsExam() {
        academicsExamPanel = new AcademicsPanel(logic.getExamList(), "exam");
        mainPanelPlaceholder.getChildren().add(academicsExamPanel.getRoot());
        academicsExamPanel.getRoot().toFront();
        onStudentAcademics();
    }

    /**
     * Opens the academics report window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAcademicsReport() {
        academicsReportPanel = new AcademicsPanel(logic.getFilteredAcademicsList(), "report");
        mainPanelPlaceholder.getChildren().add(academicsReportPanel.getRoot());
        academicsReportPanel.getRoot().toFront();
        onStudentAcademics();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException,
            DateNotFoundException, DuplicateDateException, StudentNotFoundException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            String consoleReply = commandResult.getFeedbackToUser();
            logger.info("Result: " + consoleReply);
            resultDisplay.setFeedbackToUser(consoleReply);

            switch (consoleReply) {
            case DefaultStudentDisplayCommand.MESSAGE_SUCCESS:
                handleStudentDefault();
                break;
            case DetailedStudentDisplayCommand.MESSAGE_SUCCESS:
                handleStudentDetailed();
                break;
            case "List of dates with admin details of the class displayed!":
                handleDates();
                break;
            case "Academics now displays all HOMEWORK assessments":
                handleAcademicsHomework();
                break;
            case "Academics now displays all EXAM assessments":
                handleAcademicsExam();
                break;
            case "Academics now displays the report of each assessment.":
                handleAcademicsReport();
                break;
            case NotesExportCommand.MESSAGE_SUCCESS:
                NotesExporter notesExporter = new NotesExporter(logic.getFilteredNotesList());
                notesExporter.saveToCsv();
                break;
            case AcademicsExportCommand.MESSAGE_SUCCESS:
                AcademicsExporter academicsExporter = new AcademicsExporter(logic.getFilteredAcademicsList());
                academicsExporter.saveToCsv();
                break;
            case EventExportCommand.MESSAGE_SUCCESS:
                EventExporter eventExporter = new EventExporter(logic.getVEventHistory());
                eventExporter.saveToIcs();
                break;
            default:
                break;
            }

            if (consoleReply.contains("The Student list now displays last updated ADMIN details")) {
                handleStudentAdmin();
            }

            if (consoleReply.contains("New student added") || consoleReply.contains("Deleted Student")
                    || consoleReply.contains("students listed")) {
                handleStudentDefault();
            }

            if (consoleReply.contains("Academics submitted following submissions")
                    || consoleReply.contains("Academics marked following submissions")
                    || consoleReply.contains("Added assessment")
                    || consoleReply.contains("Edited Assessment")
                    || consoleReply.contains("The Academics tracks all your assessments and student submissions.")) {
                handleAcademics();
            }

            if (consoleReply.contains("Edited Student")) {
                handleStudentDefault();
            }

            if (consoleReply.contains("Admin list has been deleted for")) {
                handleDates();
            }

            if (consoleReply.contains("Class admin details for")) {
                studentListPanel = new StudentListPanel(FXCollections.observableArrayList(logic.getFilteredDateList()
                        .get(0).getStudents()), "admin");
                mainPanelPlaceholder.getChildren().add(studentListPanel.getRoot());
            }

            if (consoleReply.contains("This admin list has been saved for")) {
                handleDates();
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            if ((consoleReply.contains("Deleted Event:"))
                    || (consoleReply.contains("Added Event"))
                    || (consoleReply.contains("Successfully edited Event"))
                    || (consoleReply.contains("Event found:"))
                    || (consoleReply.contains("This is the closest event we can find based on what you've entered:"))
                    || (consoleReply.contains("on reference date"))) {
                handleSchedule();
            }

            if ((consoleReply.contains("This is your schedule for the week"))) {
                openWeeklySchedule();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        } catch (DateNotFoundException dnfe) {
            logger.info(AdminFetchCommand.MESSAGE_DATE_NOT_FOUND_ADMIN);
            resultDisplay.setFeedbackToUser(dnfe.getMessage());
            throw dnfe;
        } catch (DuplicateDateException dde) {
            logger.info(AdminSaveCommand.MESSAGE_DUPLICATE_DATE_ADMIN);
            resultDisplay.setFeedbackToUser(dde.getMessage());
            throw dde;
        } catch (StudentNotFoundException ssne) {
            logger.info(MESSAGE_STUDENT_NOT_FOUND);
            resultDisplay.setFeedbackToUser(ssne.getMessage());
            throw ssne;
        }
    }

    /**
     * Edits result display message
     * @param feedback edited display message
     */
    public static void editResultDisplay(String feedback) {
        resultDisplay.setFeedbackToUser(feedback);
    }

    /* -------- Section of commands which handles to GUI tab colours -------- */

    /**
     * Changes the studentList menu tab to orange, while the rest becomes white.
     */
    private void onStudentList() {
        studentAcademics.setStyle("-fx-background-color: derive(#white, 20%)");
        studentList.setStyle(" -fx-background-color: Orange");
        studentAdmin.setStyle(" -fx-background-color: derive(#white, 20%)");
        personalSchedule.setStyle(" -fx-background-color: derive(#white, 20%)");
    }

    /**
     * Changes the studentAdmin menu tab to orange, while the rest becomes white.
     */
    private void onStudentAdmin() {
        studentAcademics.setStyle("-fx-background-color: derive(#white, 20%)");
        studentList.setStyle(" -fx-background-color: derive(#white, 20%)");
        studentAdmin.setStyle(" -fx-background-color: Orange");
        personalSchedule.setStyle(" -fx-background-color: derive(#white, 20%)");
    }

    /**
     * Changes the studentAcademics menu tab to orange, while the rest becomes white.
     */
    private void onStudentAcademics() {
        studentAcademics.setStyle("-fx-background-color: Orange");
        studentList.setStyle(" -fx-background-color: derive(#white, 20%)");
        studentAdmin.setStyle(" -fx-background-color: derive(#white, 20%)");
        personalSchedule.setStyle(" -fx-background-color: derive(#white, 20%)");
    }

    /**
     * Changes the personalSchedule menu tab to orange, while the rest becomes white.
     */
    private void onSchedule() {
        studentAcademics.setStyle("-fx-background-color: derive(#white, 20%) ");
        studentList.setStyle(" -fx-background-color: derive(#white, 20%)");
        studentAdmin.setStyle(" -fx-background-color: derive(#white, 20%)");
        personalSchedule.setStyle(" -fx-background-color: Orange");
    }

    /* --------------------------- End of Section --------------------------- */

}
