package gameengine;

import gameengine.HelperFunctions.CollisionTypeFinder;
import gameengine.Shapes.FinishLine;
import gameengine.Shapes.Obstacle;
import gameengine.Shapes.Shape;

public class CollisionHelper {

    // Returns the minimum translation vector (dx, dy) to resolve collision, or null if no collision
    public static double[] polygonCollision(double[][] polyA, double[][] polyB) {
        double minOverlap = Double.MAX_VALUE;
        double[] smallestAxis = null;

        // Iterate edges of both polygons
        for (double[][] poly : new double[][][]{polyA, polyB}) {
            int n = poly.length;
            for (int i = 0; i < n; i++) {
                double[] p1 = poly[i];
                double[] p2 = poly[(i + 1) % n];

                double edgeX = p2[0] - p1[0];
                double edgeY = p2[1] - p1[1];

                // perpendicular axis
                double axisX = -edgeY;
                double axisY = edgeX;
                double len = Math.sqrt(axisX * axisX + axisY * axisY);
                axisX /= len;
                axisY /= len;

                // project polyA
                double minA = Double.MAX_VALUE, maxA = -Double.MAX_VALUE;
                for (double[] v : polyA) {
                    double proj = v[0] * axisX + v[1] * axisY;
                    minA = Math.min(minA, proj);
                    maxA = Math.max(maxA, proj);
                }

                // project polyB
                double minB = Double.MAX_VALUE, maxB = -Double.MAX_VALUE;
                for (double[] v : polyB) {
                    double proj = v[0] * axisX + v[1] * axisY;
                    minB = Math.min(minB, proj);
                    maxB = Math.max(maxB, proj);
                }

                // overlap
                double overlap = Math.min(maxA, maxB) - Math.max(minA, minB);
                if (overlap <= 0) return null; // no collision

                if (overlap < minOverlap) {
                    minOverlap = overlap;
                    smallestAxis = new double[]{axisX, axisY};

                    // ensure axis points from A → B
                    double centerAX = 0, centerAY = 0, centerBX = 0, centerBY = 0;
                    for (double[] v : polyA) { centerAX += v[0]; centerAY += v[1]; }
                    for (double[] v : polyB) { centerBX += v[0]; centerBY += v[1]; }
                    centerAX /= polyA.length; centerAY /= polyA.length;
                    centerBX /= polyB.length; centerBY /= polyB.length;

                    double dx = centerBX - centerAX;
                    double dy = centerBY - centerAY;
                    if (dx * axisX + dy * axisY < 0) {
                        smallestAxis[0] = -axisX;
                        smallestAxis[1] = -axisY;
                    }
                }
            }
        }

        return new double[]{smallestAxis[0] * minOverlap, smallestAxis[1] * minOverlap};
    }

    public static void resolveCollision(Shape a, Shape b, double[] mtv) {
    if (a instanceof FinishLine || b instanceof FinishLine) { 
        handleFinishLineCollision(a, b); 
        return; 
    }

    if(a.ignoreCollision || b.ignoreCollision) return;

    if(a.ignoreCollisionsFrames > 0) a.ignoreCollisionsFrames--;
    if(b.ignoreCollisionsFrames > 0) b.ignoreCollisionsFrames--;

    if(a.ignoreCollisionsFrames > 0 || b.ignoreCollisionsFrames > 0) return;

    if (mtv == null) return;

    boolean aStatic = a instanceof Obstacle;
    boolean bStatic = b instanceof Obstacle;

    // Slightly overcompensate to push overlapping objects apart
    double pushMultiplier = 1.05;
    double dx = mtv[0] * pushMultiplier;
    double dy = mtv[1] * pushMultiplier;

    double len = Math.sqrt(dx*dx + dy*dy);
    double nx = dx / len;
    double ny = dy / len;

    if (aStatic && !bStatic) {
        b.XPos += dx;
        b.YPos += dy;
    } else if (!aStatic && bStatic) {
        a.XPos -= dx;
        a.YPos -= dy;
    } else if (!aStatic && !bStatic) {
        a.XPos -= dx / 2;
        a.YPos -= dy / 2;
        b.XPos += dx / 2;
        b.YPos += dy / 2;
    }

    if (!aStatic) reflectVelocity(a, nx, ny);
    if (!bStatic) reflectVelocity(b, nx, ny);

    if(a.wallAndDup) if(CollisionTypeFinder.isShapeAndObstacle(a.type,b.type)) a.copiesToCreate++;
    if(b.wallAndDup) if(CollisionTypeFinder.isShapeAndObstacle(b.type,a.type)) b.copiesToCreate++;
    if(a.bounceAndDel) a.deleteMark = CollisionTypeFinder.isShapeAndShape(a.type,b.type);
    else if(b.bounceAndDel) b.deleteMark = CollisionTypeFinder.isShapeAndShape(b.type,a.type);
}


    private static void reflectVelocity(Shape s, double nx, double ny) {
        double dot = s.XVel * nx + s.YVel * ny;
        s.XVel -= 2 * dot * nx;
        s.YVel -= 2 * dot * ny;

        if (Math.abs(s.XVel) < 0.01) s.XVel = 0;
        if (Math.abs(s.YVel) < 0.01) s.YVel = 0;
    }


private static void handleFinishLineCollision(Shape a, Shape b) {
    Shape s = (a instanceof FinishLine) ? b : a;
    FinishLine f = (a instanceof FinishLine) ? (FinishLine)a : (FinishLine)b;

    if (s instanceof Obstacle) return;

    if (s.finished) return;

    if (!s.finishing) {
        s.finishing = true;
        s.ignoreCollision = true;
    }

    double sMinX = s.getMinX();
    double sMaxX = s.getMaxX();
    double sMinY = s.getMinY();
    double sMaxY = s.getMaxY();

    double moveX = s.XVel;
    double moveY = s.YVel;

    if (s.XVel > 0) moveX = Math.min(moveX, f.getMaxX() - sMaxX);
    else if (s.XVel < 0) moveX = Math.max(moveX, f.getMinX() - sMinX);

    if (s.YVel > 0) moveY = Math.min(moveY, f.getMaxY() - sMaxY);
    else if (s.YVel < 0) moveY = Math.max(moveY, f.getMinY() - sMinY);

    s.XPos += moveX;
    s.YPos += moveY;

    sMinX = s.getMinX();
    sMaxX = s.getMaxX();
    sMinY = s.getMinY();
    sMaxY = s.getMaxY();

    if (sMinX >= f.getMinX() && sMaxX <= f.getMaxX() &&
        sMinY >= f.getMinY() && sMaxY <= f.getMaxY()) {
        s.XVel = 0;
        s.YVel = 0;
        s.finishing = false;
        s.finished = true;
    }

    if (!Entities.leaderBoard.contains(s)) {
    Entities.leaderBoard.add(s);
    System.out.println(Entities.leaderBoard);
}
}


}
