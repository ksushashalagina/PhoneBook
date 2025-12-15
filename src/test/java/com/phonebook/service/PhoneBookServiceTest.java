package com.phonebook.service;

import com.phonebook.model.Subscriber;
import com.phonebook.model.PhoneNumber;
import com.phonebook.model.PhoneType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сервиса телефонной книги.
 */
class PhoneBookServiceTest {

    @TempDir
    Path tempDir;

    private PhoneBookService phoneBookService;
    private String testDataFile;

    @BeforeEach
    void setUp() {
        testDataFile = tempDir.resolve("test_phonebook.dat").toString();
        phoneBookService = new PhoneBookService(testDataFile);
    }

    @Test
    void testAddSubscriber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");

        assertNotNull(subscriber);
        assertEquals("Smith", subscriber.getLastName());
        assertEquals("John", subscriber.getFirstName());
        assertEquals("David", subscriber.getMiddleName());
        assertEquals(1, phoneBookService.getSubscriberCount());

        // Проверка дублирования
        Subscriber duplicate = phoneBookService.addSubscriber("Smith", "John", "David");
        assertNotNull(duplicate);
        assertEquals(2, phoneBookService.getSubscriberCount()); // Должны быть разные абоненты
    }

    @Test
    void testAddInvalidSubscriber() {
        // Пустая фамилия
        Subscriber invalid1 = phoneBookService.addSubscriber("", "John", "David");
        assertNull(invalid1);
        assertEquals(0, phoneBookService.getSubscriberCount());

        // Пустое имя
        Subscriber invalid2 = phoneBookService.addSubscriber("Smith", "", "David");
        assertNull(invalid2);
        assertEquals(0, phoneBookService.getSubscriberCount());

        // Некорректные символы в имени
        Subscriber invalid3 = phoneBookService.addSubscriber("Smith123", "John", "David");
        assertNull(invalid3);
        assertEquals(0, phoneBookService.getSubscriberCount());
    }

    @Test
    void testUpdateSubscriber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        assertNotNull(subscriber);

        boolean updated = phoneBookService.updateSubscriber(subscriber, "Johnson", "James", "Robert");
        assertTrue(updated);

        assertEquals("Johnson", subscriber.getLastName());
        assertEquals("James", subscriber.getFirstName());
        assertEquals("Robert", subscriber.getMiddleName());
    }

    @Test
    void testDeleteSubscriber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        assertEquals(1, phoneBookService.getSubscriberCount());

        boolean deleted = phoneBookService.deleteSubscriber(subscriber);
        assertTrue(deleted);
        assertEquals(0, phoneBookService.getSubscriberCount());

        // Удаление несуществующего абонента
        boolean notDeleted = phoneBookService.deleteSubscriber(subscriber);
        assertFalse(notDeleted);
    }

    @Test
    void testAddPhoneNumber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        assertNotNull(subscriber);

        boolean added = phoneBookService.addPhoneNumber(subscriber, "+1234567890", PhoneType.MOBILE);
        assertTrue(added);
        assertEquals(1, subscriber.getPhoneNumbers().size());

        // Дублирование номера
        boolean duplicate = phoneBookService.addPhoneNumber(subscriber, "+1234567890", PhoneType.MOBILE);
        assertFalse(duplicate);
        assertEquals(1, subscriber.getPhoneNumbers().size());
    }

    @Test
    void testAddInvalidPhoneNumber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        assertNotNull(subscriber);

        // Пустой номер
        boolean invalid1 = phoneBookService.addPhoneNumber(subscriber, "", PhoneType.MOBILE);
        assertFalse(invalid1);
        assertEquals(0, subscriber.getPhoneNumbers().size());

        // Некорректный формат
        boolean invalid2 = phoneBookService.addPhoneNumber(subscriber, "abc123", PhoneType.MOBILE);
        assertFalse(invalid2);
        assertEquals(0, subscriber.getPhoneNumbers().size());

        // Слишком короткий номер
        boolean invalid3 = phoneBookService.addPhoneNumber(subscriber, "123", PhoneType.MOBILE);
        assertFalse(invalid3);
        assertEquals(0, subscriber.getPhoneNumbers().size());
    }

    @Test
    void testRemovePhoneNumber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        PhoneNumber phoneNumber = new PhoneNumber("+1234567890", PhoneType.MOBILE);
        subscriber.addPhoneNumber(phoneNumber);

        boolean removed = phoneBookService.removePhoneNumber(subscriber, phoneNumber);
        assertTrue(removed);
        assertEquals(0, subscriber.getPhoneNumbers().size());
    }

    @Test
    void testSearchSubscribers() {
        phoneBookService.addSubscriber("Smith", "John", "David");
        phoneBookService.addSubscriber("Johnson", "James", "Robert");
        phoneBookService.addSubscriber("Williams", "Michael", "David");

        // Поиск по фамилии
        List<Subscriber> result1 = phoneBookService.searchSubscribers("Smith");
        assertEquals(1, result1.size());
        assertEquals("Smith", result1.get(0).getLastName());

        // Поиск по имени
        List<Subscriber> result2 = phoneBookService.searchSubscribers("James");
        assertEquals(1, result2.size());
        assertEquals("Johnson", result2.get(0).getLastName());

        // Поиск по отчеству
        List<Subscriber> result3 = phoneBookService.searchSubscribers("David");
        assertEquals(2, result3.size());

        // Поиск без результатов
        List<Subscriber> result4 = phoneBookService.searchSubscribers("Nonexistent");
        assertEquals(0, result4.size());

        // Поиск пустой строкой возвращает всех
        List<Subscriber> result5 = phoneBookService.searchSubscribers("");
        assertEquals(3, result5.size());
    }

    @Test
    void testSearchByPhoneNumber() {
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        phoneBookService.addPhoneNumber(subscriber, "+1234567890", PhoneType.MOBILE);

        List<Subscriber> result = phoneBookService.searchSubscribers("1234567890");
        assertEquals(1, result.size());
        assertEquals("Smith", result.get(0).getLastName());
    }

    @Test
    void testSortSubscribers() {
        phoneBookService.addSubscriber("Smith", "John", "David");
        phoneBookService.addSubscriber("Adams", "John", "David");
        phoneBookService.addSubscriber("Williams", "Michael", "David");

        List<Subscriber> subscribers = phoneBookService.getAllSubscribers();
        assertEquals("Adams", subscribers.get(0).getLastName());
        assertEquals("Smith", subscribers.get(1).getLastName());
        assertEquals("Williams", subscribers.get(2).getLastName());
    }

    @Test
    void testGetPhoneNumberCount() {
        Subscriber subscriber1 = phoneBookService.addSubscriber("Smith", "John", "David");
        Subscriber subscriber2 = phoneBookService.addSubscriber("Johnson", "James", "Robert");

        phoneBookService.addPhoneNumber(subscriber1, "+1234567890", PhoneType.MOBILE);
        phoneBookService.addPhoneNumber(subscriber1, "+0987654321", PhoneType.HOME);
        phoneBookService.addPhoneNumber(subscriber2, "+1111111111", PhoneType.WORK);

        assertEquals(3, phoneBookService.getPhoneNumberCount());
    }

    @Test
    void testSaveAndLoadData() {
        // Добавляем тестовые данные
        Subscriber subscriber = phoneBookService.addSubscriber("Smith", "John", "David");
        phoneBookService.addPhoneNumber(subscriber, "+1234567890", PhoneType.MOBILE);

        // Сохраняем
        boolean saved = phoneBookService.saveData();
        assertTrue(saved);

        // Создаем новый сервис с тем же файлом
        PhoneBookService newService = new PhoneBookService(testDataFile);

        // Проверяем, что данные загрузились
        assertEquals(1, newService.getSubscriberCount());
        assertEquals(1, newService.getPhoneNumberCount());

        List<Subscriber> subscribers = newService.getAllSubscribers();
        assertEquals("Smith", subscribers.get(0).getLastName());
        assertEquals("John", subscribers.get(0).getFirstName());
        assertEquals(1, subscribers.get(0).getPhoneNumbers().size());
    }
}