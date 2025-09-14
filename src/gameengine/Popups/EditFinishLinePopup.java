package gameengine.Popups;

import gameengine.Entities;
import gameengine.HelperFunctions.GetUniqueName;
import gameengine.Shapes.FinishLine;

public class EditFinishLinePopup extends AddFinishLinePopup {
    public FinishLine finishLine;

    public EditFinishLinePopup(FinishLine finishLine){
        super("Edit");
        this.finishLine = finishLine;
        nameField.setText(finishLine.name);
        xposSpinner.getValueFactory().setValue(finishLine.XPos);
        yposSpinner.getValueFactory().setValue(finishLine.YPos);
        xlenSpinner.getValueFactory().setValue(finishLine.XLen);
        ylenSpinner.getValueFactory().setValue(finishLine.YLen);
        colorPicker.setValue(finishLine.Color);
        angSpinner.getValueFactory().setValue(finishLine.angle);

    }

    @Override
    public void setConfirmButton(Entities entities){
        confirmButton.setOnAction(event -> {
        try {

            if(nameField.getText() == finishLine.name){
            }
            else if(!nameField.getText().isBlank()){
             finishLine.name = GetUniqueName.getUniqueName(nameField.getText(), entities, "finishLine");
            }
            finishLine.XLen = xlenSpinner.getValue();
            finishLine.YLen = ylenSpinner.getValue();
            finishLine.XPos = xposSpinner.getValue();
            finishLine.orgXPos = finishLine.XPos;
            finishLine.YPos = yposSpinner.getValue();
            finishLine.orgYPos = finishLine.YPos;
            finishLine.Color = colorPicker.getValue();
            double normalizedAngle = ((finishLine.angle % 360) + 360) % 360;
            angSpinner.getValueFactory().setValue(normalizedAngle);
            finishLine.angle = angSpinner.getValue() % 360;
            
        } catch (NumberFormatException ex) {
            ex.printStackTrace(); 
        }

        stage.show();
        });
    }
}
