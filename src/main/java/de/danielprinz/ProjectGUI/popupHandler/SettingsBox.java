package de.danielprinz.ProjectGUI.popupHandler;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import purejavacomm.CommPortIdentifier;

import java.util.List;
import java.util.stream.Collectors;

public class SettingsBox {

    public static void display(String title) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        //window.initStyle(StageStyle.DECORATED); // UTILITY
        if(SettingsHandler.checkAvailableResources())
            window.getIcons().add(SettingsHandler.APP_ICON);

        window.setTitle(title);
        window.setMinWidth(450);
        window.setMinHeight(290);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();


        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        /*
         * PORT
         */
        Label portLabel = new Label("Port:");
        portLabel.setMinWidth(fontLoader.computeStringWidth(portLabel.getText(), portLabel.getFont()));
        GridPane.setConstraints(portLabel, 0, 0);

        List<CommPortIdentifier> allPorts = Main.getConnectionHandler().getAllPorts();
        ObservableList<String> options = FXCollections.observableArrayList(allPorts.stream().map(CommPortIdentifier::getName).collect(Collectors.toList()));
        ComboBox<String> portComboBox = new ComboBox<>(options);

        portComboBox.valueProperty().setValue(SettingsHandler.PORT == null ? "---" : SettingsHandler.PORT.getName());
        portComboBox.setOnShowing(e -> {
            List<CommPortIdentifier> allPortsNew = Main.getConnectionHandler().getAllPorts();
            ObservableList<String> optionsUpdated = FXCollections.observableArrayList(allPortsNew.stream().map(CommPortIdentifier::getName).collect(Collectors.toList()));
            portComboBox.setItems(optionsUpdated);

            if(!allPortsNew.stream().map(CommPortIdentifier::getName).collect(Collectors.toList()).contains(portComboBox.getValue())) {
                SettingsHandler.PORT = allPorts.isEmpty() ? null : allPorts.get(0);
                portComboBox.valueProperty().setValue(SettingsHandler.PORT == null ? "---" : SettingsHandler.PORT.getName());

            }
        });

        GridPane.setConstraints(portComboBox, 1, 0);


        /*
         * preview image width
         */
        Label prevImgWidthLabel = new Label("Preview image max width:");
        prevImgWidthLabel.setMinWidth(fontLoader.computeStringWidth(prevImgWidthLabel.getText(), prevImgWidthLabel.getFont()));
        GridPane.setConstraints(prevImgWidthLabel, 0, 1);

        TextField prevImgWidthTextField = new TextField(String.valueOf(SettingsHandler.PREVIEW_IMAGE_MAX_WIDTH));
        // force the field to be numeric only
        prevImgWidthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                prevImgWidthTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(prevImgWidthTextField, 1, 1);

        /*
         * preview image height
         */
        Label prevImgHeightLabel = new Label("Preview image max height:");
        prevImgHeightLabel.setMinWidth(fontLoader.computeStringWidth(prevImgHeightLabel.getText(), prevImgHeightLabel.getFont()));
        GridPane.setConstraints(prevImgHeightLabel, 0, 2);

        TextField prevImgHeightTextField = new TextField(String.valueOf(SettingsHandler.PREVIEW_IMAGE_MAX_HEIGHT));
        // force the field to be numeric only
        prevImgHeightTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                prevImgHeightTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(prevImgHeightTextField, 1, 2);

        /*
         * Handdrawing movement speed
         */
        Label handdrawingMovementSpeedLabel = new Label("Handdrawing movement speed:");
        handdrawingMovementSpeedLabel.setMinWidth(fontLoader.computeStringWidth(handdrawingMovementSpeedLabel.getText(), handdrawingMovementSpeedLabel.getFont()));
        GridPane.setConstraints(handdrawingMovementSpeedLabel, 0, 3);

        TextField handdrawingMovementSpeedTextField = new TextField(String.valueOf(SettingsHandler.HANDDRAWING_MOVEMENT_SPEED));
        // force the field to be numeric only
        handdrawingMovementSpeedTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                handdrawingMovementSpeedTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(handdrawingMovementSpeedTextField, 1, 3);

