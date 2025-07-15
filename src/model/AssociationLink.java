package model;

import java.awt.Graphics;

public class AssociationLink extends LinkObject {

    public AssociationLink(ConnectionPort p1, ConnectionPort p2) {
        super(p1, p2);
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        // 兩條斜線模擬箭頭
        double angle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
        int arrowLength = 10;

        int x1 = p2.getX() - (int)(arrowLength * Math.cos(angle - Math.PI / 6));
        int y1 = p2.getY() - (int)(arrowLength * Math.sin(angle - Math.PI / 6));
        int x2 = p2.getX() - (int)(arrowLength * Math.cos(angle + Math.PI / 6));
        int y2 = p2.getY() - (int)(arrowLength * Math.sin(angle + Math.PI / 6));

        g.drawLine(p2.getX(), p2.getY(), x1, y1);
        g.drawLine(p2.getX(), p2.getY(), x2, y2);
    }
}
