package com.phonebook.gui;

import com.phonebook.model.PhoneType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import java.util.Optional;

/**
 * Диалоговое окно для добавления или редактирования телефонного номера.
 */
public class PhoneNumberDialog {

    private final Dialog<PhoneNumberResult> dialog;
    private final TextField numberField;
    private final ComboBox<String> typeComboBox;

    /**
     * Конструктор диалогового окна.
     *
     * @param title заголовок окна
     * @param headerText заголовок содержимого
     */
    public PhoneNumberDialog(String title, String headerText) {
        dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        // Устанавливаем кнопки
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Создаем поля ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        numberField = new TextField();
        numberField.setPromptText("Enter phone number");

        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(PhoneType.getDisplayNames());
        typeComboBox.getSelectionModel().selectFirst();

        grid.add(new Label("Phone Number:"), 0, 0);
        grid.add(numberField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Устанавливаем валидацию
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new PhoneNumberResult(
                        numberField.getText(),
                        PhoneType.fromDisplayName(typeComboBox.getValue())
                );
            }
            return null;
        });
    }

    /**
     * Показывает диалоговое окно и возвращает результат.
     *
     * @return Optional с результатом или empty если диалог отменен
     */
    public Optional<PhoneNumberResult> showAndWait() {
        return dialog.showAndWait();
    }

    /**
     * Устанавливает значения полей для редактирования.
     *
     * @param number текущий номер телефона
     * @param type текущий тип телефона
     */
    public void setValues(String number, PhoneType type) {
        numberField.setText(number);
        if (type != null) {
            typeComboBox.getSelectionModel().select(type.getDisplayName());
        }
    }

    /**
     * Класс для хранения результата диалога.
     */
    public static class PhoneNumberResult {
        private final String number;
        private final PhoneType type;

        public PhoneNumberResult(String number, PhoneType type) {
            this.number = number;
            this.type = type;
        }

        public String getNumber() {
            return number;
        }

        public PhoneType getType() {
            return type;
        }
    }
}