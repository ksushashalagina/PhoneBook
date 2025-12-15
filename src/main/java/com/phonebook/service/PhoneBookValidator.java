package com.phonebook.service;

import com.phonebook.model.Subscriber;
import com.phonebook.model.PhoneNumber;

/**
 * Класс для валидации данных телефонной книги.
 * Проверяет корректность вводимых данных.
 */
public class PhoneBookValidator {

    /**
     * Приватный конструктор для предотвращения создания экземпляров.
     */
    private PhoneBookValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Проверяет валидность данных абонента.
     *
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     * @throws IllegalArgumentException если данные невалидны
     */
    public static void validateSubscriber(String lastName, String firstName, String middleName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        // Проверяем, что ФИО содержат только буквы, пробелы и дефисы
        if (!isValidName(lastName)) {
            throw new IllegalArgumentException("Last name contains invalid characters");
        }

        if (!isValidName(firstName)) {
            throw new IllegalArgumentException("First name contains invalid characters");
        }

        if (middleName != null && !middleName.trim().isEmpty() && !isValidName(middleName)) {
            throw new IllegalArgumentException("Middle name contains invalid characters");
        }
    }

    /**
     * Проверяет валидность телефонного номера.
     *
     * @param phoneNumber телефонный номер
     * @throws IllegalArgumentException если номер невалиден
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Удаляем все нецифровые символы, кроме + в начале
        String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");

        if (cleanNumber.length() < 5) {
            throw new IllegalArgumentException("Phone number is too short");
        }

        if (cleanNumber.length() > 15) {
            throw new IllegalArgumentException("Phone number is too long");
        }

        // Проверяем, что номер начинается с допустимого префикса
        if (!cleanNumber.matches("^(\\+?[0-9]{1,3})?[0-9]{4,}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    /**
     * Проверяет, является ли абонент валидным.
     *
     * @param subscriber абонент для проверки
     * @return true если абонент валиден, иначе false
     */
    public static boolean isValidSubscriber(Subscriber subscriber) {
        if (subscriber == null) {
            return false;
        }

        try {
            validateSubscriber(subscriber.getLastName(),
                    subscriber.getFirstName(),
                    subscriber.getMiddleName());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли телефонный номер валидным.
     *
     * @param phoneNumber телефонный номер для проверки
     * @return true если номер валиден, иначе false
     */
    public static boolean isValidPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        try {
            validatePhoneNumber(phoneNumber.getNumber());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверяет, содержит ли строка только допустимые символы для имени.
     *
     * @param name строка для проверки
     * @return true если строка валидна, иначе false
     */
    private static boolean isValidName(String name) {
        // Разрешаем буквы, пробелы, дефисы и апострофы
        return name.matches("^[\\p{L}\\s\\-' ]+$");
    }
}