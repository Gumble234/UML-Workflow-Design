package model;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class CompositeObject extends GraphicObject {

    private List<GraphicObject> children = new ArrayList<>();

    public CompositeObject(int x, int y) {
        super(x, y);  // 傳入中心座標
    }

    public void add(GraphicObject obj) {
        children.add(obj);
    }

    public void remove(GraphicObject obj) {
        children.remove(obj);
    }

    @Override
    public void draw(Graphics g) {
        for (GraphicObject obj : children) {
            obj.draw(g);
        }

        if (isSelected()) {
            // 若 Composite 被選取，也畫紅框（包住所有子物件的 bounding box）
            var bounds = getBounds();
            g.setColor(java.awt.Color.RED);
            g.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        for (GraphicObject obj : children) {
            if (obj.contains(px, py)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rectangle getBounds() {
        if (children.isEmpty()) {
            return new Rectangle(x, y, 0, 0);
        }

        Rectangle result = children.get(0).getBounds();
        for (int i = 1; i < children.size(); i++) {
            result = result.union(children.get(i).getBounds());
        }
        return result;
    }

    @Override
    public void setX(int newX) {
        int deltaX = newX - this.getCenterX();
        for (GraphicObject child : children) {
            child.setX(child.getCenterX() + deltaX);
        }
        this.x = newX;
    }

    @Override
    public void setY(int newY) {
        int deltaY = newY - this.getCenterY();
        for (GraphicObject child : children) {
            child.setY(child.getCenterY() + deltaY);
        }
        this.y = newY;
    }

    @Override
    public List<ConnectionPort> getConnectionPorts() {
        List<ConnectionPort> ports = new ArrayList<>();
        int w = getWidth(), h = getHeight();

        ports.add(new ConnectionPort(this, 0, -h / 2));             // 上
        ports.add(new ConnectionPort(this, w / 2, -h / 2));         // 右上
        ports.add(new ConnectionPort(this, w / 2, 0));              // 右
        ports.add(new ConnectionPort(this, w / 2, h / 2));          // 右下
        ports.add(new ConnectionPort(this, 0, h / 2));              // 下
        ports.add(new ConnectionPort(this, -w / 2, h / 2));         // 左下
        ports.add(new ConnectionPort(this, -w / 2, 0));             // 左
        ports.add(new ConnectionPort(this, -w / 2, -h / 2));        // 左上

        return ports;
    }

    public List<GraphicObject> getChildren() {
        return new ArrayList<>(children);
    }
}