        /*
         * Serial connection timeout
         */
        Label serialConnectionTimeoutLabel = new Label("Serial connection timeout:");
        handdrawingMovementSpeedLabel.setMinWidth(fontLoader.computeStringWidth(serialConnectionTimeoutLabel.getText(), serialConnectionTimeoutLabel.getFont()));
        GridPane.setConstraints(serialConnectionTimeoutLabel, 0, 4);

        TextField serialConnectionTimeoutTextField = new TextField(String.valueOf(SettingsHandler.SERIAL_CONNECTION_TIMEOUT));
        // force the field to be numeric only
        serialConnectionTimeoutTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                serialConnectionTimeoutTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(serialConnectionTimeoutTextField, 1, 4);

        /*
         * Serial connection commands sent per second
         */
        Label serialConnectionCommandsSentPerSecondLabel = new Label("Commands sent per second:");
        serialConnectionCommandsSentPerSecondLabel.setMinWidth(fontLoader.computeStringWidth(serialConnectionCommandsSentPerSecondLabel.getText(), serialConnectionCommandsSentPerSecondLabel.getFont()));
        GridPane.setConstraints(serialConnectionCommandsSentPerSecondLabel, 0, 5);

        TextField serialConnectionCommandsSentPerSecondTextField = new TextField(String.valueOf(SettingsHandler.SERIAL_CONNECTION_COMMANDS_SENT_PER_SECOND));
        // force the field to be numeric only
        serialConnectionCommandsSentPerSecondTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                serialConnectionCommandsSentPerSecondTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(serialConnectionCommandsSentPerSecondTextField, 1, 5);








        Button save = new Button("Save and close");
        GridPane.setConstraints(save, 1, 6);
        save.setOnAction(event -> {
            SettingsHandler.PORT = allPorts.get(portComboBox.getSelectionModel().getSelectedIndex());
            SettingsHandler.PREVIEW_IMAGE_MAX_WIDTH = prevImgWidthTextField.getText().equalsIgnoreCase("") ? 650 : Integer.parseInt(prevImgWidthTextField.getText());
            SettingsHandler.PREVIEW_IMAGE_MAX_HEIGHT = prevImgHeightTextField.getText().equalsIgnoreCase("") ? 750 : Integer.parseInt(prevImgHeightTextField.getText());
            SettingsHandler.HANDDRAWING_MOVEMENT_SPEED = handdrawingMovementSpeedTextField.getText().equalsIgnoreCase("") ? 1.2 : Double.parseDouble(handdrawingMovementSpeedTextField.getText());
            SettingsHandler.SERIAL_CONNECTION_TIMEOUT = serialConnectionTimeoutTextField.getText().equalsIgnoreCase("") ? 2000 : Integer.parseInt(serialConnectionTimeoutTextField.getText());
            SettingsHandler.SERIAL_CONNECTION_COMMANDS_SENT_PER_SECOND = serialConnectionCommandsSentPerSecondTextField.getText().equalsIgnoreCase("") ? 20 : Integer.parseInt(serialConnectionCommandsSentPerSecondTextField.getText());

            window.close();
        });


        // Add everything to grid
        grid.getChildren().addAll(portLabel, portComboBox, prevImgWidthLabel, prevImgWidthTextField, prevImgHeightLabel, prevImgHeightTextField,
                handdrawingMovementSpeedLabel, handdrawingMovementSpeedTextField, serialConnectionTimeoutLabel, serialConnectionTimeoutTextField,
                serialConnectionCommandsSentPerSecondLabel, serialConnectionCommandsSentPerSecondTextField, save);


        Scene scene = new Scene(grid, 360, 200);

        KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
        scene.setOnKeyPressed(e -> {
            if(enter.match(e)){
                // Enter was pressed. Simulate a save click
                save.fire();
            }
        });

        window.setScene(scene);
        window.showAndWait();

    }

}
