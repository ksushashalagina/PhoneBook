package com.phonebook.gui;

import com.phonebook.model.Subscriber;
import com.phonebook.model.PhoneNumber;
import com.phonebook.model.PhoneType;
import com.phonebook.service.PhoneBookService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;

/**
 * Графический интерфейс приложения телефонного справочника.
 * Основное окно с возможностями управления абонентами.
 */
public class PhoneBookGUI extends Application {

    private static final Logger logger = LogManager.getLogger(PhoneBookGUI.class);

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    private PhoneBookService phoneBookService;
    private ObservableList<Subscriber> subscribersList;
    private ObservableList<PhoneNumber> phoneNumbersList;

    // Основные элементы интерфейса
    private TableView<Subscriber> subscribersTable;
    private TableView<PhoneNumber> phoneNumbersTable;
    private TextField searchField;
    private Label statusLabel;
    private Label statsLabel;

    // Колонки таблицы абонентов
    private TableColumn<Subscriber, String> lastNameColumn;
    private TableColumn<Subscriber, String> firstNameColumn;
    private TableColumn<Subscriber, String> middleNameColumn;

    // Колонки таблицы телефонных номеров
    private TableColumn<PhoneNumber, String> phoneNumberColumn;
    private TableColumn<PhoneNumber, PhoneType> phoneTypeColumn;

    /**
     * Точка входа для запуска графического интерфейса.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Phone Book Application GUI");

        try {
            phoneBookService = new PhoneBookService();
            initializeData();
            initializeUI(primaryStage);
            primaryStage.show();
            updateStatus("Application started successfully");
            updateStats();
            logger.info("Phone Book GUI initialized successfully");
        } catch (Exception e) {
            logger.error("Error starting GUI: {}", e.getMessage(), e);
            showErrorDialog("Startup Error", "Failed to start application", e.getMessage());
        }
    }

    /**
     * Инициализирует данные приложения.
     */
    private void initializeData() {
        subscribersList = FXCollections.observableArrayList(phoneBookService.getAllSubscribers());
        phoneNumbersList = FXCollections.observableArrayList();
    }

    /**
     * Инициализирует пользовательский интерфейс.
     */
    private void initializeUI(Stage stage) {
        stage.setTitle("Phone Book Manager");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Верхняя панель с поиском
        root.setTop(createTopPanel());

        // Центральная панель с таблицами
        root.setCenter(createCenterPanel());

        // Нижняя панель со статусом
        root.setBottom(createBottomPanel());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setOnCloseRequest(event -> {
            logger.info("Closing Phone Book Application");
            saveData();
        });
    }

    /**
     * Создает верхнюю панель с поиском и кнопками управления.
     */
    private HBox createTopPanel() {
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER_LEFT);

