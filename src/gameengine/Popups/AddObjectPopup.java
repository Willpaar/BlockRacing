package gameengine.Popups;

import gameengine.Entities;
import gameengine.Shapes.Shape;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class AddObjectPopup {
    public Stage stage = new Stage();
    public VBox layout = new VBox(10);
    public VBox generalLayout = new VBox(10);
    public VBox advancedLayout = new VBox(10);  // made public
    private Scene popupScene;
    public TextField nameField;

    public String formType, type;
    
    public Spinner<Double> xposSpinner;
    public Spinner<Double> yposSpinner;
    public Spinner<Double> angSpinner;

    public ColorPicker colorPicker;
    public Button confirmButton; 
    
    protected AddObjectPopup(String type, String formType){
        this.type = type;
        this.formType = formType;
        stage.setTitle(formType + " " + type);
        AddBeginningFields();
        assembleGeneral();
        AddEndFields();
        assembleAdvanced();
    }

    public abstract void assembleGeneral();
    public abstract void setConfirmButton(Entities entities);
    public abstract void assembleAdvanced();

    protected void AddBeginningFields(){
        // Create TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // General Tab
        Tab generalTab = new Tab("General");

        // Name
        Label nameLabel = new Label("Name:");
        nameField = new TextField();   
        nameField.setPrefWidth(100);
        generalLayout.getChildren().addAll(new HBox(10, nameLabel, nameField));

        // X Position
        Label xposLabel = new Label("X Position:");
        xposSpinner = new Spinner<>(0.0, 5000, 100, 10);
        xposSpinner.setPrefWidth(100);
        xposSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, xposLabel, xposSpinner));

        // Y Position
        Label yposLabel = new Label("Y Position:");
        yposSpinner = new Spinner<>(0.0, 5000, 100, 10);
        yposSpinner.setPrefWidth(100);
        yposSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, yposLabel, yposSpinner));

        // Angle
        Label angLabel = new Label("Angle:");
        angSpinner = new Spinner<>(0.0, 360.0, 0.0, 1);
        angSpinner.setPrefWidth(100);
        angSpinner.setEditable(true);
        generalLayout.getChildren().addAll(new HBox(10, angLabel, angSpinner));

        generalTab.setContent(generalLayout);

        // Advanced Tab
        Tab advancedTab = new Tab("Advanced");
        advancedTab.setContent(advancedLayout);  // public layout used

        // Add tabs to TabPane
        tabPane.getTabs().addAll(generalTab, advancedTab);

        // Replace main layout with TabPane
        layout.getChildren().clear();
        layout.getChildren().add(tabPane);
    }

    protected void AddEndFields(){
        // Color
        Label colorLabel = new Label("Color:");
        colorPicker = new ColorPicker(Color.RED); 
        generalLayout.getChildren().add(new HBox(10, colorLabel, colorPicker));

        // Confirm Button
        String buttonMessage = formType + " " + type;
        confirmButton = new Button(buttonMessage);
        confirmButton.setPrefWidth(200);
        layout.getChildren().add(confirmButton);
    }

    public void showScene(){
        popupScene = new Scene(layout, 500, 500);   
        stage.setScene(popupScene);
        stage.show();
    }
}
