package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public abstract class GraphicObject {

    protected int x, y; // 中心點座標
    protected int width = 100;
    protected int height = 60;
    private boolean selected = false;
    protected String label = "Label";
    protected ShapeType labelShape = ShapeType.RECT;
    protected Color labelColor = Color.LIGHT_GRAY;
    protected int labelFontSize = 12;

    public enum ShapeType {
        RECT, OVAL
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ShapeType getLabelShape() {
        return labelShape;
    }

    public void setLabelShape(ShapeType shape) {
        this.labelShape = shape;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color color) {
        this.labelColor = color;
    }

    public int getLabelFontSize() {
        return labelFontSize;
    }

    public void setLabelFontSize(int size) {
        this.labelFontSize = size;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public GraphicObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getCenterX() {
        return x;
    }

    public int getCenterY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isSelected() {
        return selected;
    }
    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public abstract void draw(Graphics g);

    public void setSelected(boolean selected) {
        this.selected = selected;
        // 選取狀態時顯示 ports，否則隱藏
        for (ConnectionPort port : getConnectionPorts()) {
            port.setVisible(selected);
        }
    }

    public boolean contains(int px, int py) {
        int left = x - width / 2;
        int top = y - height / 2;
        return px >= left && px <= left + width && py >= top && py <= top + height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - width / 2, y - height / 2, width, height);
    }

    // ✅ 重點：每個 GraphicObject 都需提供自己的連接埠
    public abstract List<ConnectionPort> getConnectionPorts();
}
