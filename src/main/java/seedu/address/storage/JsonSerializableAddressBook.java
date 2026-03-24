package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_GROUP = "Group list contains duplicate group(s).";
    public static final String MESSAGE_INVALID_MATRICULATION_NUMBER = "Invalid matriculation number.";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonNode> persons = new ArrayList<>();
    private final List<JsonNode> preservedSkippedPersons = new ArrayList<>();
    private final List<JsonNode> groups = new ArrayList<>();
    private final List<JsonNode> preservedSkippedGroups = new ArrayList<>();
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and groups.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonNode> persons,
                                       @JsonProperty("groups") List<JsonNode> groups,
                                       @JsonProperty("preservedSkippedPersons") List<JsonNode> preservedSkippedPersons,
                                       @JsonProperty("preservedSkippedGroups") List<JsonNode>
                                                   preservedSkippedGroups,
                                       @JsonProperty("loadWarnings") List<String> loadWarnings) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (preservedSkippedPersons != null) {
            this.preservedSkippedPersons.addAll(preservedSkippedPersons);
        }
        if (preservedSkippedGroups != null) {
            this.preservedSkippedGroups.addAll(preservedSkippedGroups);
        }
        if (loadWarnings != null) {
            this.loadWarnings.addAll(loadWarnings);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this(source, List.of(), List.of(), List.of());
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * while preserving raw person entries that were skipped during loading.
     *
     * @param source Address book data to serialize.
     * @param preservedSkippedPersons Raw person JSON nodes that should be written back unchanged.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source, List<JsonNode> preservedSkippedPersons) {
        this(source, preservedSkippedPersons, List.of(), List.of());
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * while preserving raw person entries that were skipped during loading.
     *
     * @param source Address book data to serialize.
     * @param preservedSkippedPersons Raw person JSON nodes that should be written back unchanged.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source,
                                       List<JsonNode> preservedSkippedPersons,
                                       List<JsonNode> preservedSkippedGroups,
                                       List<String> preservedLoadWarnings) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        if (preservedSkippedPersons != null) {
            for (JsonNode skippedPerson : preservedSkippedPersons) {
                this.preservedSkippedPersons.add(skippedPerson.deepCopy());
            }
        }
        groups.addAll(source.getGroupList().stream()
                .map(JsonAdaptedGroup::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        if (preservedSkippedGroups != null) {
            for (JsonNode skippedGroup : preservedSkippedGroups) {
                this.preservedSkippedGroups.add(skippedGroup.deepCopy());
            }
        }
        if (preservedLoadWarnings != null) {
            this.loadWarnings.addAll(preservedLoadWarnings);
        }
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
     * Returns the raw skipped person entries that should be preserved on the next save.
     *
     * @return Unmodifiable list of skipped person JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedPersons() {
        return Collections.unmodifiableList(preservedSkippedPersons);
    }

    /**
     * Returns the raw skipped group entries that should be preserved on the next save.
     *
     * @return Unmodifiable list of skipped group JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedGroups() {
        return Collections.unmodifiableList(preservedSkippedGroups);
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        List<String> previousWarnings = new ArrayList<>(loadWarnings);
        loadWarnings.clear();

        logger.info("Loading address book: " + groups.size() + " group(s), "
                + persons.size() + " person(s)");

        loadGroups(addressBook);
        loadPersons(addressBook);
        loadWarnings.addAll(0, previousWarnings);

        logger.info("Address book loaded: " + addressBook.getPersonList().size()
                + " person(s) loaded, " + loadWarnings.size() + " skipped");

        return addressBook;
    }

    private void loadPersons(AddressBook addressBook) {
        requireNonNull(addressBook);
        for (int i = 0; i < persons.size(); i++) {
            loadPerson(addressBook, persons.get(i), i);
        }
    }

    private void loadPerson(AddressBook addressBook, JsonNode rawPersonNode, int index) {
        requireNonNull(addressBook);
        requireNonNull(rawPersonNode);
        assert index >= 0 : "Person index should never be negative";

        try {
            JsonAdaptedPerson jsonAdaptedPerson = JsonUtil.fromJsonNode(rawPersonNode, JsonAdaptedPerson.class);
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                skipDuplicatePerson(rawPersonNode, person, index);
                return;
            }
            ensureGroupsExist(addressBook, person);
            validateAssignmentGrades(addressBook, person);
            validateGroupSessions(addressBook, person);
            addressBook.addPerson(person);
        } catch (IllegalValueException | JsonProcessingException e) {
            skipInvalidPerson(rawPersonNode, index, e.getMessage());
        }
    }

    private void skipDuplicatePerson(JsonNode rawPersonNode, Person person, int index) {
        String identifier = person.getName().fullName + " (Matric: " + person.getMatricNumber().value + ")";
        logger.warning("Skipping duplicate contact at entry #" + (index + 1) + ": " + identifier);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add("Skipped duplicate contact: " + identifier);
    }

    private void skipInvalidPerson(JsonNode rawPersonNode, int index, String errorMessage) {
        String identifier = getRawPersonIdentifier(rawPersonNode, index);
        String formattedWarning = formatInvalidContactWarning(identifier, errorMessage);
        logger.warning(formattedWarning);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add(formattedWarning);
    }

    private void validateAssignmentGrades(AddressBook addressBook, Person person) throws IllegalValueException {
        for (var groupEntry : person.getAssignmentGrades().entrySet()) {
            GroupName groupName = groupEntry.getKey();
            Map<AssignmentName, Integer> grades = groupEntry.getValue();

            validatePersonIsMemberOfGroup(person, groupName);
            /*
            This should never be reached as ensureGroupsExist guarantees the group
            exists before this method is called. The orElseThrow is a defensive guard against
            future errors in the load sequence.
             */
            Group group = addressBook.getGroupList().stream()
                    .filter(cs -> cs.getGroupName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() ->
                            new AssertionError("Group '" + groupName.value
                                    + "' should exist after ensureGroupsExist"));
            validateGradesAgainstGroup(group, groupName, grades);
        }
    }

    private void validatePersonIsMemberOfGroup(Person person, GroupName groupName)
            throws IllegalValueException {
        if (!person.getGroups().contains(groupName)) {
            throw new IllegalValueException(String.format(
                    "Person has grades for group '%s' but is not a member of it.",
                    groupName.value));
        }
    }

    private void validateGradesAgainstGroup(Group group, GroupName groupName,
                                                 Map<AssignmentName, Integer> grades) throws IllegalValueException {
        for (var gradeEntry : grades.entrySet()) {
            AssignmentName assignmentName = gradeEntry.getKey();
            int grade = gradeEntry.getValue();

            if (!group.hasAssignment(assignmentName)) {
                throw new IllegalValueException(String.format(
                        "Person has a grade for assignment '%s' in group '%s',"
                                + "but that assignment does not exist.",
                        assignmentName.value, groupName.value));
            }

            Assignment assignment = group.findAssignmentByName(assignmentName).get();
            if (grade > assignment.getMaxMarks()) {
                throw new IllegalValueException(String.format(
                        "Grade %d for assignment '%s' in group '%s' exceeds max marks of %d.",
                        grade, assignmentName.value, groupName.value, assignment.getMaxMarks()));
            }
        }
    }

    private void validateGroupSessions(AddressBook addressBook, Person person) throws IllegalValueException {
        for (GroupName groupName : person.getGroupSessions().keySet()) {
            if (!person.getGroups().contains(groupName)) {
                throw new IllegalValueException(String.format(
                        "Person has sessions for group '%s' but is not a member of it.",
                        groupName.value));
            }
        }
    }

    private String getRawPersonIdentifier(JsonNode rawPersonNode, int index) {
        JsonNode nameNode = rawPersonNode.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            return "'" + nameNode.asText() + "'";
        }
        return "entry #" + (index + 1) + " (missing name)";
    }

    private void ensureGroupsExist(AddressBook addressBook, Person person) {
        for (var groupName : person.getGroups()) {
            Group group = new Group(groupName);
            if (!addressBook.hasGroup(group)) {
                addressBook.addGroup(group);
            }
        }
    }

    private void loadGroups(AddressBook addressBook) {
        requireNonNull(addressBook);
        for (int i = 0; i < groups.size(); i++) {
            loadGroup(addressBook, groups.get(i), i);
        }
    }

    private void loadGroup(AddressBook addressBook, JsonNode rawGroupNode, int index) {
        requireNonNull(addressBook);
        requireNonNull(rawGroupNode);
        assert index >= 0 : "Group index should never be negative";

        try {
            JsonAdaptedGroup jsonAdaptedGroup =
                    JsonUtil.fromJsonNode(rawGroupNode, JsonAdaptedGroup.class);
            Group group = jsonAdaptedGroup.toModelType();

            if (addressBook.hasGroup(group)) {
                String identifier = "'" + group.getGroupName().value + "'";
                logger.warning("Skipping duplicate group at entry #" + (index + 1) + ": " + identifier);
                preservedSkippedGroups.add(rawGroupNode.deepCopy());
                loadWarnings.add("Skipped duplicate group: " + identifier);
                return;
            }

            addressBook.addGroup(group);
        } catch (IllegalValueException | JsonProcessingException e) {
            String identifier = getRawGroupIdentifier(rawGroupNode, index);
            String formattedWarning = formatInvalidGroupWarning(identifier, e.getMessage());
            logger.warning(formattedWarning);
            preservedSkippedGroups.add(rawGroupNode.deepCopy());
            loadWarnings.add(formattedWarning);
        }
    }

    private String getRawGroupIdentifier(JsonNode rawGroupNode, int index) {
        JsonNode nameNode = rawGroupNode.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            return "'" + nameNode.asText() + "'";
        }
        return "entry #" + (index + 1) + " (missing name)";
    }


    private String formatInvalidContactWarning(String identifier, String errorMessage) {
        String[] errors = errorMessage.split(";\\s*");

        StringBuilder sb = new StringBuilder("Skipped invalid contact ")
                .append(identifier)
                .append(":\n");

        for (String error : errors) {
            sb.append("- ").append(error).append("\n");
        }

        return sb.toString().trim();
    }

    private String formatInvalidGroupWarning(String identifier, String errorMessage) {
        String[] errors = errorMessage.split(";\\s*");

        StringBuilder sb = new StringBuilder("Skipped invalid group ")
                .append(identifier)
                .append(":\n");

        for (String error : errors) {
            sb.append("- ").append(error).append("\n");
        }

        return sb.toString().trim();
    }

}
