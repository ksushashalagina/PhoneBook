package com.phonebook.service;

import com.phonebook.model.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с файловым хранилищем телефонной книги.
 * Обеспечивает сохранение и загрузку данных в бинарный файл.
 */
public class FileDataService {

    private static final Logger logger = LogManager.getLogger(FileDataService.class);
    private static final String DEFAULT_FILE_NAME = "phonebook.dat";

    private final String fileName;

    /**
     * Конструктор с использованием файла по умолчанию.
     */
    public FileDataService() {
        this.fileName = DEFAULT_FILE_NAME;
    }

    /**
     * Конструктор с указанием имени файла.
     *
     * @param fileName имя файла для хранения данных
     */
    public FileDataService(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Сохраняет список абонентов в файл.
     *
     * @param subscribers список абонентов для сохранения
     * @throws IOException если произошла ошибка при сохранении
     */
    public void saveSubscribers(List<Subscriber> subscribers) throws IOException {
        logger.info("Saving {} subscribers to file: {}", subscribers.size(), fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)))) {
            oos.writeObject(subscribers);
            logger.info("Subscribers saved successfully");
        } catch (IOException e) {
            logger.error("Error saving subscribers to file: {}", e.getMessage(), e);
            throw new IOException("Failed to save subscribers: " + e.getMessage(), e);
        }
    }

    /**
     * Загружает список абонентов из файла.
     *
     * @return список абонентов
     * @throws IOException если произошла ошибка при загрузке
     * @throws ClassNotFoundException если класс данных не найден
     */
    @SuppressWarnings("unchecked")
    public List<Subscriber> loadSubscribers() throws IOException, ClassNotFoundException {
        logger.info("Loading subscribers from file: {}", fileName);

        File file = new File(fileName);
        if (!file.exists()) {
            logger.info("File not found, returning empty list");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {
            List<Subscriber> subscribers = (List<Subscriber>) ois.readObject();
            logger.info("Loaded {} subscribers from file", subscribers.size());
            return subscribers;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error loading subscribers from file: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Проверяет существование файла с данными.
     *
     * @return true если файл существует, иначе false
     */
    public boolean dataFileExists() {
        return new File(fileName).exists();
    }

    /**
     * Удаляет файл с данными.
     *
     * @return true если файл успешно удален, иначе false
     */
    public boolean deleteDataFile() {
        File file = new File(fileName);
        if (file.exists()) {
            logger.info("Deleting data file: {}", fileName);
            return file.delete();
        }
        return false;
    }

    /**
     * Создает резервную копию файла данных.
     *
     * @param backupName имя файла для резервной копии
     * @return true если резервная копия создана успешно, иначе false
     */
    public boolean createBackup(String backupName) {
        File source = new File(fileName);
        File backup = new File(backupName);

        if (!source.exists()) {
            logger.warn("Source file does not exist for backup: {}", fileName);
            return false;
        }

        try (InputStream in = new BufferedInputStream(new FileInputStream(source));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(backup))) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            logger.info("Backup created successfully: {}", backupName);
            return true;

        } catch (IOException e) {
            logger.error("Error creating backup: {}", e.getMessage(), e);
            return false;
        }
    }
}