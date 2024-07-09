package com.example.daiquiri;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HabitatConsoleController {
    @FXML
    private TextField commandTextField;

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button executeButton;
    @FXML
    private HBox hbox;
    private Controller controller;

    public void initialize() {
        commandTextField.setPromptText("Введите команду здесь");
        //commandTextArea.setPrefRowCount(1);

        executeButton.setOnAction(event -> {
            String command = commandTextField.getText();
            responseTextArea.appendText(command + "\n");
            String response = executeCommand(command);
            responseTextArea.appendText(response);
            commandTextField.clear();
        });

        HBox.setHgrow(commandTextField, Priority.ALWAYS);
        HBox.setHgrow(executeButton, Priority.NEVER);
        executeButton.setMinWidth(Button.USE_PREF_SIZE);

        responseTextArea.setEditable(false);
        responseTextArea.setPrefRowCount(100);
    }
    public void setController(Controller controller){
        this.controller = controller;
    }

    private String executeCommand(String command) {
        if ("П".equalsIgnoreCase(command)) {
            if(!controller.isTimerVisible()) {
                controller.consoleShowTimer();
                return "Команда выполнена\n";
            }else {
                return "Таймер уже показывается\n";
            }
        } else if ("С".equalsIgnoreCase(command)) {
            if(controller.isTimerVisible()) {
                controller.consoleHideTimer();
                return "Команда выполнена\n";
            }else {
                return "Таймер уже скрыт\n";
            }
        } else {
            return "Неизвестная команда\n";
        }
    }
}
