package com.example.daiquiri;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HabitatConsoleController {
    @FXML
    private TextArea commandTextArea;

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button executeButton;

    @FXML
    private HBox hbox;

    public void initialize() {
        commandTextArea.setPromptText("Введите команду здесь");
        commandTextArea.setPrefRowCount(1);

        executeButton.setOnAction(event -> {
            String command = commandTextArea.getText();
            responseTextArea.appendText(command + "\n");
            String response = executeCommand(command);
            responseTextArea.appendText(response);
            commandTextArea.clear();
        });

        HBox.setHgrow(commandTextArea, Priority.ALWAYS);
        HBox.setHgrow(executeButton, Priority.NEVER);
        executeButton.setMinWidth(Button.USE_PREF_SIZE);

        responseTextArea.setEditable(false);
        responseTextArea.setPrefRowCount(100);
    }

    private String executeCommand(String command) {
        if ("П".equalsIgnoreCase(command)) {
            return "Команда выполнена\n";
        } else if ("С".equalsIgnoreCase(command)) {
            return "Команда выполнена\n";
        } else {
            return "Неизвестная команда\n";
        }
    }
}
