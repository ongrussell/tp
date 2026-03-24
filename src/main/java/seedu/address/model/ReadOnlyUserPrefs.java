package seedu.address.model;

import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    Path getAddressBookFilePath();

    /**
     * Returns the last active group name from a previous session, if any.
     */
    Optional<String> getLastActiveGroupName();

    /**
     * Returns the last active session date (yyyy-MM-dd) from a previous session, if any.
     */
    Optional<String> getLastActiveSessionDate();

    /**
     * Returns whether attendance view was active in the previous session.
     */
    boolean isAttendanceViewActive();

}
