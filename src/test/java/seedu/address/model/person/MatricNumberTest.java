package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatricNumberTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatricNumber(null));
    }

    @Test
    public void constructor_invalidMatricNumber_throwsIllegalArgumentException() {
        String invalidMatricNumber = "";
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber(invalidMatricNumber));
    }

    @Test
    public void isValidMatricNumber() {
        // null matriculation number
        assertThrows(NullPointerException.class, () -> MatricNumber.isValidFormat(null));

        // invalid matriculation numbers
        assertFalse(MatricNumber.isValidFormat("")); // empty string
        assertFalse(MatricNumber.isValidFormat(" ")); // spaces only
        assertFalse(MatricNumber.isValidFormat("B1234567N")); //starts with `B`
        assertFalse(MatricNumber.isValidFormat("1234567")); //only numbers
        assertFalse(MatricNumber.isValidFormat("A12345678Z")); //has 8 digits
        assertFalse(MatricNumber.isValidFormat("A123456Z")); //has 6 digits
        assertFalse(MatricNumber.isValidFormat("A0N")); // 1 digit
        assertFalse(MatricNumber.isValidFormat("AZ")); //no digits
        assertFalse(MatricNumber.isValidFormat("A1234567N ")); // trailing space
        assertFalse(MatricNumber.isValidFormat(" A1234567N")); // leading space
        assertFalse(MatricNumber.isValidFormat("A1234 567N")); // space in the middle

        // valid matriculation numbers
        assertTrue(MatricNumber.isValidFormat("A4433221B"));
        assertTrue(MatricNumber.isValidFormat("A0000000A")); // all same digits
        assertTrue(MatricNumber.isValidFormat("A4455667A")); // ends with `A`
        assertTrue(MatricNumber.isValidFormat("a4455667Z")); // starts with lower capital `a`
        assertTrue(MatricNumber.isValidFormat("A4455667z")); // ends with lower capital
        assertTrue(MatricNumber.isValidFormat("a4455667z")); // characters are both in lower capital
    }

    @Test
    public void equals() {
        MatricNumber matricNumber = new MatricNumber("A1111111Z");

        // same values -> returns true
        assertTrue(matricNumber.equals(new MatricNumber("A1111111Z")));

        //different case -> returns true
        assertTrue(matricNumber.equals(new MatricNumber("a1111111z")));

        // same object -> returns true
        assertTrue(matricNumber.equals(matricNumber));

        // null -> returns false
        assertFalse(matricNumber.equals(null));

        // different types -> returns false
        assertFalse(matricNumber.equals(5.0f));

        // different values -> returns false
        assertFalse(matricNumber.equals(new MatricNumber("A1111111X")));
    }

    @Test
    public void hashCode_test() {
        String validMatricNumber = "A1234567X";
        MatricNumber matricNumber1 = new MatricNumber(validMatricNumber);
        MatricNumber matricNumber2 = new MatricNumber(validMatricNumber);
        assertEquals(matricNumber1, matricNumber2);
    }
}
