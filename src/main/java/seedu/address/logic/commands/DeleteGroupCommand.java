package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Deletes a group.
 */
public class DeleteGroupCommand extends Command {

    public static final String COMMAND_WORD = "deletegroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a group.\n"
            + "Parameters: g/GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Deleted group: %1$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";

    private final GroupName groupName;

    public DeleteGroupCommand(GroupName groupName) {
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.findGroupByName(groupName)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(groupName)) {
                continue;
            }
            Person updatedPerson = person.withoutGroupData(groupName);
            model.setPerson(person, updatedPerson);
        }

        model.deleteGroup(group);
        return new CommandResult(String.format(MESSAGE_SUCCESS, groupName.value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteGroupCommand)) {
            return false;
        }
        DeleteGroupCommand otherCommand = (DeleteGroupCommand) other;
        return groupName.equals(otherCommand.groupName);
    }
}
