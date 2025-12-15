package com.phonebook.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса PhoneNumber.
 */
class PhoneNumberTest {

    @Test
    void testPhoneNumberCreation() {
        PhoneNumber phone = new PhoneNumber("+1234567890", PhoneType.MOBILE);

        assertEquals("+1234567890", phone.getNumber());
        assertEquals(PhoneType.MOBILE, phone.getType());
        assertEquals("+1234567890 (Mobile)", phone.getFormatted());
        assertTrue(phone.isValid());
    }

    @Test
    void testContainsSearch() {
        PhoneNumber phone = new PhoneNumber("+1234567890", PhoneType.MOBILE);

        assertTrue(phone.contains("1234567890"));
        assertTrue(phone.contains("456"));
        assertTrue(phone.contains("+123"));

        assertFalse(phone.contains("999"));
        assertFalse(phone.contains("home"));
    }

    @Test
    void testValidity() {
        assertTrue(new PhoneNumber("1234567890", PhoneType.MOBILE).isValid());
        assertTrue(new PhoneNumber("+1 (234) 567-890", PhoneType.HOME).isValid());
        assertTrue(new PhoneNumber("123-456-7890", PhoneType.WORK).isValid());

        assertFalse(new PhoneNumber("", PhoneType.MOBILE).isValid());
        assertFalse(new PhoneNumber(null, PhoneType.MOBILE).isValid());
        assertFalse(new PhoneNumber("abc123", PhoneType.MOBILE).isValid());
    }

    @Test
    void testEqualsAndHashCode() {
        PhoneNumber phone1 = new PhoneNumber("1234567890", PhoneType.MOBILE);
        PhoneNumber phone2 = new PhoneNumber("1234567890", PhoneType.MOBILE);
        PhoneNumber phone3 = new PhoneNumber("0987654321", PhoneType.MOBILE);
        PhoneNumber phone4 = new PhoneNumber("1234567890", PhoneType.HOME);

        assertEquals(phone1, phone2);
        assertNotEquals(phone1, phone3);
        assertNotEquals(phone1, phone4);
        assertNotEquals(phone1, null);
        assertNotEquals(phone1, "string");

        assertEquals(phone1.hashCode(), phone2.hashCode());
        assertNotEquals(phone1.hashCode(), phone3.hashCode());
    }
}