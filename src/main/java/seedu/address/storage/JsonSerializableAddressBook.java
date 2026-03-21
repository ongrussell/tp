package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {
    public static final String MESSAGE_DUPLICATE_CLASS_SPACE = "Class space list contains duplicate class space(s).";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedClassSpace> classSpaces = new ArrayList<>();
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and class spaces.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("classSpaces") List<JsonAdaptedClassSpace> classSpaces) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (classSpaces != null) {
            this.classSpaces.addAll(classSpaces);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        classSpaces.addAll(source.getClassSpaceList().stream()
                .map(JsonAdaptedClassSpace::new)
                .collect(Collectors.toList()));
    }

    /**
     * Returns an unmodifiable list of warnings accumulated during loading of app.
     *
     * @return A list of warnings describing issues with contacts during loading.
     */
    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        loadWarnings.clear();

        logger.info("Loading address book: " + classSpaces.size() + " class space(s), "
                + persons.size() + " person(s)");

        loadClassSpaces(addressBook);
        loadPersons(addressBook);

        logger.info("Address book loaded: " + addressBook.getPersonList().size()
                + " person(s) loaded, " + loadWarnings.size() + " skipped");

        return addressBook;
    }

    private void loadPersons(AddressBook addressBook) {
        for (int i = 0; i < persons.size(); i++) {
            loadPerson(addressBook, persons.get(i), i);
        }
    }

    private void loadPerson(AddressBook addressBook, JsonAdaptedPerson jsonAdaptedPerson, int index) {
        requireNonNull(addressBook);
        requireNonNull(jsonAdaptedPerson);
        assert index >= 0 : "Person index should never be negative";

        try {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                String identifier = person.getName().fullName + " (Matric: " + person.getMatricNumber().value + ")";
                logger.warning("Skipping duplicate contact at entry #" + (index + 1) + ": " + identifier);
                loadWarnings.add("Skipped duplicate contact: " + identifier);
                return;
            }
            ensureClassSpacesExist(addressBook, person);
            addressBook.addPerson(person);
        } catch (IllegalValueException ive) {
            String identifier = jsonAdaptedPerson.getName() != null
                    ? "'" + jsonAdaptedPerson.getName() + "'"
                    : "entry #" + (index + 1) + " (missing name)";
            logger.warning("Skipping invalid contact " + identifier + ": " + ive.getMessage());
            loadWarnings.add("Skipped invalid contact " + identifier + ": " + ive.getMessage());
        }
    }

    private void ensureClassSpacesExist(AddressBook addressBook, Person person) {
        for (var classSpaceName : person.getClassSpaces()) {
            ClassSpace classSpace = new ClassSpace(classSpaceName);
            if (!addressBook.hasClassSpace(classSpace)) {
                addressBook.addClassSpace(classSpace);
            }
        }
    }

    private void loadClassSpaces(AddressBook addressBook) throws IllegalValueException {
        for (JsonAdaptedClassSpace jsonAdaptedClassSpace : classSpaces) {
            ClassSpace classSpace = jsonAdaptedClassSpace.toModelType();
            if (addressBook.hasClassSpace(classSpace)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CLASS_SPACE);
            }
            addressBook.addClassSpace(classSpace);
        }
    }
}
