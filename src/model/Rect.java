package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Rect extends GraphicObject {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;

    // 固定連接埠清單，初始化一次
    private List<ConnectionPort> ports;

    public Rect(int centerX, int centerY) {
        super(centerX, centerY);
        this.width = WIDTH;
        this.height = HEIGHT;

        int w = getWidth();
        int h = getHeight();
        ports = new ArrayList<>();

        ports.add(new ConnectionPort(this, 0, -h / 2));             // 上
        ports.add(new ConnectionPort(this, w / 2, -h / 2));         // 右上
        ports.add(new ConnectionPort(this, w / 2, 0));              // 右
        ports.add(new ConnectionPort(this, w / 2, h / 2));          // 右下
        ports.add(new ConnectionPort(this, 0, h / 2));              // 下
        ports.add(new ConnectionPort(this, -w / 2, h / 2));         // 左下
        ports.add(new ConnectionPort(this, -w / 2, 0));             // 左
        ports.add(new ConnectionPort(this, -w / 2, -h / 2));        // 左上
    }

    @Override
    public List<ConnectionPort> getConnectionPorts() {
        return ports;  // 回傳固定清單
    }

    @Override
    public boolean contains(int x, int y) {
        int left = this.x - width / 2;
        int right = this.x + width / 2;
        int top = this.y - height / 2;
        int bottom = this.y + height / 2;
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    @Override
    public void draw(Graphics g) {
        int drawX = x - width / 2;
        int drawY = y - height / 2;

        // 畫黑色矩形邊框
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, width, height);

        // 選取時畫紅色外框及連接埠小方塊
        if (isSelected()) {
            g.setColor(Color.RED);
            g.drawRect(drawX - 2, drawY - 2, width + 4, height + 4);

            // 畫所有連接埠（port）
            for (ConnectionPort port : ports) {
                port.draw(g);
            }
        }
        // Label rendering
        g.setColor(labelColor);
        g.setFont(new Font("Arial", Font.PLAIN, labelFontSize));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textHeight = metrics.getHeight();
        int centerX = getCenterX();
        int centerY = getCenterY();

        int boxX = centerX - textWidth / 2 - 4;
        int boxY = centerY - textHeight / 2 - 2;
        int boxW = textWidth + 8;
        int boxH = textHeight;

        if (labelShape == ShapeType.RECT) {
            g.fillRect(boxX, boxY, boxW, boxH);
        } else {
            g.fillOval(boxX, boxY, boxW, boxH);
        }
        g.setColor(Color.BLACK);
        g.drawString(label, centerX - textWidth / 2, centerY + textHeight / 4);

    }
}
