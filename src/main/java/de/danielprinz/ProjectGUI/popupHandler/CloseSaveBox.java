package de.danielprinz.ProjectGUI.popupHandler;

import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CloseSaveBox {

    private static CloseSaveBoxResult answer;

    public static CloseSaveBoxResult display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        //window.initStyle(StageStyle.DECORATED); // UTILITY
        if(SettingsHandler.checkAvailableResources())
            window.getIcons().add(SettingsHandler.APP_ICON);

        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        Font font = new Font(label.getFont().getName(), 15);
        label.setFont(font);
        label.setTextFill(Color.web("#039"));


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        //Create two buttons
        Button saveButton = new Button(Strings.SAVE_CHANGES_ANSWER_YES.format());
        Button nosaveButton = new Button(Strings.SAVE_CHANGES_ANSWER_NO.format());
        Button cancelButton = new Button(Strings.SAVE_CHANGES_ANSWER_CANCEL.format());

        //Clicking will set answer and close window
        saveButton.setOnAction(e -> {
            answer = CloseSaveBoxResult.SAVE;
            window.close();
        });
        nosaveButton.setOnAction(e -> {
            answer = CloseSaveBoxResult.NOSAVE;
            window.close();
        });
        cancelButton.setOnAction(e -> {
            answer = CloseSaveBoxResult.CANCEL;
            window.close();
        });

        gridPane.add(saveButton, 0, 0);
        gridPane.add(nosaveButton, 1, 0);
        gridPane.add(cancelButton, 2, 0);
        gridPane.setAlignment(Pos.CENTER);

        VBox layout = new VBox(0);
        layout.setPadding(new Insets(10, 10, 10, 10));

        //Add buttons
        layout.getChildren().addAll(label, gridPane);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        return answer;
    }

}
