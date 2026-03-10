package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the class space list.
     * This list will not contain any duplicate class spaces.
     */
    ObservableList<ClassSpace> getClassSpaceList();

}
