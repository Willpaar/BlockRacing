package gameengine.Popups;

import gameengine.Entities;
import gameengine.Shapes.Wall;
import gameengine.typedefs.Shapes_TypeDefs;
import gameengine.HelperFunctions.*;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class AddWallPopup extends AddObjectPopup{
    public Spinner<Double> lenSpinner;

    public AddWallPopup() {
        super("Wall", "Add");
    }

    public AddWallPopup(String formType) {
        super("Wall", formType);
    }


    @Override
    public void assembleGeneral() {
        Label lenLabel = new Label("Length:");
        lenSpinner = new Spinner<>(1.0, 2000.0, 200.0, 10);
        lenSpinner.setPrefWidth(100);
        lenSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, lenLabel, lenSpinner));

    }

    @Override
    public void setConfirmButton(Entities entities){
        colorPicker.setValue(Color.BLACK);
        
        confirmButton.setOnAction(event -> {
        try {
            String shapeName = GetUniqueName.getUniqueName(nameField.getText(), entities, "Wall");

            Wall w = new Wall(Shapes_TypeDefs.WALL, shapeName, lenSpinner.getValue(), 
            xposSpinner.getValue(), yposSpinner.getValue(), 
            colorPicker.getValue(), angSpinner.getValue());
            entities.addShape(w);

        } catch (NumberFormatException ex) {
            ex.printStackTrace(); 
        }

        stage.show();
    });

    }

    @Override
    public void assembleAdvanced() {
        
    }
    
}
