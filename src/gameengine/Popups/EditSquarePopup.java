package gameengine.Popups;

import gameengine.Entities;
import gameengine.Shapes.Square;
import gameengine.HelperFunctions.*;

public class EditSquarePopup extends AddSquarePopup {
    public Square square;
    
    public EditSquarePopup(Square square){
        super("Edit");
        this.square = square;
        nameField.setText(square.name);
        xposSpinner.getValueFactory().setValue(square.XPos);
        yposSpinner.getValueFactory().setValue(square.YPos);
        xlenSpinner.getValueFactory().setValue(square.XLen);
        ylenSpinner.getValueFactory().setValue(square.YLen);
        xvelSpinner.getValueFactory().setValue(square.storedXVel);
        yvelSpinner.getValueFactory().setValue(square.storedYVel);
        colorPicker.setValue(square.Color);
        double normalizedAngle = ((square.angle % 360) + 360) % 360;
        angSpinner.getValueFactory().setValue(normalizedAngle);
        wallAndDupOption.setSelected(square.wallAndDup);
        bounceAndDelOption.setSelected(square.bounceAndDel);

    }

    @Override
    public void setConfirmButton(Entities entities){
        confirmButton.setOnAction(event -> {
        try {

            if(nameField.getText() == square.name){
            }
            else if(!nameField.getText().isBlank()){
             square.name = GetUniqueName.getUniqueName(nameField.getText(), entities, "Square");
            }
            square.XLen = xlenSpinner.getValue();
            square.YLen = ylenSpinner.getValue();
            square.XPos = xposSpinner.getValue();
            square.orgXPos = square.XPos;
            square.YPos = yposSpinner.getValue();
            square.orgYPos = square.YPos;
            square.storedXVel = xvelSpinner.getValue();
            square.storedYVel = yvelSpinner.getValue();
            square.Color = colorPicker.getValue();
            square.angle = angSpinner.getValue() % 360;

            square.wallAndDup  = wallAndDupOption.isSelected();
            square.bounceAndDel = bounceAndDelOption.isSelected(); 
            square.createOnDelete = bounceAndDelOption.isSelected();
            
        } catch (NumberFormatException ex) {
            ex.printStackTrace(); 
        }

        stage.show();
        });
    }
}
