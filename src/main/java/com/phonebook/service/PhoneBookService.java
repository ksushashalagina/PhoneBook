package com.phonebook.service;

import com.phonebook.model.Subscriber;
import com.phonebook.model.PhoneNumber;
import com.phonebook.model.PhoneType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 * Основной сервис телефонной книги.
 * Управляет операциями с абонентами и их телефонными номерами.
 */
public class PhoneBookService {

    private static final Logger logger = LogManager.getLogger(PhoneBookService.class);

    private final List<Subscriber> subscribers;
    private final FileDataService fileDataService;

    /**
     * Конструктор сервиса телефонной книги.
     */
    public PhoneBookService() {
        this.subscribers = new ArrayList<>();
        this.fileDataService = new FileDataService();
        loadData();
    }

    /**
     * Конструктор сервиса с указанием имени файла данных.
     *
     * @param dataFileName имя файла данных
     */
    public PhoneBookService(String dataFileName) {
        this.subscribers = new ArrayList<>();
        this.fileDataService = new FileDataService(dataFileName);
        loadData();
    }

    /**
     * Загружает данные из файла.
     */
    private void loadData() {
        try {
            List<Subscriber> loadedSubscribers = fileDataService.loadSubscribers();
            subscribers.clear();
            subscribers.addAll(loadedSubscribers);
            sortSubscribers();
            logger.info("Data loaded successfully. Total subscribers: {}", subscribers.size());
        } catch (Exception e) {
            logger.error("Error loading data: {}", e.getMessage(), e);
            subscribers.clear();
        }
    }

    /**
     * Сохраняет данные в файл.
     *
     * @return true если сохранение прошло успешно, иначе false
     */
    public boolean saveData() {
        try {
            fileDataService.saveSubscribers(subscribers);
            logger.info("Data saved successfully. Total subscribers: {}", subscribers.size());
            return true;
        } catch (Exception e) {
            logger.error("Error saving data: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Добавляет нового абонента.
     *
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     * @return созданный абонент или null если абонент не добавлен
     */
    public Subscriber addSubscriber(String lastName, String firstName, String middleName) {
        try {
            PhoneBookValidator.validateSubscriber(lastName, firstName, middleName);

            Subscriber subscriber = new Subscriber(lastName, firstName, middleName);
            subscribers.add(subscriber);
            sortSubscribers();
            saveData();

            logger.info("Subscriber added: {}", subscriber.getFullName());
            return subscriber;

        } catch (IllegalArgumentException e) {
            logger.error("Error adding subscriber: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Обновляет информацию об абоненте.
     *
     * @param subscriber абонент для обновления
     * @param lastName новая фамилия
     * @param firstName новое имя
     * @param middleName новое отчество
     * @return true если обновление прошло успешно, иначе false
     */
    public boolean updateSubscriber(Subscriber subscriber, String lastName, String firstName, String middleName) {
        try {
            PhoneBookValidator.validateSubscriber(lastName, firstName, middleName);

            subscriber.setLastName(lastName);
            subscriber.setFirstName(firstName);
            subscriber.setMiddleName(middleName);

            sortSubscribers();
            saveData();

            logger.info("Subscriber updated: {}", subscriber.getFullName());
            return true;

        } catch (IllegalArgumentException e) {
            logger.error("Error updating subscriber: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет абонента.
     *
     * @param subscriber абонент для удаления
     * @return true если абонент удален, иначе false
     */
    public boolean deleteSubscriber(Subscriber subscriber) {
        if (subscribers.remove(subscriber)) {
            saveData();
            logger.info("Subscriber deleted: {}", subscriber.getFullName());
            return true;
        }
        return false;
    }

    /**
     * Добавляет телефонный номер абоненту.
     *
     * @param subscriber абонент
     * @param number номер телефона
     * @param type тип телефона
     * @return true если номер добавлен, иначе false
     */
    public boolean addPhoneNumber(Subscriber subscriber, String number, PhoneType type) {
        try {
            PhoneBookValidator.validatePhoneNumber(number);

            PhoneNumber phoneNumber = new PhoneNumber(number, type);
            if (subscriber.addPhoneNumber(phoneNumber)) {
                saveData();
                logger.info("Phone number added to {}: {}", subscriber.getFullName(), phoneNumber);
                return true;
            }

            logger.warn("Phone number already exists for subscriber: {}", number);
            return false;

        } catch (IllegalArgumentException e) {
            logger.error("Error adding phone number: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет телефонный номер у абонента.
     *
     * @param subscriber абонент
     * @param phoneNumber телефонный номер для удаления
     * @return true если номер удален, иначе false
     */
    public boolean removePhoneNumber(Subscriber subscriber, PhoneNumber phoneNumber) {
        if (subscriber.removePhoneNumber(phoneNumber)) {
            saveData();
            logger.info("Phone number removed from {}: {}", subscriber.getFullName(), phoneNumber);
            return true;
        }
        return false;
    }

    /**
     * Возвращает список всех абонентов.
     *
     * @return список всех абонентов
     */
    public List<Subscriber> getAllSubscribers() {
        return new ArrayList<>(subscribers);
    }

    /**
     * Выполняет поиск абонентов по заданному тексту.
     *
     * @param searchText текст для поиска
     * @return список найденных абонентов
     */
    public List<Subscriber> searchSubscribers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllSubscribers();
        }

        List<Subscriber> result = new ArrayList<>();
        String lowerSearch = searchText.toLowerCase();

        for (Subscriber subscriber : subscribers) {
            if (subscriber.contains(lowerSearch)) {
                result.add(subscriber);
            }
        }

        logger.info("Search '{}' found {} subscribers", searchText, result.size());
        return result;
    }

    /**
     * Сортирует абонентов по ФИО.
     */
    public void sortSubscribers() {
        subscribers.sort(Comparator.naturalOrder());
        logger.debug("Subscribers sorted");
    }

    /**
     * Возвращает количество абонентов.
     *
     * @return количество абонентов
     */
    public int getSubscriberCount() {
        return subscribers.size();
    }

    /**
     * Возвращает общее количество телефонных номеров.
     *
     * @return количество телефонных номеров
     */
    public int getPhoneNumberCount() {
        int count = 0;
        for (Subscriber subscriber : subscribers) {
            count += subscriber.getPhoneNumbers().size();
        }
        return count;
    }

    /**
     * Очищает все данные телефонной книги.
     *
     * @return true если данные очищены успешно, иначе false
     */
    public boolean clearAllData() {
        subscribers.clear();
        return saveData();
    }
}