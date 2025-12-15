package com.phonebook.model;

import java.io.Serializable;

/**
 * Перечисление типов телефонных номеров.
 * Определяет различные категории телефонных номеров.
 */
public enum PhoneType implements Serializable {
    MOBILE("Mobile"),
    HOME("Home"),
    WORK("Work"),
    FAX("Fax"),
    OTHER("Other");

    private final String displayName;

    /**
     * Конструктор перечисления типов телефонов.
     *
     * @param displayName отображаемое имя типа
     */
    PhoneType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает отображаемое имя типа телефона.
     *
     * @return отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Возвращает массив отображаемых имен всех типов.
     *
     * @return массив имен типов
     */
    public static String[] getDisplayNames() {
        PhoneType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * Находит тип телефона по отображаемому имени.
     *
     * @param displayName отображаемое имя
     * @return соответствующий тип или null, если не найден
     */
    public static PhoneType fromDisplayName(String displayName) {
        for (PhoneType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}