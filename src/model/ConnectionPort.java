package model;

import java.awt.Graphics;

public class ConnectionPort {

    private GraphicObject owner;
    private int offsetX, offsetY;
    private boolean visible = false;  // 新增可見性狀態

    public ConnectionPort(GraphicObject owner, int offsetX, int offsetY) {
        this.owner = owner;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int getX() {
        return owner.getCenterX() + offsetX;
    }

    public int getY() {
        return owner.getCenterY() + offsetY;
    }

    public GraphicObject getOwner() {
        return owner;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    // 繪製連接埠，只有可見時才畫
    public void draw(Graphics g) {
        if (!visible) {
            return;
        }

        int size = 6; // 長方形大小
        int x = getX() - size / 2;
        int y = getY() - size / 2;

        g.setColor(java.awt.Color.BLACK);
        g.fillRect(x, y, size, size);
    }

}
