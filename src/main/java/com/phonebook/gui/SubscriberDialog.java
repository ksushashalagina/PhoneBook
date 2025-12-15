package com.phonebook.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import java.util.Optional;

/**
 * Диалоговое окно для добавления или редактирования абонента.
 */
public class SubscriberDialog {

    private final Dialog<SubscriberResult> dialog;
    private final TextField lastNameField;
    private final TextField firstNameField;
    private final TextField middleNameField;

    /**
     * Конструктор диалогового окна.
     *
     * @param title заголовок окна
     * @param headerText заголовок содержимого
     */
    public SubscriberDialog(String title, String headerText) {
        dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        // Устанавливаем кнопки
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Создаем поля ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        lastNameField = new TextField();
        lastNameField.setPromptText("Last name");

        firstNameField = new TextField();
        firstNameField.setPromptText("First name");

        middleNameField = new TextField();
        middleNameField.setPromptText("Middle name");

        grid.add(new Label("Last Name*:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("First Name*:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Middle Name:"), 0, 2);
        grid.add(middleNameField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Валидация обязательных полей
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new SubscriberResult(
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText()
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
    public Optional<SubscriberResult> showAndWait() {
        return dialog.showAndWait();
    }

    /**
     * Устанавливает значения полей для редактирования.
     *
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     */
    public void setValues(String lastName, String firstName, String middleName) {
        lastNameField.setText(lastName);
        firstNameField.setText(firstName);
        middleNameField.setText(middleName);
    }

    /**
     * Класс для хранения результата диалога.
     */
    public static class SubscriberResult {
        private final String lastName;
        private final String firstName;
        private final String middleName;

        public SubscriberResult(String lastName, String firstName, String middleName) {
            this.lastName = lastName;
            this.firstName = firstName;
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getMiddleName() {
            return middleName;
        }
    }
}