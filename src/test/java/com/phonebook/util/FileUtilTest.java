package com.phonebook.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для утилит работы с файлами.
 */
class FileUtilTest {

    @TempDir
    Path tempDir;

    // Создаем DecimalFormat с английской локалью для тестов
    private DecimalFormat createEnglishDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.0", symbols);
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        return df;
    }

    @Test
    void testFileExists() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        File file = testFile.toFile();

        assertFalse(FileUtil.fileExists(testFile.toString()));

        file.createNewFile();
        assertTrue(FileUtil.fileExists(testFile.toString()));

        // Проверка несуществующего файла
        assertFalse(FileUtil.fileExists(tempDir.resolve("nonexistent.txt").toString()));
    }

    @Test
    void testCreateDirectoryIfNotExists() {
        Path testDir = tempDir.resolve("testDir");
        String dirPath = testDir.toString();

        assertTrue(FileUtil.createDirectoryIfNotExists(dirPath));
        assertTrue(new File(dirPath).exists());

        // Повторный вызов должен вернуть true
        assertTrue(FileUtil.createDirectoryIfNotExists(dirPath));
    }

    @Test
    void testGetFileExtension() {
        assertEquals("txt", FileUtil.getFileExtension("file.txt"));
        assertEquals("jpg", FileUtil.getFileExtension("image.test.jpg"));
        assertEquals("", FileUtil.getFileExtension("file"));
        assertEquals("", FileUtil.getFileExtension("file."));
        assertEquals("", FileUtil.getFileExtension(null));
        assertEquals("", FileUtil.getFileExtension(""));
    }

    @Test
    void testGetFileNameWithoutExtension() {
        assertEquals("file", FileUtil.getFileNameWithoutExtension("file.txt"));
        assertEquals("image.test", FileUtil.getFileNameWithoutExtension("image.test.jpg"));
        assertEquals("file", FileUtil.getFileNameWithoutExtension("file"));
        assertEquals("file", FileUtil.getFileNameWithoutExtension("file."));
        assertEquals("", FileUtil.getFileNameWithoutExtension(null));
        assertEquals("", FileUtil.getFileNameWithoutExtension(""));
    }

    @Test
    void testFormatFileSize() {
        // Используем английский формат для тестов
        DecimalFormat df = createEnglishDecimalFormat();

        // Байты
        assertEquals("100 B", FileUtil.formatFileSize(100));
        assertEquals("1023 B", FileUtil.formatFileSize(1023));

        // Килобайты
        String kb1 = FileUtil.formatFileSize(1024);
        assertTrue(kb1.contains("KB"));
        assertTrue(kb1.contains("1"));

        String kb2 = FileUtil.formatFileSize(1536); // 1.5 * 1024
        assertTrue(kb2.contains("KB"));
        assertTrue(kb2.contains("1.5") || kb2.contains("1,5"));

        // Мегабайты
        long oneMB = 1024L * 1024L;
        String mb1 = FileUtil.formatFileSize(oneMB);
        assertTrue(mb1.contains("MB"));
        assertTrue(mb1.contains("1"));

        // Гигабайты
        long oneGB = 1024L * 1024L * 1024L;
        String gb1 = FileUtil.formatFileSize(oneGB);
        assertTrue(gb1.contains("GB"));
        assertTrue(gb1.contains("1"));
    }

    @Test
    void testFormatFileSizeEdgeCases() {
        assertEquals("0 B", FileUtil.formatFileSize(0));
        assertEquals("1 B", FileUtil.formatFileSize(1));

        // Проверяем что формат правильный, независимо от локали
        String result = FileUtil.formatFileSize(1024);
        assertTrue(result.contains("KB"));
        assertTrue(result.matches(".*\\d.*")); // Содержит цифру

        result = FileUtil.formatFileSize((long)(1.123 * 1024));
        assertTrue(result.contains("KB"));
    }

    @Test
    void testNonExistentDirectory() {
        // Пытаемся создать директорию в несуществующем пути
        assertTrue(FileUtil.createDirectoryIfNotExists(
                tempDir.resolve("deep/nested/directory").toString()));
    }

    @Test
    void testFormatFileSizeConsistency() {
        // Просто проверяем что метод не падает и возвращает корректные строки
        assertNotNull(FileUtil.formatFileSize(0));
        assertNotNull(FileUtil.formatFileSize(100));
        assertNotNull(FileUtil.formatFileSize(1024));
        assertNotNull(FileUtil.formatFileSize(1024 * 1024));
        assertNotNull(FileUtil.formatFileSize(1024 * 1024 * 1024));

        // Проверяем что возвращаемая строка содержит единицы измерения
        String result = FileUtil.formatFileSize(1024);
        assertTrue(result.endsWith("KB") || result.endsWith("MB") ||
                result.endsWith("GB") || result.endsWith("B"));
    }
}