package gameengine.Shapes;
import javafx.scene.paint.Color;
import gameengine.Entities;
import gameengine.typedefs.*;


public abstract class Obstacle extends Shape {
    
    public Obstacle() {}

    public Obstacle(String type, String name, double XPos, double YPos, Color color, double angle){
        super(type, name, XPos, YPos, 0.0, 0.0, color, angle);
    }

    public final double XVel = 0;
    public final double YVel = 0;


    
    @Override
    public void update(Entities world) {
        //obstacles dont move
    }
}
