package com.phonebook;

import com.phonebook.gui.PhoneBookGUI;

/**
 * Главный класс приложения телефонного справочника.
 * Точка входа в программу.
 */
public class Main {

    /**
     * Основной метод запуска приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.out.println("Starting Phone Book Application...");
        PhoneBookGUI.main(args);
    }
}