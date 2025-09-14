package gameengine.Popups;

import gameengine.Entities;
import gameengine.Shapes.Wall;
import gameengine.HelperFunctions.*;

public class EditWallPopup extends AddWallPopup {
    Wall wall;


    public EditWallPopup(Wall wall){
        super("Edit");
        this.wall = wall;
        nameField.setText(wall.name);
        xposSpinner.getValueFactory().setValue(wall.XPos);
        yposSpinner.getValueFactory().setValue(wall.YPos);
        lenSpinner.getValueFactory().setValue(wall.len);
        colorPicker.setValue(wall.Color);
        double normalizedAngle = ((wall.angle % 360) + 360) % 360;
        angSpinner.getValueFactory().setValue(normalizedAngle);
        angSpinner.getValueFactory().setValue(wall.angle);

    }

    @Override
    public void setConfirmButton(Entities entities){
        confirmButton.setOnAction(event -> {
        try {

            if(nameField.getText() == wall.name){
            }
            else if(!nameField.getText().isBlank()){
             wall.name = GetUniqueName.getUniqueName(nameField.getText(), entities, "wall");
            }
            wall.len = lenSpinner.getValue();
            wall.XPos = xposSpinner.getValue();
            wall.orgXPos = wall.XPos;
            wall.YPos = yposSpinner.getValue();
            wall.orgYPos = wall.YPos;
            wall.Color = colorPicker.getValue();
            wall.angle = angSpinner.getValue() % 360;
            
        } catch (NumberFormatException ex) {
            ex.printStackTrace(); 
        }

        stage.show();
        });
    }
    
}
