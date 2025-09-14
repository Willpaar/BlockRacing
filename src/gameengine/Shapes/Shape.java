package gameengine.Shapes;
import gameengine.typedefs.*;
import gameengine.Entities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)


public abstract class Shape {
    public String name, type;
    public double XPos, YPos, XVel, YVel, storedXVel, storedYVel, orgXPos, orgYPos, angle;

    public boolean isSelected,ignoreCollision,finished,finishing,
    wallAndDup, bounceAndDel, createOnDelete, deleteAfterReset, deleteMark
    = false;

    public int copiesToCreate, ignoreCollisionsFrames, cannotBeDeleted = 0;

    public Color Color;

    public String activeHandle = null; 

    public Shape() {}

    public Shape(String type, String name, double XPos,double YPos,double storedXVel,double storedYVel, Color color, double angle){
        this.type = type;
        this.name = name;
        this.XPos = XPos;
        this.YPos = YPos;
        this.storedXVel = storedXVel;
        this.storedYVel = storedYVel;
        this.XVel = 0;
        this.YVel = 0;
        this.Color = color;
        this.orgXPos = XPos;
        this.orgYPos = YPos;
        this.angle = angle % 360;

    }

    public abstract Shape Copy();
    public abstract void update(Entities world);                
    public abstract void render(GraphicsContext gc);
    public abstract double[][] getCorners();

    public abstract double getMinGridLen();

    public abstract double getMinX();
    public abstract double getMinY();
    public abstract double getMaxX();
    public abstract double getMaxY();

    public boolean collidesWith(Shape other) {
        return !(getMaxX() < other.getMinX() ||
                 getMinX() > other.getMaxX() ||
                 getMaxY() < other.getMinY() ||
                 getMinY() > other.getMaxY());
    }

    public abstract boolean contains(double x, double y);

    public abstract void highlight(GraphicsContext gc);

    public abstract String getClickedHandle(double mouseX, double mouseY);

    public abstract void resizeShape(String handleId, double mouseX, double mouseY);

    public abstract String[] getHandles();

    protected String getLogicalHandle(String clickedHandle) {
        if (clickedHandle.equals("Rotation")) return clickedHandle;

        String[] handles = getHandles();
        int n = handles.length;

        double stepAngle = 360.0 / n;

        int steps = (int) Math.round(angle / stepAngle);
        steps = ((steps % n) + n) % n; // Positive modulo

        int originalIndex = -1;
        for (int i = 0; i < n; i++) {
            if (handles[i].equals(clickedHandle)) {
                originalIndex = i;
                break;
            }
        }
        if (originalIndex == -1) return clickedHandle;

        int logicalIndex = (originalIndex + steps) % n;
        return handles[logicalIndex];
    }


}
