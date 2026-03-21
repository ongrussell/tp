package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UiManagerTest {

    @Test
    public void buildStartUpMessage_noWarnings_returnsSuccessMessage() {
        // Create a stub that has exactly 5 contacts loaded.
        Logic logicStub = new LogicStub(5);
        UiManager uiManager = new UiManager(logicStub, List.of());

        String result = uiManager.buildStartUpMessage(List.of());

        assertEquals("5 contacts loaded successfully.", result);
    }

    @Test
    public void buildStartUpMessage_oneWarning_formatsSingularCorrectly() {
        // Create a stub that has exactly 1 contact loaded.
        Logic logicStub = new LogicStub(1);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of("Skipped invalid contact 'Alice'");
        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("1 contact loaded successfully."));
        assertTrue(result.contains("1 contact could not be loaded and was skipped:"));
        assertTrue(result.contains("1. Skipped invalid contact 'Alice'"));
    }

    @Test
    public void buildStartUpMessage_multipleWarnings_formatsPluralCorrectly() {
        // Create a stub that has 10 contacts loaded.
        Logic logicStub = new LogicStub(10);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "Skipped invalid contact 'Bob'",
                "Skipped duplicate contact 'Charlie'"
        );
        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("10 contacts loaded successfully."));
        assertTrue(result.contains("2 contacts could not be loaded and were skipped:"));
        assertTrue(result.contains("1. Skipped invalid contact 'Bob'"));
        assertTrue(result.contains("2. Skipped duplicate contact 'Charlie'"));
        assertTrue(result.contains("You can fix these entries directly in the save file: dummy.json"));
    }

    /**
     * A stub class to isolate UiManager string testing from the rest of the application.
     * It provides hardcoded, predictable data for testing.
     */
    private static class LogicStub implements Logic {
        private final int personCount;

        LogicStub(int personCount) {
            this.personCount = personCount;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            ObservableList<Person> list = FXCollections.observableArrayList();
            for (int i = 0; i < personCount; i++) {
                list.add(new PersonBuilder().build());
            }
            return list;
        }

        @Override
        public Path getAddressBookFilePath() {
            // Provide a fake file path
            return Paths.get("dummy.json");
        }

        // --- Other Logic methods omitted for brevity. We don't use them in the string builders! ---
        @Override
        public CommandResult execute(String commandText) {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public ReadOnlyStringProperty currentViewProperty() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public ReadOnlyObjectProperty<ClassSpaceName> activeClassSpaceNameProperty() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public ReadOnlyObjectProperty<LocalDate> activeSessionDateProperty() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public ReadOnlyBooleanProperty attendanceViewActiveProperty() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException("This method should not be called.");
        }
    }
}
