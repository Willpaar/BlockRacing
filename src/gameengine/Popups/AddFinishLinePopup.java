package gameengine.Popups;

import gameengine.Entities;
import gameengine.HelperFunctions.GetUniqueName;
import gameengine.Shapes.FinishLine;
import gameengine.Shapes.Square;
import gameengine.typedefs.Shapes_TypeDefs;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class AddFinishLinePopup extends AddObjectPopup{
    public Spinner<Double> xlenSpinner;
    public Spinner<Double> ylenSpinner;

    public AddFinishLinePopup(){
        super("Finish Line", "Add");
    }

    public AddFinishLinePopup(String formType){
        super("Finish Line", formType);
    }

    @Override
    public void assembleGeneral(){
        Label xlenLabel = new Label("Width:");
        xlenSpinner = new Spinner<>(1.0, 1000.0, 50.0, 5);
        xlenSpinner.setPrefWidth(100);
        xlenSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, xlenLabel, xlenSpinner));

        Label ylenLabel = new Label("Height:");
        ylenSpinner = new Spinner<>(1.0, 1000.0, 50.0, 5);
        ylenSpinner.setPrefWidth(100);
        ylenSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, ylenLabel, ylenSpinner));

    }

    @Override
    public void setConfirmButton(Entities entities){
        colorPicker.setValue(Color.BLACK);

        confirmButton.setOnAction(event -> {
        try {
            String shapeName = GetUniqueName.getUniqueName(nameField.getText(), entities, "Square");

            FinishLine f = new FinishLine(Shapes_TypeDefs.FINISH,shapeName, 
            xlenSpinner.getValue(), ylenSpinner.getValue(), 
            xposSpinner.getValue(), yposSpinner.getValue(), 
            colorPicker.getValue(), angSpinner.getValue());
 
            entities.addShape(f);

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
