package seedu.address.model.classspace;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.classspace.exceptions.ClassSpaceNotFoundException;
import seedu.address.model.classspace.exceptions.DuplicateClassSpaceException;

/**
 * A list of class spaces that enforces uniqueness between its elements and does not allow nulls.
 */
public class UniqueClassSpaceList implements Iterable<ClassSpace> {

    private final ObservableList<ClassSpace> internalList = FXCollections.observableArrayList();
    private final ObservableList<ClassSpace> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent class space as the given argument.
     */
    public boolean contains(ClassSpace toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameClassSpace);
    }

    /**
     * Adds a class space to the list.
     */
    public void add(ClassSpace toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateClassSpaceException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the class space {@code target} in the list with {@code editedClassSpace}.
     */
    public void setClassSpace(ClassSpace target, ClassSpace editedClassSpace) {
        requireAllNonNull(target, editedClassSpace);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ClassSpaceNotFoundException();
        }

        if (!target.isSameClassSpace(editedClassSpace) && contains(editedClassSpace)) {
            throw new DuplicateClassSpaceException();
        }

        internalList.set(index, editedClassSpace);
    }

    /**
     * Removes the equivalent class space from the list.
     */
    public void remove(ClassSpace toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new ClassSpaceNotFoundException();
        }
    }

    public void setClassSpaces(List<ClassSpace> classSpaces) {
        requireAllNonNull(classSpaces);
        if (!classSpacesAreUnique(classSpaces)) {
            throw new DuplicateClassSpaceException();
        }

        internalList.setAll(classSpaces);
    }

    public ObservableList<ClassSpace> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<ClassSpace> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueClassSpaceList)) {
            return false;
        }

        UniqueClassSpaceList otherUniqueClassSpaceList = (UniqueClassSpaceList) other;
        return internalList.equals(otherUniqueClassSpaceList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean classSpacesAreUnique(List<ClassSpace> classSpaces) {
        for (int i = 0; i < classSpaces.size() - 1; i++) {
            for (int j = i + 1; j < classSpaces.size(); j++) {
                if (classSpaces.get(i).isSameClassSpace(classSpaces.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
