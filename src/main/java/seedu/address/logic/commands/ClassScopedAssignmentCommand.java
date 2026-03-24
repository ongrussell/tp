package seedu.address.logic.commands;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Shared logic for assignment commands that operate within the active group context.
 */
abstract class ClassScopedAssignmentCommand extends Command {

    static final String MESSAGE_REQUIRE_ACTIVE_GROUP =
            "Assignment commands can only be used when viewing a specific group.";
    static final String MESSAGE_ASSIGNMENT_NOT_FOUND = "This assignment does not exist in the current group.";
    static final String MESSAGE_DUPLICATE_ASSIGNMENT =
            "An assignment with that name already exists in the current group.";
    static final String MESSAGE_INVALID_MAX_MARKS_FOR_EXISTING_GRADES =
            "New max marks cannot be lower than an existing grade for this assignment.";

    protected Group getActiveGroup(Model model) throws CommandException {
        GroupName activeGroupName = model.getActiveGroupName()
                .orElseThrow(() -> new CommandException(MESSAGE_REQUIRE_ACTIVE_GROUP));
        return model.findGroupByName(activeGroupName)
                .orElseThrow(() -> new CommandException(MESSAGE_REQUIRE_ACTIVE_GROUP));
    }

    protected Assignment getRequiredAssignment(Group group, AssignmentName assignmentName)
            throws CommandException {
        return group.findAssignmentByName(assignmentName)
                .orElseThrow(() -> new CommandException(MESSAGE_ASSIGNMENT_NOT_FOUND));
    }

    protected List<Person> getStudentsInClass(Model model, GroupName groupName) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.hasGroup(groupName))
                .toList();
    }
}
