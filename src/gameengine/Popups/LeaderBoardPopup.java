package gameengine.Popups;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import gameengine.Entities;
import gameengine.Shapes.Shape;

public class LeaderBoardPopup {

    public static void show() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Leaderboard");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #222;");

        if (Entities.leaderBoard.isEmpty()) {
            Label noWinners = new Label("No winners yet!");
            noWinners.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            layout.getChildren().add(noWinners);
        } else {
            int rank = 1;
            for (Shape s : Entities.leaderBoard) {
                String shapeInfo = rank + ". " + s.getClass().getSimpleName() + ": " + s.name;
                Label shapeLabel = new Label(shapeInfo);
                shapeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                layout.getChildren().add(shapeLabel);
                rank++;
            }
        }

        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
