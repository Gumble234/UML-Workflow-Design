package model;

import java.awt.*;

public class GeneralizationLink extends LinkObject {

    public GeneralizationLink(ConnectionPort p1, ConnectionPort p2) {
        super(p1, p2);
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        Graphics2D g2 = (Graphics2D) g;
        double angle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
        int size = 10;

        int x0 = p2.getX();
        int y0 = p2.getY();

        int x1 = x0 - (int) (size * Math.cos(angle));
        int y1 = y0 - (int) (size * Math.sin(angle));

        int x2 = x1 - (int) (size * Math.cos(angle + Math.PI / 2));
        int y2 = y1 - (int) (size * Math.sin(angle + Math.PI / 2));

        int x3 = x0 - (int) (size * Math.cos(angle + Math.PI / 2));
        int y3 = y0 - (int) (size * Math.sin(angle + Math.PI / 2));

        int[] xPoints = {x0, x1, x2, x3};
        int[] yPoints = {y0, y1, y2, y3};

        g2.drawPolygon(xPoints, yPoints, 4); // 畫菱形（空心）
    }
}
