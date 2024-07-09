package com.example.daiquiri;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HabitatInfoController {

    @FXML
    private Label boyCountLabel;
    @FXML
    private Label girlCountLabel;

    @FXML
    private Label durationLabel;

    private boolean isOKClicked = false;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    public void initializeData(int boyCount, int girlCount, String duration) {
        boyCountLabel.setText(String.valueOf(boyCount));
        girlCountLabel.setText(String.valueOf(girlCount));
        durationLabel.setText(duration);
    }

    public boolean isOKClicked() {
        return isOKClicked;
    }

    @FXML
    private void handleOKButton() {
        isOKClicked = true;
        closeWindow();
    }

    @FXML
    private void handleCancelButton() {
        isOKClicked = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
