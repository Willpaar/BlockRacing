package gameengine.Popups;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SavePresetPopup {
    public Stage stage = new Stage();
    public VBox layout = new VBox(10);
    private Scene popupScene;

    public TextField nameField;
    public Button confirmButton; 

    public SavePresetPopup(){
        stage.setTitle("Save Preset");

        Label nameLabel = new Label("Name:");
        nameField = new TextField();   
        nameField.setPrefWidth(100);
        layout.getChildren().addAll(new HBox(10, nameLabel, nameField));

        confirmButton = new Button("Save Preset");
        confirmButton.setPrefWidth(200);

        layout.getChildren().add(confirmButton);

        popupScene = new Scene(layout, 500, 500);   
        stage.setScene(popupScene);
        stage.show();
    }
}
