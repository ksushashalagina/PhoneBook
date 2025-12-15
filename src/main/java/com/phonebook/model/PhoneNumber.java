package com.phonebook.model;

import java.io.Serializable;

/**
 * Класс, представляющий телефонный номер.
 * Хранит номер и его тип.
 */
public class PhoneNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    private String number;
    private PhoneType type;

    /**
     * Конструктор для создания телефонного номера.
     *
     * @param number строковое представление номера
     * @param type тип телефонного номера
     */
    public PhoneNumber(String number, PhoneType type) {
        this.number = number;
        this.type = type;
    }

    /**
     * Возвращает номер телефона.
     *
     * @return номер телефона
     */
    public String getNumber() {
        return number;
    }

    /**
     * Устанавливает номер телефона.
     *
     * @param number новый номер телефона
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Возвращает тип телефонного номера.
     *
     * @return тип номера
     */
    public PhoneType getType() {
        return type;
    }

    /**
     * Устанавливает тип телефонного номера.
     *
     * @param type новый тип номера
     */
    public void setType(PhoneType type) {
        this.type = type;
    }

    /**
     * Проверяет, содержит ли номер указанную подстроку.
     *
     * @param searchText текст для поиска
     * @return true если номер содержит подстроку, иначе false
     */
    public boolean contains(String searchText) {
        return number.toLowerCase().contains(searchText.toLowerCase());
    }

    /**
     * Проверяет, является ли номер валидным.
     * Валидным считается номер, содержащий только цифры, пробелы, скобки и знаки +, -.
     *
     * @return true если номер валиден, иначе false
     */
    public boolean isValid() {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        // Разрешаем цифры, пробелы, скобки, плюс, минус
        return number.matches("^[0-9+\\-()\\s]+$");
    }

    /**
     * Форматирует номер для отображения.
     *
     * @return отформатированная строка с номером и типом
     */
    public String getFormatted() {
        return String.format("%s (%s)", number, type.getDisplayName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneNumber that = (PhoneNumber) obj;
        return number.equals(that.number) && type == that.type;
    }

    @Override
    public int hashCode() {
        return 31 * number.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}