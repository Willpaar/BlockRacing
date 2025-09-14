package gameengine.Popups;

import gameengine.Entities;
import gameengine.Shapes.Square;
import gameengine.typedefs.Shapes_TypeDefs;
import gameengine.HelperFunctions.*;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;


public class AddSquarePopup extends AddShapePopup {
    public Spinner<Double> xlenSpinner;
    public Spinner<Double> ylenSpinner;

    public AddSquarePopup(){
        super("Square", "Add");
    }

    public AddSquarePopup(String formType){
        super("Square", formType);
    } 

    public AddSquarePopup(String type, String formType){
        super(type, formType);
    } 

    @Override
    public void assembleGeneral(){

        Label xlenLabel = new Label("Width:");
        xlenSpinner = new Spinner<>(1.0, 100.0, 50.0, 5);
        xlenSpinner.setPrefWidth(100);
        xlenSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, xlenLabel, xlenSpinner));

        Label ylenLabel = new Label("Height:");
        ylenSpinner = new Spinner<>(1.0, 100.0, 50.0, 5);
        ylenSpinner.setPrefWidth(100);
        ylenSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, ylenLabel, ylenSpinner));
    }

    @Override
    public void setConfirmButton(Entities entities){
        confirmButton.setOnAction(event -> {
        try {
            String shapeName = GetUniqueName.getUniqueName(nameField.getText(), entities, "Square");

            Square s = new Square(Shapes_TypeDefs.SQUARE,shapeName, 
            xlenSpinner.getValue(), ylenSpinner.getValue(), 
            xposSpinner.getValue(), yposSpinner.getValue(), 
            xvelSpinner.getValue(), yvelSpinner.getValue(), 
            colorPicker.getValue(), angSpinner.getValue());
            if(entities.running){
                s.XVel = s.storedXVel;
                s.YVel = s.storedYVel;
            }
            entities.addShape(s);

            s.wallAndDup  = wallAndDupOption.isSelected();
            s.bounceAndDel = bounceAndDelOption.isSelected(); 
            s.createOnDelete = bounceAndDelOption.isSelected();

        } catch (NumberFormatException ex) {
            ex.printStackTrace(); 
        }

        stage.show();
    });

    }
}
