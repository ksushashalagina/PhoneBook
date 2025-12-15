package com.phonebook.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Утилитарный класс для работы с файлами.
 */
public class FileUtil {

    /**
     * Приватный конструктор для предотвращения создания экземпляров.
     */
    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    // Форматтер с английской локалью для гарантированного использования точки
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DECIMAL_FORMAT = new DecimalFormat("#.0", symbols);
        DECIMAL_FORMAT.setMaximumFractionDigits(1);
        DECIMAL_FORMAT.setMinimumFractionDigits(1);
    }

    /**
     * Проверяет, существует ли файл.
     *
     * @param filePath путь к файлу
     * @return true если файл существует, иначе false
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Создает директорию, если она не существует.
     *
     * @param directoryPath путь к директории
     * @return true если директория создана или уже существует, иначе false
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    /**
     * Получает расширение файла.
     *
     * @param fileName имя файла
     * @return расширение файла или пустую строку если расширения нет
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Получает имя файла без расширения.
     *
     * @param fileName имя файла
     * @return имя файла без расширения
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * Форматирует размер файла в читаемом виде.
     *
     * @param size размер файла в байтах
     * @return форматированная строка с размером
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            double kb = size / 1024.0;
            return DECIMAL_FORMAT.format(kb) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            double mb = size / (1024.0 * 1024.0);
            return DECIMAL_FORMAT.format(mb) + " MB";
        } else {
            double gb = size / (1024.0 * 1024.0 * 1024.0);
            return DECIMAL_FORMAT.format(gb) + " GB";
        }
    }
}