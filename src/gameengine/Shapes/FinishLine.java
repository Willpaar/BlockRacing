package gameengine.Shapes;

import gameengine.Entities;
import gameengine.HelperFunctions.HighlightPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FinishLine extends Obstacle{
    public double XLen, YLen;

    public final boolean ignoreCollision = true;
        
    public FinishLine(String type, String name, double XLen, double YLen, double XPos,double YPos, Color color, double angle){
        super(type, name, XPos, YPos, color, angle);
        this.XLen = XLen;
        this.YLen = YLen;
    }

    public FinishLine(FinishLine f){
        super(f.type, f.name, f.XPos , f.YPos, f.Color, f.angle);
        this.XLen = f.XLen;
        this.YLen = f.YLen;
    }

    @Override 
    public Shape Copy(){
        return new FinishLine(this);
    }

    @Override
    public void render(GraphicsContext gc) {
        double insideLen = 10; 

        // Save context
        gc.save();

        // Translate to center and rotate
        gc.translate(XPos, YPos);
        gc.rotate(angle);

        int cols = (int) Math.ceil(XLen / insideLen);
        int rows = (int) Math.ceil(YLen / insideLen);

        double startX = -XLen / 2;
        double startY = -YLen / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * insideLen;
                double y = startY + row * insideLen;

                if ((row + col) % 2 == 0) {
                    gc.setFill(this.Color);
                } else {
                    gc.setFill(javafx.scene.paint.Color.WHITE);
                }

                gc.fillRect(x, y, insideLen, insideLen);
            }
        }

        gc.restore();

        if (isSelected) {
            highlight(gc);
        }
    }



    @Override
    public double getMinX() {
        double[][] c = getCorners();
        double min = c[0][0];
        for (int i = 1; i < c.length; i++) min = Math.min(min, c[i][0]);
        return min;
    }

    @Override
    public double getMaxX() {
        double[][] c = getCorners();
        double max = c[0][0];
        for (int i = 1; i < c.length; i++) max = Math.max(max, c[i][0]);
        return max;
    }

    @Override
    public double getMinY() {
        double[][] c = getCorners();
        double min = c[0][1];
        for (int i = 1; i < c.length; i++) min = Math.min(min, c[i][1]);
        return min;
    }

    @Override
    public double getMaxY() {
        double[][] c = getCorners();
        double max = c[0][1];
        for (int i = 1; i < c.length; i++) max = Math.max(max, c[i][1]);
        return max;
    }

    @Override
    public boolean contains(double x, double y) {
        double halfW = XLen / 2.0;
        double halfH = YLen / 2.0;
        return x >= XPos - halfW && x <= XPos + halfW &&
            y >= YPos - halfH && y <= YPos + halfH;
    }


    @Override
    public double[][] getCorners() {
        double rad = Math.toRadians(this.angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        // local square corners (centered at (0,0))
        double halfW = XLen / 2.0;
        double halfH = YLen / 2.0;

        double[][] localCorners = {
            {-halfW, -halfH},
            { halfW, -halfH},
            { halfW,  halfH},
            {-halfW,  halfH}
        };

        double[][] worldCorners = new double[4][2];
        for (int i = 0; i < 4; i++) {
            double x = localCorners[i][0];
            double y = localCorners[i][1];

            double xRot = x * cos - y * sin;
            double yRot = x * sin + y * cos;

            worldCorners[i][0] = this.XPos + xRot;
            worldCorners[i][1] = this.YPos + yRot;
        }

        return worldCorners;
    }

    @Override
    public double getMinGridLen() {
        return Math.max(XLen, YLen);
    }

    @Override
    public void highlight(GraphicsContext gc) {

        Color iconColor = Color.LIGHTBLUE;
        Color borderColor = Color.GREY;
        if(this.Color == Color.LIGHTBLUE){
            iconColor = Color.GREY;
        } 
        else if(this.Color == Color.GREY){
            borderColor = Color.BLACK;
        }

        double minX = getMinX();
        double maxX = getMaxX();
        double minY = getMinY();
        double maxY = getMaxY();

        gc.setStroke(borderColor);
        gc.setLineWidth(1);
        gc.strokeRect(minX, minY, maxX - minX, maxY - minY);

        double size = 8;
        double midX = (minX + maxX) / 2;
        double midY = (minY + maxY) / 2;

        double[][] iconPositions = {
            {minX, minY}, {maxX, minY}, {minX, maxY}, {maxX, maxY},
            {midX, minY}, {midX, maxY}, {minX, midY}, {maxX, midY}
        };

        gc.setFill(iconColor);
        gc.setLineWidth(1);
        gc.setStroke(borderColor);

        for (double[] pos : iconPositions) {
            gc.fillRect(pos[0] - size/2, pos[1] - size/2, size, size);
            gc.strokeRect(pos[0] - size/2, pos[1] - size/2, size, size);
        }

        double handleDistance = 20;
        double handleRadius = 6;
        double handleX = midX;
        double handleY = minY - handleDistance;

        gc.setFill(iconColor); 
        gc.fillOval(handleX - handleRadius, handleY - handleRadius, handleRadius * 2, handleRadius * 2);
        gc.setStroke(borderColor);
        gc.strokeOval(handleX - handleRadius, handleY - handleRadius, handleRadius * 2, handleRadius * 2);

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeLine(midX, minY, handleX, handleY);
    }

    @Override
    public String getClickedHandle(double mouseX, double mouseY) {
        double minX = getMinX();
        double maxX = getMaxX();
        double minY = getMinY();
        double maxY = getMaxY();
        double midX = (minX + maxX) / 2;
        double midY = (minY + maxY) / 2;

        double size = 8; // corner/edge handle size
        double handleDistance = 20;
        double handleRadius = 6;

        double handleX = midX;
        double handleY = minY - handleDistance;

        HighlightPoint[] corners = {
            new HighlightPoint(minX, minY, "Top-Left"),
            new HighlightPoint(maxX, minY, "Top-Right"),
            new HighlightPoint(minX, maxY, "Bottom-Left"),
            new HighlightPoint(maxX, maxY, "Bottom-Right")
        };

        HighlightPoint[] edges = {
            new HighlightPoint(midX, minY, "Top-Center"),
            new HighlightPoint(midX, maxY, "Bottom-Center"),
            new HighlightPoint(minX, midY, "Left-Center"),
            new HighlightPoint(maxX, midY, "Right-Center")
        };

        for (HighlightPoint corner : corners) {
            if (mouseX >= corner.x - size/2 && mouseX <= corner.x + size/2 &&
                mouseY >= corner.y - size/2 && mouseY <= corner.y + size/2) {
                return corner.name;
            }
        }

        for (HighlightPoint edge : edges) {
            if (mouseX >= edge.x - size/2 && mouseX <= edge.x + size/2 &&
                mouseY >= edge.y - size/2 && mouseY <= edge.y + size/2) {
                return edge.name;
            }
        }

        if (Math.hypot(mouseX - handleX, mouseY - handleY) <= handleRadius) {
            return "Rotation";
        }

        return null;
    }

    @Override
    public void resizeShape(String handle, double mouseX, double mouseY) {
        handle = handle.equals("Rotation") ? handle : getLogicalHandle(handle);


        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double minSize = 10; 
        double localX =  cos * (mouseX - XPos) + sin * (mouseY - YPos);
        double localY = -sin * (mouseX - XPos) + cos * (mouseY - YPos);

        double halfW = XLen / 2;
        double halfH = YLen / 2;

        switch (handle) {
            case "Top-Left":
                double newHalfW_TL = Math.max(minSize/2, halfW - localX);
                double newHalfH_TL = Math.max(minSize/2, halfH - localY);
                XPos += (halfW - newHalfW_TL) * cos - (halfH - newHalfH_TL) * sin;
                YPos += (halfW - newHalfW_TL) * sin + (halfH - newHalfH_TL) * cos;
                XLen = newHalfW_TL * 2;
                YLen = newHalfH_TL * 2;
                break;

            case "Top-Right":
                double newHalfW_TR = Math.max(minSize/2, halfW + localX);
                double newHalfH_TR = Math.max(minSize/2, halfH - localY);
                XPos += (newHalfW_TR - halfW) * cos - (halfH - newHalfH_TR) * sin;
                YPos += (newHalfW_TR - halfW) * sin + (halfH - newHalfH_TR) * cos;
                XLen = newHalfW_TR * 2;
                YLen = newHalfH_TR * 2;
                break;

            case "Bottom-Left":
                double newHalfW_BL = Math.max(minSize/2, halfW - localX);
                double newHalfH_BL = Math.max(minSize/2, halfH + localY);
                XPos += (halfW - newHalfW_BL) * cos - (newHalfH_BL - halfH) * sin;
                YPos += (halfW - newHalfW_BL) * sin + (newHalfH_BL - halfH) * cos;
                XLen = newHalfW_BL * 2;
                YLen = newHalfH_BL * 2;
                break;

            case "Bottom-Right":
                double newHalfW_BR = Math.max(minSize/2, halfW + localX);
                double newHalfH_BR = Math.max(minSize/2, halfH + localY);
                XPos += (newHalfW_BR - halfW) * cos - (newHalfH_BR - halfH) * sin;
                YPos += (newHalfW_BR - halfW) * sin + (newHalfH_BR - halfH) * cos;
                XLen = newHalfW_BR * 2;
                YLen = newHalfH_BR * 2;
                break;

            case "Top-Center":
                double newHalfH_TC = Math.max(minSize/2, halfH - localY);
                YPos += (halfH - newHalfH_TC) * cos;
                XPos += (halfH - newHalfH_TC) * -sin;
                YLen = newHalfH_TC * 2;
                break;

            case "Bottom-Center":
                double newHalfH_BC = Math.max(minSize/2, halfH + localY);
                YPos += (newHalfH_BC - halfH) * cos;
                XPos += (newHalfH_BC - halfH) * -sin;
                YLen = newHalfH_BC * 2;
                break;

            case "Left-Center":
                double newHalfW_LC = Math.max(minSize/2, halfW - localX);
                XPos += (halfW - newHalfW_LC) * cos;
                YPos += (halfW - newHalfW_LC) * sin;
                XLen = newHalfW_LC * 2;
                break;

            case "Right-Center":
                double newHalfW_RC = Math.max(minSize/2, halfW + localX);
                XPos += (newHalfW_RC - halfW) * cos;
                YPos += (newHalfW_RC - halfW) * sin;
                XLen = newHalfW_RC * 2;
                break;

            case "Rotation":
                angle = Math.toDegrees(Math.atan2(mouseY - YPos, mouseX - XPos)) - 90;
                break;
        }
    }

    @Override
    public String[] getHandles() {
        return new String[] {
            "Top-Left", "Top-Center", "Top-Right",
            "Right-Center", "Bottom-Right", "Bottom-Center",
            "Bottom-Left", "Left-Center"
        };
    }

}
