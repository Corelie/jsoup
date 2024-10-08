package org.jsoup.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidateTest {
    @Test
    public void testNotNull() {
        Validate.notNull("foo");
        boolean threw = false;
        try {
            Validate.notNull(null);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        Assertions.assertTrue(threw);
    }

    @Test void stacktraceFiltersOutValidateClass() {
        boolean threw = false;
        try {
            Validate.notNull(null);
        } catch (ValidationException e) {
            threw = true;
            assertEquals("Object must not be null", e.getMessage());
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement trace : stackTrace) {
                assertNotEquals(trace.getClassName(), Validate.class.getName());
            }
            assertTrue(stackTrace.length >= 1);
        }
        Assertions.assertTrue(threw);
    }

    @Test void nonnullParam() {
        boolean threw = true;
        try {
            Validate.notNullParam(null, "foo");
        } catch (ValidationException e) {
            assertEquals("The parameter 'foo' must not be null.", e.getMessage());
        }
        assertTrue(threw);
    }

@Test
/**
 * Teste les 2 fonctions notEmpty et la fonction notEmptyParam de Validate. Ces 3 fonctions font exactement la même chose,
 * ce pourquoi j'ai jugé bon de les réunir dans un même test. Les trois prennent en entrée un string vérifie s'il est vide
 * et si oui jettent une Exception. La seule différence est le message affiché, le premier notEmpty n'en affiche pas,
 * le second prends un string en entrée et l'affiche et notEmptyParam prends le nom du paramètre en entrée et l'affiche
 * dans le message.
 *
 * Le test vérifie que l'Exception est bien lancée et que le bon message est affiché si le string est vide ou null.
 * Il vérifie également qu'aucune exception n'est levée en cas de string valide.
 * @author Corélie Godefroid
 */
    public void testNotEmpty(){
        String test_null=null;
        String test_empty="";
        String test_ok="test";


        ValidationException exception1 = assertThrows(ValidationException.class, () -> {Validate.notEmpty(test_null,"null");});
        ValidationException exception2 = assertThrows(ValidationException.class, () -> {Validate.notEmpty(test_empty,"empty");});
        ValidationException exception3 = assertThrows(ValidationException.class, () -> {Validate.notEmptyParam(test_null,"nullParam");});
        ValidationException exception4 = assertThrows(ValidationException.class, () -> {Validate.notEmptyParam(test_empty,"emptyParam");});

        assertEquals("null", exception1.getMessage());
        assertEquals("empty", exception2.getMessage());
        assertEquals("The 'nullParam' parameter must not be empty.", exception3.getMessage());
        assertEquals("The 'emptyParam' parameter must not be empty.", exception4.getMessage());

        assertThrows(ValidationException.class, () -> {Validate.notEmpty(test_null);});
        assertThrows(ValidationException.class, () -> {Validate.notEmpty(test_empty);});

        assertDoesNotThrow(() -> {Validate.notEmpty(test_ok);});
        assertDoesNotThrow(() -> {Validate.notEmpty(test_ok,"ok");});
        assertDoesNotThrow(() -> {Validate.notEmptyParam(test_ok,"okParam");});

}
}
