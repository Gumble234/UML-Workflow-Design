package model;

import java.awt.*;

public class CompositionLink extends LinkObject {

    public CompositionLink(ConnectionPort p1, ConnectionPort p2) {
        super(p1, p2);
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        Graphics2D g2 = (Graphics2D) g;
        double angle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
        int size = 10;

        int[] xPoints = {
            p2.getX(),
            p2.getX() - (int)(size * Math.cos(angle - Math.PI / 6)),
            p2.getX() - (int)(size * Math.cos(angle)),
            p2.getX() - (int)(size * Math.cos(angle + Math.PI / 6))
        };

        int[] yPoints = {
            p2.getY(),
            p2.getY() - (int)(size * Math.sin(angle - Math.PI / 6)),
            p2.getY() - (int)(size * Math.sin(angle)),
            p2.getY() - (int)(size * Math.sin(angle + Math.PI / 6))
        };

        g2.fillPolygon(xPoints, yPoints, 4);
    }
}
