package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void setUpJavaFx() {
        Platform.startup(() -> {
        });
    }


    @Test
    public void constructor_personCardCreated_returnsNotNull() {
        Person testPerson = new PersonBuilder()
                .withName("Leon")
                .withPhone("12345678")
                .withEmail("leon@example.com")
                .withMatricNumber("A1234567P")
                .build();
        PersonCard card = new PersonCard(testPerson, 1);
        assertNotNull(card);
    }

    @Test
    public void display_personWithCorrectDetails_returnsTrue() {
        Person testPerson = new PersonBuilder().build();
        PersonCard card = new PersonCard(testPerson, 1);
        assertEquals(testPerson, card.person);
    }
}

