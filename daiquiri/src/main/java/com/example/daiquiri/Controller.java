package com.example.daiquiri;

import com.example.daiquiri.Student.StudentBoy;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Controller {
    private Stage primaryStage;
    private Habitat habitat;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button currentObjectsButton;
    @FXML
    private Label timeLabel;
    @FXML
    private Pane simulationPane;
    @FXML
    private RadioButton showTimerRadioButton;
    @FXML
    private RadioButton hideTimerRadioButton;
    @FXML
    private ToggleGroup timerToggleGroup;
    @FXML
    private CheckBox showInfoCheckBox;
    @FXML
    private MenuItem menuStartButton;
    @FXML
    private MenuItem menuStopButton;
    @FXML
    private CheckMenuItem menuShowTimerCheckBox;
    @FXML
    private CheckMenuItem menuShowInfoCheckBox;
    @FXML
    private TextField boySpawnDelayField;
    @FXML
    private TextField girlSpawnDelayField;
    @FXML
    private TextField boyLifetimeField;
    @FXML
    private TextField girlLifetimeField;
    @FXML
    private ComboBox boyProbabilityComboBox;
    @FXML
    private Slider girlProbabilitySlider;
    @FXML
    private Button boyAIButton;
    @FXML
    private Button girlAIButton;
    public Habitat getHabitat() {
        return habitat;
    }
    @FXML
    private Button consoleButton;

    @FXML
    public void initialize(Stage primaryStage) {
        habitat = Habitat.getInstance(simulationPane, timeLabel, showInfoCheckBox);
        this.primaryStage = primaryStage;
        timeLabel.setText("00:00");
        rootPane.setOnKeyPressed(this::handleKeyPressed);
        rootPane.requestFocus();
        initBoyProbabilityComboBox();
        handleSetBoyProbability();
        girlProbabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSetGirlProbability();
        });
        //stopButton.setDisable(true);
        //menuStopButton.setVisible(false);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case B -> startSimulation();
            case E -> stopSimulation();
            case T -> toggleTimerVisability();
        }
    }

    @FXML
    private void handleStartButton() {
        startSimulation();
    }
    @FXML
    private void handleStopButton() {
        stopSimulation();
    }
    @FXML
    private void handleShowCurrentObjects() {
        habitat.showCurrentObjectsDialog(primaryStage);
    }
    @FXML
    private void handleOpenConsole() {
        habitat.openConsole();
    }
    @FXML
    private void handleShowTimerButton() {
        timeLabel.setVisible(true);
        menuShowTimerCheckBox.setSelected(true);
    }

    @FXML
    private void handleHideTimerButton() {
        timeLabel.setVisible(false);
        menuShowTimerCheckBox.setSelected(false);
    }
    @FXML
    private void handleToggleTimerVisability() {
        toggleTimerVisability();
    }
    @FXML
    private void handleBoyAIButton() {
        if(habitat.getBoyAIState()){
            boyAIButton.setText("Start");
            habitat.stopBoyAI();
        }else {
            boyAIButton.setText("Stop");
            habitat.startBoyAI();
        }
    }
    @FXML
    private void handleGirlAIButton() {
        if(habitat.getGirlAIState()){
            girlAIButton.setText("Start");
            habitat.stopGirlAI();
        }else {
            girlAIButton.setText("Stop");
            habitat.startGirlAI();
        }
    }
    @FXML
    private void handleSetBoyProbability() {
        if(!habitat.getSimulationState()){
            habitat.setBoyProbability(Integer.parseInt(boyProbabilityComboBox.getValue().toString().substring(0, boyProbabilityComboBox.getValue().toString().length() - 1)) / 100d);
        }
    }
    @FXML
    private void handleSetGirlProbability() {
        if(!habitat.getSimulationState()){
            habitat.setGirlProbability(girlProbabilitySlider.getValue() / 100d);
        }
    }
    @FXML
    private void handleShowInfoCheckBox() {
        if (showInfoCheckBox.isSelected()) {
            menuShowInfoCheckBox.setSelected(true);
        } else {
            menuShowInfoCheckBox.setSelected(false);
        }
    }

    @FXML
    private void handleMenuShowInfoCheckBox() {
        if (menuShowInfoCheckBox.isSelected()) {
            showInfoCheckBox.setSelected(true);
        } else {
            showInfoCheckBox.setSelected(false);
        }
    }

    private void startSimulation() {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        menuStartButton.setVisible(false);
        menuStopButton.setVisible(true);
        setSpawnDelayFields();
        setLifetimeFields();
        habitat.startSimulation();
    }

    private void stopSimulation() {
        if (habitat.stopSimulation()) {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            menuStartButton.setVisible(true);
            menuStopButton.setVisible(false);
        }
    }

    private void toggleTimerVisability() {
        timeLabel.setVisible(!timeLabel.isVisible());
        Toggle selectedToggle = timerToggleGroup.getSelectedToggle();
        if (selectedToggle == showTimerRadioButton) {
            hideTimerRadioButton.setSelected(true);
            menuShowTimerCheckBox.setSelected(false);
        } else if (selectedToggle == hideTimerRadioButton) {
            showTimerRadioButton.setSelected(true);
            menuShowTimerCheckBox.setSelected(true);
        }
    }

    private void setSpawnDelayFields() {
        if (!boySpawnDelayField.getText().matches("\\d+") || Integer.parseInt(boySpawnDelayField.getText()) < 1) {
            boySpawnDelayField.setText("5");
        }
        if (!girlSpawnDelayField.getText().matches("\\d+") || Integer.parseInt(girlSpawnDelayField.getText()) < 1) {
            girlSpawnDelayField.setText("5");
        }
        habitat.setBoySpawnDelay(Integer.parseInt(boySpawnDelayField.getText()));
        habitat.setGirlSpawnDelay(Integer.parseInt(girlSpawnDelayField.getText()));
    }

    private void setLifetimeFields() {
        if (!boyLifetimeField.getText().matches("\\d+") || Integer.parseInt(boyLifetimeField.getText()) < 1) {
            boyLifetimeField.setText("15");
        }
        if (!girlLifetimeField.getText().matches("\\d+") || Integer.parseInt(girlLifetimeField.getText()) < 1) {
            girlLifetimeField.setText("15");
        }
        habitat.setBoyLifeTime(Integer.parseInt(boyLifetimeField.getText()));
        habitat.setGirlLifeTime(Integer.parseInt(girlLifetimeField.getText()));
    }

    private void initBoyProbabilityComboBox() {
        for (int i = 0; i <= 100; i += 10) {
            boyProbabilityComboBox.getItems().add(i + "%");
        }
        boyProbabilityComboBox.setValue("50%");
    }
}