        // Поле поиска
        searchField = new TextField();
        searchField.setPromptText("Search by name or phone number...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });

        // Кнопка поиска
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        // Кнопка сброса поиска
        Button clearSearchButton = new Button("Clear");
        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            performSearch("");
        });

        // Кнопка обновления
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshData());

        // Кнопки масштабирования
        Button zoomOutButton = new Button("-");
        zoomOutButton.setTooltip(new Tooltip("Decrease font size"));
        zoomOutButton.setOnAction(e -> zoomTable(0.9));

        Button zoomInButton = new Button("+");
        zoomInButton.setTooltip(new Tooltip("Increase font size"));
        zoomInButton.setOnAction(e -> zoomTable(1.1));

        Button resetZoomButton = new Button("Reset Zoom");
        resetZoomButton.setTooltip(new Tooltip("Reset to default font size"));
        resetZoomButton.setOnAction(e -> resetZoom());

        topPanel.getChildren().addAll(
                new Label("Search:"),
                searchField,
                searchButton,
                clearSearchButton,
                new Separator(),
                refreshButton,
                new Separator(),
                zoomOutButton,
                zoomInButton,
                resetZoomButton
        );

        return topPanel;
    }

    /**
     * Создает центральную панель с таблицами.
     */
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        // Создаем таблицу абонентов
        subscribersTable = createSubscribersTable();

        // Создаем таблицу телефонных номеров
        phoneNumbersTable = createPhoneNumbersTable();

        // Панель управления абонентами
        HBox subscriberButtons = createSubscriberButtons();

        // Панель управления телефонными номерами
        HBox phoneButtons = createPhoneNumberButtons();

        // Разделитель
        Label phonesLabel = new Label("Phone Numbers:");
        phonesLabel.setFont(Font.font("Arial", 14));

        centerPanel.getChildren().addAll(
                new Label("Subscribers:"),
                subscribersTable,
                subscriberButtons,
                new Separator(),
                phonesLabel,
                phoneNumbersTable,
                phoneButtons
        );

        VBox.setVgrow(subscribersTable, Priority.ALWAYS);
        VBox.setVgrow(phoneNumbersTable, Priority.ALWAYS);

        return centerPanel;
    }

    /**
     * Создает таблицу абонентов.
     */
    private TableView<Subscriber> createSubscribersTable() {
        TableView<Subscriber> table = new TableView<>();
        table.setItems(subscribersList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Колонка фамилии
        lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setPrefWidth(150);

        // Колонка имени
        firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setPrefWidth(150);

        // Колонка отчества
        middleNameColumn = new TableColumn<>("Middle Name");
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameColumn.setPrefWidth(150);

        table.getColumns().addAll(lastNameColumn, firstNameColumn, middleNameColumn);

        // Обработчик выбора абонента
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updatePhoneNumbersTable(newValue);
                    }
                }
        );

        return table;
    }

    /**
     * Создает таблицу телефонных номеров.
     */
    private TableView<PhoneNumber> createPhoneNumbersTable() {
        TableView<PhoneNumber> table = new TableView<>();
        table.setItems(phoneNumbersList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Колонка номера телефона
        phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneNumberColumn.setPrefWidth(200);

        // Колонка типа телефона
        phoneTypeColumn = new TableColumn<>("Type");
        phoneTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        phoneTypeColumn.setPrefWidth(100);

        table.getColumns().addAll(phoneNumberColumn, phoneTypeColumn);

        return table;
    }

    /**
     * Создает панель кнопок для управления абонентами.
     */
    private HBox createSubscriberButtons() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button addButton = new Button("Add Subscriber");
        addButton.setOnAction(e -> addSubscriber());

        Button editButton = new Button("Edit Subscriber");
        editButton.setOnAction(e -> editSubscriber());

        Button deleteButton = new Button("Delete Subscriber");
        deleteButton.setOnAction(e -> deleteSubscriber());

        Button sortButton = new Button("Sort by Name");
        sortButton.setOnAction(e -> sortSubscribers());

        buttonPanel.getChildren().addAll(addButton, editButton, deleteButton, sortButton);

        return buttonPanel;
    }

    /**
     * Создает панель кнопок для управления телефонными номерами.
     */
    private HBox createPhoneNumberButtons() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button addPhoneButton = new Button("Add Phone");
        addPhoneButton.setOnAction(e -> addPhoneNumber());

        Button deletePhoneButton = new Button("Delete Phone");
        deletePhoneButton.setOnAction(e -> deletePhoneNumber());

        buttonPanel.getChildren().addAll(addPhoneButton, deletePhoneButton);

        return buttonPanel;
    }

    /**
     * Создает нижнюю панель со статусом и статистикой.
     */
    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        bottomPanel.setStyle("-fx-background-color: #f0f0f0;");

        statusLabel = new Label("Ready");
        statsLabel = new Label();

        bottomPanel.getChildren().addAll(statusLabel, new Separator(), statsLabel);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        return bottomPanel;
    }

    /**
     * Изменяет масштаб таблиц.
     *
     * @param factor множитель масштабирования (например, 1.1 для увеличения на 10%)
     */
    private void zoomTable(double factor) {
        try {
            // Текущий размер шрифта по умолчанию 12px
            double currentSize = 12.0;

            // Получаем текущий размер из стиля таблицы абонентов
            String style = subscribersTable.getStyle();
            if (style != null && !style.isEmpty()) {
                // Ищем "-fx-font-size:"
                int startIdx = style.indexOf("-fx-font-size:");
                if (startIdx != -1) {
                    startIdx += "-fx-font-size:".length();
                    int endIdx = style.indexOf("px", startIdx);
                    if (endIdx != -1) {
                        String sizeStr = style.substring(startIdx, endIdx).trim();
                        try {
                            currentSize = Double.parseDouble(sizeStr);
                        } catch (NumberFormatException e) {
                            // Если не удалось распарсить, используем значение по умолчанию
                            currentSize = 12.0;
                        }
                    }
                }
            }

            // Применяем масштабирование
            double newSize = currentSize * factor;

            // Ограничиваем размер шрифта
            if (newSize < 8.0) {
                newSize = 8.0;
            } else if (newSize > 24.0) {
                newSize = 24.0;
            }

            // Форматируем без десятичных знаков (целые числа выглядят лучше)
            int roundedSize = (int) Math.round(newSize);

            // Применяем новый размер к обеим таблицам
            String newStyle = String.format("-fx-font-size: %dpx;", roundedSize);
            subscribersTable.setStyle(newStyle);
            phoneNumbersTable.setStyle(newStyle);

            // Обновляем статус
            updateStatus(String.format("Font size: %dpx", roundedSize));

        } catch (Exception e) {
            // При любой ошибке сбрасываем к стандартному размеру
            subscribersTable.setStyle("-fx-font-size: 12px;");
            phoneNumbersTable.setStyle("-fx-font-size: 12px;");
            updateStatus("Zoom error - reset to default");
            logger.warn("Zoom error, reset to default: {}", e.getMessage());
        }
    }

    /**
     * Сбрасывает масштаб к значению по умолчанию.
     */
    private void resetZoom() {
        subscribersTable.setStyle("-fx-font-size: 12px;");
        phoneNumbersTable.setStyle("-fx-font-size: 12px;");
        updateStatus("Zoom reset to default (12px)");
    }

    /**
     * Выполняет поиск абонентов.
     */
    private void performSearch(String searchText) {
        List<Subscriber> searchResults = phoneBookService.searchSubscribers(searchText);
        subscribersList.setAll(searchResults);
        updateStatus("Found " + searchResults.size() + " subscribers");
    }

    /**
     * Обновляет таблицу телефонных номеров для выбранного абонента.
     */
    private void updatePhoneNumbersTable(Subscriber subscriber) {
        phoneNumbersList.setAll(subscriber.getPhoneNumbers());
        updateStatus("Selected: " + subscriber.getFullName());
    }

    /**
     * Обновляет статусную строку.
     */
    private void updateStatus(String message) {
        statusLabel.setText("Status: " + message);
        logger.info("Status update: {}", message);
    }

    /**
     * Обновляет статистику.
     */
    private void updateStats() {
        int subscriberCount = phoneBookService.getSubscriberCount();
        int phoneCount = phoneBookService.getPhoneNumberCount();
        statsLabel.setText(String.format("Subscribers: %d | Phone Numbers: %d",
                subscriberCount, phoneCount));
    }

    /**
     * Обновляет данные в таблицах.
     */
    private void refreshData() {
        subscribersList.setAll(phoneBookService.getAllSubscribers());
        updateStats();
        updateStatus("Data refreshed");
    }

    /**
     * Сохраняет данные.
     */
    private void saveData() {
        if (phoneBookService.saveData()) {
            updateStatus("Data saved successfully");
        } else {
            updateStatus("Error saving data");
        }
    }

    /**
     * Добавляет нового абонента.
     */
    private void addSubscriber() {
        SubscriberDialog dialog = new SubscriberDialog("Add New Subscriber",
                "Enter subscriber information:");

        Optional<SubscriberDialog.SubscriberResult> result = dialog.showAndWait();
        result.ifPresent(subscriberResult -> {
            Subscriber newSubscriber = phoneBookService.addSubscriber(
                    subscriberResult.getLastName(),
                    subscriberResult.getFirstName(),
                    subscriberResult.getMiddleName()
            );

            if (newSubscriber != null) {
                subscribersList.add(newSubscriber);
                subscribersTable.getSelectionModel().select(newSubscriber);
                updatePhoneNumbersTable(newSubscriber);
                updateStats();
                updateStatus("Subscriber added: " + newSubscriber.getFullName());
            } else {
                showErrorDialog("Error", "Failed to add subscriber",
                        "Please check the entered data.");
            }
        });
    }

    /**
     * Редактирует выбранного абонента.
     */
    private void editSubscriber() {
        Subscriber selected = subscribersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Error", "No Subscriber Selected",
                    "Please select a subscriber to edit.");
            return;
        }

        SubscriberDialog dialog = new SubscriberDialog("Edit Subscriber",
                "Edit subscriber information:");
        dialog.setValues(selected.getLastName(), selected.getFirstName(), selected.getMiddleName());

        Optional<SubscriberDialog.SubscriberResult> result = dialog.showAndWait();
        result.ifPresent(subscriberResult -> {
            boolean success = phoneBookService.updateSubscriber(
                    selected,
                    subscriberResult.getLastName(),
                    subscriberResult.getFirstName(),
                    subscriberResult.getMiddleName()
            );

            if (success) {
                subscribersList.setAll(phoneBookService.getAllSubscribers());
                subscribersTable.getSelectionModel().select(selected);
                updatePhoneNumbersTable(selected);
                updateStatus("Subscriber updated: " + selected.getFullName());
            } else {
                showErrorDialog("Error", "Failed to update subscriber",
                        "Please check the entered data.");
            }
        });
    }

    /**
     * Удаляет выбранного абонента.
     */
    private void deleteSubscriber() {
        Subscriber selected = subscribersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Error", "No Subscriber Selected",
                    "Please select a subscriber to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Subscriber");
        confirmDialog.setContentText("Are you sure you want to delete subscriber: " +
                selected.getFullName() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (phoneBookService.deleteSubscriber(selected)) {
                subscribersList.remove(selected);
                phoneNumbersList.clear();
                updateStats();
                updateStatus("Subscriber deleted: " + selected.getFullName());
            } else {
                showErrorDialog("Error", "Delete Failed",
                        "Failed to delete subscriber.");
            }
        }
    }

    /**
     * Добавляет телефонный номер выбранному абоненту.
     */
    private void addPhoneNumber() {
        Subscriber selected = subscribersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Error", "No Subscriber Selected",
                    "Please select a subscriber to add phone number.");
            return;
        }

        PhoneNumberDialog dialog = new PhoneNumberDialog("Add Phone Number",
                "Enter phone number information:");

        Optional<PhoneNumberDialog.PhoneNumberResult> result = dialog.showAndWait();
        result.ifPresent(phoneResult -> {
            boolean success = phoneBookService.addPhoneNumber(
                    selected,
                    phoneResult.getNumber(),
                    phoneResult.getType()
            );

            if (success) {
                updatePhoneNumbersTable(selected);
                updateStats();
                updateStatus("Phone number added to: " + selected.getFullName());
            } else {
                showErrorDialog("Error", "Failed to add phone number",
                        "Please check the entered data or if number already exists.");
            }
        });
    }

    /**
     * Удаляет выбранный телефонный номер.
     */
    private void deletePhoneNumber() {
        PhoneNumber selectedPhone = phoneNumbersTable.getSelectionModel().getSelectedItem();
        Subscriber selectedSubscriber = subscribersTable.getSelectionModel().getSelectedItem();

        if (selectedPhone == null || selectedSubscriber == null) {
            showErrorDialog("Error", "No Phone Number Selected",
                    "Please select a phone number to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Phone Number");
        confirmDialog.setContentText("Are you sure you want to delete phone number: " +
                selectedPhone.getNumber() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (phoneBookService.removePhoneNumber(selectedSubscriber, selectedPhone)) {
                phoneNumbersList.remove(selectedPhone);
                updateStats();
                updateStatus("Phone number deleted: " + selectedPhone.getNumber());
            } else {
                showErrorDialog("Error", "Delete Failed",
                        "Failed to delete phone number.");
            }
        }
    }

    /**
     * Сортирует абонентов по ФИО.
     */
    private void sortSubscribers() {
        phoneBookService.sortSubscribers();
        subscribersList.setAll(phoneBookService.getAllSubscribers());
        updateStatus("Subscribers sorted by name");
    }

    /**
     * Показывает диалоговое окно с ошибкой.
     */
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        logger.error("Error dialog shown: {} - {}", header, content);
    }
}