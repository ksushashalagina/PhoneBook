package com.phonebook.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса Subscriber.
 */
class SubscriberTest {

    private Subscriber subscriber;

    @BeforeEach
    void setUp() {
        subscriber = new Subscriber("Smith", "John", "David");
    }

    @Test
    void testSubscriberCreation() {
        assertNotNull(subscriber.getId());
        assertEquals("Smith", subscriber.getLastName());
        assertEquals("John", subscriber.getFirstName());
        assertEquals("David", subscriber.getMiddleName());
        assertEquals("Smith John David", subscriber.getFullName());
        assertTrue(subscriber.isValid());
    }

    @Test
    void testPhoneNumberManagement() {
        PhoneNumber phone1 = new PhoneNumber("+1234567890", PhoneType.MOBILE);
        PhoneNumber phone2 = new PhoneNumber("0987654321", PhoneType.HOME);

        assertTrue(subscriber.addPhoneNumber(phone1));
        assertFalse(subscriber.addPhoneNumber(phone1)); // Дубликат
        assertTrue(subscriber.addPhoneNumber(phone2));

        List<PhoneNumber> phones = subscriber.getPhoneNumbers();
        assertEquals(2, phones.size());
        assertTrue(phones.contains(phone1));
        assertTrue(phones.contains(phone2));

        assertTrue(subscriber.removePhoneNumber(phone1));
        assertEquals(1, subscriber.getPhoneNumbers().size());
        assertFalse(subscriber.removePhoneNumber(phone1)); // Уже удален
    }

    @Test
    void testContainsSearch() {
        PhoneNumber phone = new PhoneNumber("1234567890", PhoneType.MOBILE);
        subscriber.addPhoneNumber(phone);

        // Поиск по имени (регистр не важен)
        assertTrue(subscriber.contains("Smith"));
        assertTrue(subscriber.contains("smith"));
        assertTrue(subscriber.contains("SMITH"));

        assertTrue(subscriber.contains("John"));
        assertTrue(subscriber.contains("john"));

        assertTrue(subscriber.contains("David"));
        assertTrue(subscriber.contains("david"));

        // Поиск по полному имени
        assertTrue(subscriber.contains("Smith John"));
        assertTrue(subscriber.contains("smith john"));

        // Поиск по номеру телефона
        assertTrue(subscriber.contains("1234567890"));
        assertTrue(subscriber.contains("123456"));
        assertTrue(subscriber.contains("4567890"));

        // Поиск по типу телефона (разные варианты)
        assertTrue(subscriber.contains("Mobile")); // Display name
        assertTrue(subscriber.contains("mobile")); // нижний регистр
        assertTrue(subscriber.contains("MOBILE")); // верхний регистр
        assertTrue(subscriber.contains("MOB")); // часть слова

        // Негативные проверки
        assertFalse(subscriber.contains("Nonexistent"));
        assertFalse(subscriber.contains("9999999999"));
        assertFalse(subscriber.contains("Jones")); // Другая фамилия
    }

    @Test
    void testEmptySearch() {
        // Пустая строка поиска должна возвращать true
        assertTrue(subscriber.contains(""));
        assertTrue(subscriber.contains(" "));
        assertTrue(subscriber.contains("   "));
        assertTrue(subscriber.contains(null));
    }

    @Test
    void testSearchByPhoneType() {
        PhoneNumber mobilePhone = new PhoneNumber("1234567890", PhoneType.MOBILE);
        PhoneNumber homePhone = new PhoneNumber("0987654321", PhoneType.HOME);
        PhoneNumber workPhone = new PhoneNumber("5551234567", PhoneType.WORK);

        subscriber.addPhoneNumber(mobilePhone);
        subscriber.addPhoneNumber(homePhone);
        subscriber.addPhoneNumber(workPhone);

        // Поиск по типу телефона - разные варианты написания
        assertTrue(subscriber.contains("Mobile"));
        assertTrue(subscriber.contains("mobile"));
        assertTrue(subscriber.contains("MOBILE"));

        assertTrue(subscriber.contains("Home"));
        assertTrue(subscriber.contains("home"));
        assertTrue(subscriber.contains("HOME"));

        assertTrue(subscriber.contains("Work"));
        assertTrue(subscriber.contains("work"));
        assertTrue(subscriber.contains("WORK"));

        // Поиск по enum name тоже должен работать
        assertTrue(subscriber.contains("MOBILE")); // enum name
        assertTrue(subscriber.contains("HOME"));   // enum name
        assertTrue(subscriber.contains("WORK"));   // enum name
    }

    @Test
    void testSearchPartialMatches() {
        PhoneNumber phone = new PhoneNumber("+1-234-567-8900", PhoneType.MOBILE);
        subscriber.addPhoneNumber(phone);

        // Частичные совпадения должны работать
        assertTrue(subscriber.contains("234-567"));
        assertTrue(subscriber.contains("567-8900"));
        assertTrue(subscriber.contains("+1"));
        assertTrue(subscriber.contains("8900"));

        // Поиск по части типа
        assertTrue(subscriber.contains("Mob")); // начало слова
        assertTrue(subscriber.contains("ile")); // конец слова
        assertTrue(subscriber.contains("bil")); // середина слова
    }

    @Test
    void testInvalidSubscriber() {
        Subscriber invalid1 = new Subscriber("", "John", "David");
        Subscriber invalid2 = new Subscriber("Smith", "", "David");
        Subscriber invalid3 = new Subscriber("Smith", "John", null);

        assertFalse(invalid1.isValid());
        assertFalse(invalid2.isValid());
        assertTrue(invalid3.isValid()); // Отчество может быть null
    }

    @Test
    void testCompareTo() {
        Subscriber subscriber1 = new Subscriber("Smith", "John", "A");
        Subscriber subscriber2 = new Subscriber("Smith", "John", "B");
        Subscriber subscriber3 = new Subscriber("Adams", "John", "C");

        assertTrue(subscriber1.compareTo(subscriber2) < 0);
        assertTrue(subscriber2.compareTo(subscriber1) > 0);
        assertTrue(subscriber3.compareTo(subscriber1) < 0);

        // Создаем нового абонента с теми же данными для сравнения
        Subscriber sameAs1 = new Subscriber("Smith", "John", "A");
        // Они разные объекты, но compareTo должен вернуть 0 при одинаковых ФИО
        assertEquals(0, subscriber1.compareTo(sameAs1));
    }

    @Test
    void testEqualsAndHashCode() {
        Subscriber subscriber1 = new Subscriber("Smith", "John", "David");
        Subscriber subscriber2 = new Subscriber("Smith", "John", "David");

        // Два разных объекта с разными ID
        assertNotEquals(subscriber1, subscriber2);
        assertNotEquals(subscriber1.hashCode(), subscriber2.hashCode());

        // Проверка equals с null и другим классом
        assertNotEquals(subscriber1, null);
        assertNotEquals(subscriber1, "string");

        // Проверка на один и тот же объект
        assertEquals(subscriber1, subscriber1);
    }
}