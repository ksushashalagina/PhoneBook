package com.phonebook.service;

import com.phonebook.model.PhoneNumber;
import com.phonebook.model.PhoneType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для валидатора телефонной книги.
 */
class PhoneBookValidatorTest {

    @Test
    void testValidateSubscriber() {
        // Валидные данные
        assertDoesNotThrow(() ->
                PhoneBookValidator.validateSubscriber("Smith", "John", "David"));

        assertDoesNotThrow(() ->
                PhoneBookValidator.validateSubscriber("Smith", "John", null));

        assertDoesNotThrow(() ->
                PhoneBookValidator.validateSubscriber("O'Connor", "Mary-Jane", "Anne"));

        // Невалидные данные
        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validateSubscriber("", "John", "David"));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validateSubscriber("Smith", "", "David"));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validateSubscriber("Smith123", "John", "David"));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validateSubscriber("Smith", "John@", "David"));
    }

    @Test
    void testValidatePhoneNumber() {
        // Валидные номера
        assertDoesNotThrow(() ->
                PhoneBookValidator.validatePhoneNumber("1234567890"));

        assertDoesNotThrow(() ->
                PhoneBookValidator.validatePhoneNumber("+1234567890"));

        assertDoesNotThrow(() ->
                PhoneBookValidator.validatePhoneNumber("(123) 456-7890"));

        assertDoesNotThrow(() ->
                PhoneBookValidator.validatePhoneNumber("+1-234-567-8900"));

        // Невалидные номера
        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validatePhoneNumber(""));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validatePhoneNumber("123"));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validatePhoneNumber("abc123"));

        assertThrows(IllegalArgumentException.class, () ->
                PhoneBookValidator.validatePhoneNumber("1234567890123456")); // Слишком длинный
    }

    @Test
    void testIsValidSubscriber() {
        assertTrue(PhoneBookValidator.isValidSubscriber(
                new com.phonebook.model.Subscriber("Smith", "John", "David")));

        assertFalse(PhoneBookValidator.isValidSubscriber(
                new com.phonebook.model.Subscriber("", "John", "David")));

        assertFalse(PhoneBookValidator.isValidSubscriber(null));
    }

    @Test
    void testIsValidPhoneNumber() {
        assertTrue(PhoneBookValidator.isValidPhoneNumber(
                new PhoneNumber("1234567890", PhoneType.MOBILE)));

        assertFalse(PhoneBookValidator.isValidPhoneNumber(
                new PhoneNumber("", PhoneType.MOBILE)));

        assertFalse(PhoneBookValidator.isValidPhoneNumber(
                new PhoneNumber("abc123", PhoneType.MOBILE)));

        assertFalse(PhoneBookValidator.isValidPhoneNumber(null));
    }
}