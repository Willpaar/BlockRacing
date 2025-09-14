package gameengine.Shapes;
import gameengine.Entities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Wall extends Obstacle {   
    public Wall(String type, String name, double len, double XPos,double YPos, Color color, double angle){
        super(type, name, XPos, YPos, color, angle);
        this.len = len;
    }

    public Wall(Wall w){
        super(w.type, w.name, w.XPos, w.YPos, w.Color, w.angle);
        this.len = w.len;
    }

    @Override 
    public Shape Copy(){
        return new Wall(this);
    }
    
    public double len;

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(XPos, YPos);
        gc.rotate(angle);
        gc.setFill(Color);
        gc.fillRect(-3/2.0, -len/2.0, 3, len); 
        gc.restore();

        if(isSelected) highlight(gc);
    }



    @Override
    public double getMinGridLen() {
        //dont want this to affect grid len
        return 0;
    }

    @Override
    public double[][] getCorners() {
        double rad = Math.toRadians(this.angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double halfW = 3 / 2.0;  // half width
        double halfH = this.len / 2.0; // half height

        // local corners centered at (0,0)
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

            // rotate
            double xRot = x * cos - y * sin;
            double yRot = x * sin + y * cos;

            // translate to center position
            worldCorners[i][0] = this.XPos + xRot;
            worldCorners[i][1] = this.YPos + yRot;
        }

        return worldCorners;
    }


    @Override
    public double getMinX() {
        double[][] c = getCorners();
        return Math.min(Math.min(c[0][0], c[1][0]), Math.min(c[2][0], c[3][0]));
    }

    @Override
    public double getMaxX() {
        double[][] c = getCorners();
        return Math.max(Math.max(c[0][0], c[1][0]), Math.max(c[2][0], c[3][0]));
    }

    @Override
    public double getMinY() {
        double[][] c = getCorners();
        return Math.min(Math.min(c[0][1], c[1][1]), Math.min(c[2][1], c[3][1]));
    }

    @Override
    public double getMaxY() {
        double[][] c = getCorners();
        return Math.max(Math.max(c[0][1], c[1][1]), Math.max(c[2][1], c[3][1]));
    }

    @Override
    public boolean contains(double x, double y) {
        // translate into local coords (relative to center)
        double dx = x - this.XPos;
        double dy = y - this.YPos;

        // undo rotation
        double rad = Math.toRadians(-this.angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double localX = dx * cos - dy * sin;
        double localY = dx * sin + dy * cos;

        // now check against rectangle centered at (0,0)
        return localX >= -1.5 && localX <= 1.5 &&
            localY >= -this.len / 2 && localY <= this.len / 2;
    }

@Override
public void highlight(GraphicsContext gc) {
    Color iconColor = Color.LIGHTBLUE;
    Color borderColor = Color.GREY;
    if (this.Color == Color.LIGHTBLUE) {
        iconColor = Color.GREY;
    } else if (this.Color == Color.GREY) {
        borderColor = Color.BLACK;
    }

    double halfLen = len / 2.0;
    double rad = Math.toRadians(angle);

    double dx = Math.sin(rad) * halfLen;
    double dy = -Math.cos(rad) * halfLen;

    // Endpoints
    double x1 = XPos - dx;
    double y1 = YPos - dy;
    double x2 = XPos + dx;
    double y2 = YPos + dy;

    double size = 8;
    gc.setFill(iconColor);
    gc.setStroke(borderColor);
    gc.fillRect(x1 - size/2, y1 - size/2, size, size);
    gc.strokeRect(x1 - size/2, y1 - size/2, size, size);
    gc.fillRect(x2 - size/2, y2 - size/2, size, size);
    gc.strokeRect(x2 - size/2, y2 - size/2, size, size);

    double handleDistance = 20;
    double handleRadius = 6;
    double perpDx = Math.cos(rad) * handleDistance;
    double perpDy = Math.sin(rad) * handleDistance;

    double h1x = XPos - perpDx;
    double h1y = YPos - perpDy;
    double h2x = XPos + perpDx;
    double h2y = YPos + perpDy;

    gc.setFill(iconColor);
    gc.fillOval(h1x - handleRadius, h1y - handleRadius, handleRadius*2, handleRadius*2);
    gc.fillOval(h2x - handleRadius, h2y - handleRadius, handleRadius*2, handleRadius*2);

    gc.setStroke(borderColor);
    gc.strokeOval(h1x - handleRadius, h1y - handleRadius, handleRadius*2, handleRadius*2);
    gc.strokeOval(h2x - handleRadius, h2y - handleRadius, handleRadius*2, handleRadius*2);

    gc.setStroke(Color.GRAY);
    gc.strokeLine(XPos, YPos, h1x, h1y);
    gc.strokeLine(XPos, YPos, h2x, h2y);
}


@Override
public String getClickedHandle(double mouseX, double mouseY) {
    double halfLen = len / 2.0;
    double rad = Math.toRadians(angle);

    // Endpoint vector
    double dx = Math.sin(rad) * halfLen;
    double dy = -Math.cos(rad) * halfLen;

    // Endpoints (resize handles)
    double x1 = XPos - dx; // top end
    double y1 = YPos - dy;
    double x2 = XPos + dx; // bottom end
    double y2 = YPos + dy;

    // Rotation handles (perpendicular offset from midpoint)
    double handleDistance = 20;
    double handleRadius = 6;
    double perpDx = Math.cos(rad) * handleDistance;
    double perpDy = Math.sin(rad) * handleDistance;

    double h1x = XPos - perpDx; // left rotation
    double h1y = YPos - perpDy;
    double h2x = XPos + perpDx; // right rotation
    double h2y = YPos + perpDy;

    // --- Check clicks ---
    double resizeHalfSize = 8 / 2.0;

    // Resize handles (square hitbox)
    if (Math.abs(mouseX - x1) <= resizeHalfSize && Math.abs(mouseY - y1) <= resizeHalfSize) {
        return "Top-End";
    }
    if (Math.abs(mouseX - x2) <= resizeHalfSize && Math.abs(mouseY - y2) <= resizeHalfSize) {
        return "Bottom-End";
    }

    // Rotation handles (circle hitbox)
    double distH1 = Math.hypot(mouseX - h1x, mouseY - h1y);
    if (distH1 <= handleRadius) {
        return "Rotation";
    }

    double distH2 = Math.hypot(mouseX - h2x, mouseY - h2y);
    if (distH2 <= handleRadius) {
        return "Rotation";
    }

    // Default: no handle clicked
    return null;
}

@Override
public void resizeShape(String handleId, double mouseX, double mouseY) {
    double dx = mouseX - XPos;
    double dy = mouseY - YPos;
    double rad = Math.toRadians(angle);

    double minLen = 10; // Minimum total length the wall can shrink to

    switch (handleId) {
        case "Top-End": {
            double proj = dx * Math.sin(rad) - dy * Math.cos(rad);
            len = Math.max(minLen, 2 * Math.abs(proj)); // stop shrinking at minLen
            break;
        }

        case "Bottom-End": {
            double proj = dx * Math.sin(rad) - dy * Math.cos(rad);
            len = Math.max(minLen, 2 * Math.abs(proj));
            break;
        }

        case "Rotation": {
            angle = Math.toDegrees(Math.atan2(mouseY - YPos, mouseX - XPos)) + 90;
            break;
        }
    }
}




    @Override
    public String[] getHandles() {
        return new String[] {
            "Top-End", "Bottom-End", 
        };
    }

    

}
