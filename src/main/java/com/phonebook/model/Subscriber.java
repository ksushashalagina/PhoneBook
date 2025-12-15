package com.phonebook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс, представляющий абонента телефонной книги.
 * Хранит информацию об абоненте и его телефонных номерах.
 */
public class Subscriber implements Serializable, Comparable<Subscriber> {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private final List<PhoneNumber> phoneNumbers;

    /**
     * Конструктор для создания нового абонента.
     *
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     */
    public Subscriber(String lastName, String firstName, String middleName) {
        this.id = UUID.randomUUID().toString();
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phoneNumbers = new ArrayList<>();
    }

    /**
     * Конструктор для загрузки существующего абонента.
     *
     * @param id уникальный идентификатор
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     * @param phoneNumbers список телефонных номеров
     */
    public Subscriber(String id, String lastName, String firstName, String middleName, List<PhoneNumber> phoneNumbers) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phoneNumbers = new ArrayList<>(phoneNumbers);
    }

    /**
     * Возвращает уникальный идентификатор абонента.
     *
     * @return идентификатор
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает фамилию абонента.
     *
     * @return фамилия
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию абонента.
     *
     * @param lastName новая фамилия
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает имя абонента.
     *
     * @return имя
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя абонента.
     *
     * @param firstName новое имя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает отчество абонента.
     *
     * @return отчество
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Устанавливает отчество абонента.
     *
     * @param middleName новое отчество
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Возвращает полное имя абонента в формате "Фамилия Имя Отчество".
     *
     * @return полное имя
     */
    public String getFullName() {
        return String.format("%s %s %s", lastName, firstName, middleName);
    }

    /**
     * Возвращает список телефонных номеров абонента.
     *
     * @return неизменяемый список номеров
     */
    public List<PhoneNumber> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);
    }

    /**
     * Добавляет телефонный номер абоненту.
     *
     * @param phoneNumber телефонный номер для добавления
     * @return true если номер успешно добавлен, false если номер уже существует
     */
    public boolean addPhoneNumber(PhoneNumber phoneNumber) {
        if (!phoneNumbers.contains(phoneNumber)) {
            phoneNumbers.add(phoneNumber);
            return true;
        }
        return false;
    }

    /**
     * Удаляет телефонный номер у абонента.
     *
     * @param phoneNumber телефонный номер для удаления
     * @return true если номер успешно удален, false если номер не найден
     */
    public boolean removePhoneNumber(PhoneNumber phoneNumber) {
        return phoneNumbers.remove(phoneNumber);
    }

    /**
     * Проверяет, содержит ли информация об абоненте указанный текст.
     * Поиск осуществляется по ФИО и всем телефонным номерам.
     *
     * @param searchText текст для поиска
     * @return true если найден совпадающий текст, иначе false
     */

    public boolean contains(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }

        String lowerSearch = searchText.toLowerCase().trim();

        // Поиск по ФИО
        if ((lastName != null && lastName.toLowerCase().contains(lowerSearch)) ||
                (firstName != null && firstName.toLowerCase().contains(lowerSearch)) ||
                (middleName != null && middleName.toLowerCase().contains(lowerSearch)) ||
                getFullName().toLowerCase().contains(lowerSearch)) {
            return true;
        }

        // Поиск по телефонным номерам
        for (PhoneNumber phone : phoneNumbers) {
            // Ищем в номере
            if (phone.getNumber().toLowerCase().contains(lowerSearch)) {
                return true;
            }

            // Ищем в типе (и по имени enum и по отображаемому имени)
            PhoneType type = phone.getType();
            if (type.name().toLowerCase().contains(lowerSearch) ||
                    type.getDisplayName().toLowerCase().contains(lowerSearch)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Проверяет, является ли информация об абоненте валидной.
     * Валидным считается абонент с непустыми фамилией и именем.
     *
     * @return true если абонент валиден, иначе false
     */
    public boolean isValid() {
        return lastName != null && !lastName.trim().isEmpty() &&
                firstName != null && !firstName.trim().isEmpty();
    }

    @Override
    public int compareTo(Subscriber other) {
        int lastNameCompare = this.lastName.compareToIgnoreCase(other.lastName);
        if (lastNameCompare != 0) {
            return lastNameCompare;
        }

        int firstNameCompare = this.firstName.compareToIgnoreCase(other.firstName);
        if (firstNameCompare != 0) {
            return firstNameCompare;
        }

        return this.middleName.compareToIgnoreCase(other.middleName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subscriber that = (Subscriber) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}