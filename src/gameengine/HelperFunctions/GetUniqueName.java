package gameengine.HelperFunctions;

import gameengine.Entities;
import gameengine.Shapes.Shape;

public class GetUniqueName {
    public static String getUniqueName(String baseName, Entities entities, String defString) {
        if (baseName == null || baseName.trim().isEmpty()) {
            baseName = defString;
        }

        String uniqueName = baseName;
        int counter = 1;

        boolean exists = true;
        while (exists) {
            exists = false;
            for (Shape shape : entities.Shapes) {
                if (shape.name.equals(uniqueName)) { 
                    exists = true;
                    uniqueName = baseName + "(" + counter + ")";
                    counter++;
                    break; 
                }
            }
        }

        return uniqueName;
}
}
