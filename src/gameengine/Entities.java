package gameengine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameengine.Shapes.Obstacle;
import gameengine.Shapes.Shape;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;

public class Entities {
    public Canvas canvas;
    public ArrayList<Shape> Shapes;
    public ArrayList<Shape> restoreShapes = new ArrayList<Shape>();
    public static ArrayList<Shape> leaderBoard = new ArrayList<>();
    public double GridLen = 0;
    public boolean running = false; 
    public boolean canAddShape = true;
    public Shape selectedShape = null;
    public double offsetX, offsetY;
    public ContextMenu activeContextMenu;

    Map<Long, List<Shape>> grid = new HashMap<>();

    long cellKey(int cx, int cy) {
        return (((long)cx) << 32) | (cy & 0xffffffffL);
    }

    public Entities(Canvas canvas) {
        Shapes = new ArrayList<>();
        this.canvas = canvas;
    }

    public void addShape(Shape s) {
        Shapes.add(s);
    }

    public void removeShape(Shape s) {
        Shapes.remove(s);
    }

    public double getWidth() {
        return canvas.getWidth();
    }

    public double getHeight() {
        return canvas.getHeight();
    }

    void insertIntoGrid(Shape s, double gridLen) {
        int minCellX = (int)(s.getMinX() / gridLen);
        int minCellY = (int)(s.getMinY() / gridLen);
        int maxCellX = (int)(s.getMaxX() / gridLen);
        int maxCellY = (int)(s.getMaxY() / gridLen);
    
        for (int cx = minCellX; cx <= maxCellX; cx++) {
            for (int cy = minCellY; cy <= maxCellY; cy++) {
                long key = cellKey(cx, cy);
                grid.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
            }
        }
    }


}
