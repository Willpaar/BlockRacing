package gameengine.HelperFunctions;

import gameengine.Shapes.Shape;
import gameengine.Shapes.Square;


public class CollisionTypeFinder {
    public static Boolean isShapeAndObstacle(String a, String b) {
        if(TypeFinder.IsShape(a) && TypeFinder.IsWall(b)) {
            return true;
        }
        return false;
    }

    public static Boolean isShapeAndShape(String a, String b) {
        if(TypeFinder.IsShape(a) && TypeFinder.IsShape(b)) {
            return true;
        }
        return false;
    }
}
