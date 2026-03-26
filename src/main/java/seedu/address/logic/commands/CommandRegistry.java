package seedu.address.logic.commands;

import java.util.List;

/**
 * Central registry of all command words.
 */
public class CommandRegistry {

    public static final List<String> COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            AddSessionCommand.COMMAND_WORD,
            AddToGroupCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            CreateAssignmentCommand.COMMAND_WORD,
            CreateGroupCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            DeleteAssignmentCommand.COMMAND_WORD,
            DeleteGroupCommand.COMMAND_WORD,
            DeleteSessionCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            EditAssignmentCommand.COMMAND_WORD,
            EditSessionCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            ExportViewCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            GradeAssignmentCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ListAssignmentsCommand.COMMAND_WORD,
            ListGroupsCommand.COMMAND_WORD,
            MarkCommand.COMMAND_WORD,
            PartCommand.COMMAND_WORD,
            RemoveFromGroupCommand.COMMAND_WORD,
            RenameGroupCommand.COMMAND_WORD,
            SwitchGroupCommand.COMMAND_WORD,
            UndoSessionCommand.COMMAND_WORD,
            UnmarkCommand.COMMAND_WORD,
            ViewCommand.COMMAND_WORD
    );

    private CommandRegistry() {} // prevent instantiation
}
