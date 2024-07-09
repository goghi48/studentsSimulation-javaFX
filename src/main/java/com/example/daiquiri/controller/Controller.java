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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Controller {
    private Stage primaryStage;
    private Habitat habitat;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private AnchorPane zaslonkaPane;
    @FXML
    private VBox loginBox;
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
    private ComboBox boyAIPriorityComboBox;
    @FXML
    private ComboBox girlAIPriorityComboBox;
    @FXML
    private Slider girlProbabilitySlider;
    @FXML
    private Button boyAIButton;
    @FXML
    private Button girlAIButton;
    @FXML
    private Button saveStudentsButton;
    @FXML
    private Button loadStudentsButton;
    public Habitat getHabitat() {
        return habitat;
    }
    @FXML
    private Button consoleButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private Label errorText;
    @FXML
    private TextFlow connectedClients;
    @FXML
    private ComboBox<String> connectComboBox;
    private ObjectOutputStream out;
    private String username;
    private Socket socket;

    @FXML
    public void initialize(Stage primaryStage) {
        habitat = Habitat.getInstance(simulationPane, timeLabel, showInfoCheckBox);
        this.primaryStage = primaryStage;
        timeLabel.setText("00:00");
        rootPane.setOnKeyPressed(event -> {
            try {
                handleKeyPressed(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        rootPane.requestFocus();
        initBoyProbabilityComboBox();
        initAIPriorityComboBoxes();
/*        handleSetBoyProbability();
        handleSetBoyAIPriority();
        handleSetGirlAIPriority();*/
        girlProbabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSetGirlProbability();
        });
        habitat.loadSettings();
        boySpawnDelayField.setText(String.valueOf(habitat.getBoySpawnDelay()));
        girlSpawnDelayField.setText(String.valueOf(habitat.getGirlSpawnDelay()));
        boyLifetimeField.setText(String.valueOf(habitat.getBoyLifeTime()));
        girlLifetimeField.setText(String.valueOf(habitat.getGirlLifeTime()));
        girlProbabilitySlider.setValue(habitat.getGirlProbability() * 100);
        boyProbabilityComboBox.setValue(String.valueOf((int)(habitat.getBoyProbability() * 100)) + '%');
        boyAIPriorityComboBox.setValue(String.valueOf(habitat.getBoyAIPriority()));
        girlAIPriorityComboBox.setValue(String.valueOf(habitat.getGirlAIPriority()));
        if(!habitat.isHabitatTimerVisible()){
            consoleHideTimer();
        }
        //stopButton.setDisable(true);
        //menuStopButton.setVisible(false);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) throws IOException {
        switch (event.getCode()) {
            case B -> startSimulation();
            case E -> stopSimulation();
            case T -> toggleTimerVisability();
        }
    }

    @FXML
    private void handleStartButton() throws IOException {
        startSimulation();
    }
    @FXML
    private void handleStopButton() throws IOException {
        stopSimulation();
    }
    @FXML
    private void handleShowCurrentObjects() {
        habitat.showCurrentObjectsDialog(primaryStage);
    }
    @FXML
    private void handleOpenConsole() {
        habitat.openConsole(this);
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
    private void handleSetBoyAIPriority() {
        if(!habitat.getSimulationState()){
            habitat.setBoyAIPriority(Integer.parseInt(boyAIPriorityComboBox.getValue().toString()));
            System.out.println(habitat.getBoyAIPriority());
        }
    }
    @FXML
    private void handleSetGirlAIPriority() {
        if(!habitat.getSimulationState()){
            habitat.setGirlAIPriority(Integer.parseInt(girlAIPriorityComboBox.getValue().toString()));
            System.out.println(habitat.getGirlAIPriority());
        }
    }
    @FXML
    private void handleSetGirlProbability() {
        if(!habitat.getSimulationState()){
            habitat.setGirlProbability(girlProbabilitySlider.getValue() / 100d);
        }
    }
    @FXML
    private void handleSaveStudents() {
        habitat.saveToFile();
    }
    @FXML
    private void handleLoadStudents() throws IOException {
        if(habitat.getSimulationState()){
            stopSimulation();
        }
        habitat.loadFromFile();
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
            showInfoCheckBox.setSelected(false);//

        }
    }
    @FXML
    public void handleConnectToServer() {
        String serverIP = ipTextField.getText();
        int serverPort = Integer.parseInt(portTextField.getText());
        username = usernameTextField.getText();

        new Thread(() -> {
            try {
                socket = new Socket(serverIP, serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Отправляем имя пользователя
                out.writeObject(new UsernameInfo(username));

                Platform.runLater(() -> {
                    zaslonkaPane.setVisible(false);
                });

                while (true) {
                    TransInfo info = (TransInfo) in.readObject();
                    if (info instanceof UserListInfo) {
                        UserListInfo userListInfo = (UserListInfo) info;
                        Platform.runLater(() -> updateConnectedClients(userListInfo.getUsernames()));
                    } else if (info instanceof SimulationStatusInfo) {
                        SimulationStatusInfo simInfo = (SimulationStatusInfo) info;
                        if (simInfo.isStart()) {
                            Platform.runLater(() -> {
                                startSimulation();
                            });
                        } else {
                            Platform.runLater(() -> {
                                stopSimulation();
                            });
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> errorText.setText("Ошибка подключения к серверу"));
            }
        }).start();
    }

    private void updateConnectedClients(String[] clients) {
        connectedClients.getChildren().clear();
        connectComboBox.getItems().clear();
        connectComboBox.getItems().add("");
        for (String client : clients) {
            if (client.equals(username)) {
                Text youText = new Text("(you) \n");
                youText.setStyle("-fx-fill: red;");
                connectedClients.getChildren().addAll(new Text(client), youText);
            } else {
                connectedClients.getChildren().add(new Text(client + "\n"));
                connectComboBox.getItems().add(client);
            }
        }
    }
    private boolean isChoosingEachOther(String selectedClient) {
        String otherClient = connectComboBox.getSelectionModel().getSelectedItem();
        return otherClient != null && !otherClient.isEmpty() && otherClient.equals(selectedClient);
    }
    public void consoleShowTimer() {
        timeLabel.setVisible(true);
        showTimerRadioButton.setSelected(true);
        menuShowTimerCheckBox.setSelected(true);
    }
    public void consoleHideTimer() {
        timeLabel.setVisible(false);
        hideTimerRadioButton.setSelected(true);
        menuShowTimerCheckBox.setSelected(false);
    }
    public void disconnect() {
        // Закрываем потоки и сокет
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Удаляем текущего клиента из списка подключенных клиентов
        Platform.runLater(() -> {
            connectedClients.getChildren().clear();
            connectComboBox.getItems().clear();
        });
        System.out.printf("aboba");
    }
    public boolean isTimerVisible() {
        return timeLabel.isVisible();
    }
    private void startSimulation() {
        String selectedClient = connectComboBox.getValue();
        if (selectedClient != null && !selectedClient.isEmpty() && !selectedClient.equals(username)) {
            try {
                out.writeObject(new SimulationStatusInfo(true, selectedClient));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        localStartSimulation();
    }

    private void stopSimulation() {
        String selectedClient = connectComboBox.getValue();
        if (selectedClient != null && !selectedClient.isEmpty() && !selectedClient.equals(username)) {
            try {
                out.writeObject(new SimulationStatusInfo(false, selectedClient));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        localStopSimulation();
    }


    private void localStartSimulation() {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        menuStartButton.setVisible(false);
        menuStopButton.setVisible(true);
        setSpawnDelayFields();
        setLifetimeFields();
        habitat.startSimulation();
    }

    private void localStopSimulation() {
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
    private void initAIPriorityComboBoxes() {
        for (int priority = 1; priority <= 10; priority++) {
            boyAIPriorityComboBox.getItems().add(priority);
            girlAIPriorityComboBox.getItems().add(priority);
        }
        boyAIPriorityComboBox.setValue("5");
        girlAIPriorityComboBox.setValue("5");
    }
    public void saveHabitatSettings() {
        setSpawnDelayFields();
        setLifetimeFields();
        habitat.saveSettings();
    }
}