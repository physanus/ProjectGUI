package de.danielprinz.ProjectGUI.popupHandler;

import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.ProjectGUI.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SetingsBox {

    public static void display() {

        /*Stage window = new Stage();
        window.setTitle("Einstellungen");
        window.getIcons().add(new Image(Main.class.getResourceAsStream("delivery-truck.png")));
        window.initModality(Modality.APPLICATION_MODAL);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label consumptionLabel = new Label("Verbrauch [kw/h pro 100 km]:");
        consumptionLabel.setMinWidth(fontLoader.computeStringWidth(consumptionLabel.getText(), consumptionLabel.getFont()));
        GridPane.setConstraints(consumptionLabel, 0, 0);

        TextField consumptionTextField = new TextField();
        consumptionTextField.setPrefColumnCount(6);
        consumptionTextField.setText(String.valueOf(Main.SETTINGS_HANDLER.getConsumption(Direction.NORMAL)));
        // force the field to be numeric only
        consumptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                consumptionTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(consumptionTextField, 1, 0);


        Label decrementLabel = new Label("Energieersparnis bergab [%]:");
        decrementLabel.setMinWidth(fontLoader.computeStringWidth(decrementLabel.getText(), decrementLabel.getFont()));
        GridPane.setConstraints(decrementLabel, 0, 1);

        TextField decrementTextField = new TextField();
        decrementTextField.setPrefColumnCount(6);
        decrementTextField.setText(String.valueOf(100 - 100* Main.SETTINGS_HANDLER.getDecrementValue()));
        // force the field to be numeric only
        decrementTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                decrementTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(decrementTextField, 1, 1);


        Label incrementLabel = new Label("Energiemehraufwand bergauf [%]:");
        incrementLabel.setMinWidth(fontLoader.computeStringWidth(incrementLabel.getText(), incrementLabel.getFont()));
        GridPane.setConstraints(incrementLabel, 0, 2);

        TextField incrementTextField = new TextField();
        incrementTextField.setPrefColumnCount(6);
        incrementTextField.setText(String.valueOf(Main.SETTINGS_HANDLER.getIncrementValue() *100 - 100));
        // force the field to be numeric only
        incrementTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                incrementTextField.setText(oldValue);
            }
        });
        GridPane.setConstraints(incrementTextField, 1, 2);


        Button save = new Button("Speichern und schließen");
        GridPane.setConstraints(save, 1, 4);
        save.setOnAction(event -> {
            double consumption = Double.parseDouble(consumptionTextField.getText());
            double decrementValue = Double.parseDouble(decrementTextField.getText());
            double incrementValue = Double.parseDouble(incrementTextField.getText());

            Main.SETTINGS_HANDLER.setConsumption(consumption);
            Main.SETTINGS_HANDLER.setDecrementValue(1 - decrementValue/100.0); // 10
            Main.SETTINGS_HANDLER.setIncrementValue(1 + incrementValue/100.0); // 20

            window.close();
        });


        // Add everything to grid
        grid.getChildren().addAll(consumptionLabel, consumptionTextField, decrementLabel, decrementTextField, incrementLabel, incrementTextField, save);

        Scene scene = new Scene(grid, 360, 200);
        window.setScene(scene);
        window.showAndWait();*/

    }

}
