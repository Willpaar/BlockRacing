package gameengine.Popups;
import gameengine.Entities;
import gameengine.Shapes.Shape;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class AddShapePopup extends AddObjectPopup{  
    public Spinner<Double> xvelSpinner;
    public Spinner<Double> yvelSpinner;
    CheckBox wallAndDupOption, bounceAndDelOption;
    
    protected AddShapePopup(String type, String formType){
        super(type,formType);        
    }

    @Override
    protected void AddBeginningFields(){
        super.AddBeginningFields();

        Label xvelLabel = new Label("X Velocity:");
        xvelSpinner = new Spinner<>(-100.0, 100.0, 1.0, 10);
        xvelSpinner.setPrefWidth(100);
        xvelSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, xvelLabel, xvelSpinner));

        Label yvelLabel = new Label("Y Velocity:");
        yvelSpinner = new Spinner<>(-100.0, 100.0, 1.0, 1);
        yvelSpinner.setPrefWidth(100);
        yvelSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, yvelLabel, yvelSpinner));
    }

    @Override
    public void assembleAdvanced(){
        wallAndDupOption = new CheckBox("Bounce Off Wall and Duplicate");
        advancedLayout.getChildren().add(wallAndDupOption);

        bounceAndDelOption = new CheckBox("Bounce and Destroy");
        advancedLayout.getChildren().add(bounceAndDelOption);
    }
}
