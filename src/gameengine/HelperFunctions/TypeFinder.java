package gameengine.HelperFunctions;

import gameengine.Shapes.Shape;
import gameengine.typedefs.Shapes_TypeDefs;

public class TypeFinder {
    public static boolean IsObstacle(String s){
        return Shapes_TypeDefs.WALL.equals(s) || Shapes_TypeDefs.FINISH.equals(s);
    }

    public static boolean IsWall(String s){
        return Shapes_TypeDefs.WALL.equals(s);
    }

    public static boolean IsShape(String s){
        return Shapes_TypeDefs.SQUARE.equals(s);
    }
}
