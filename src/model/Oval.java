package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
public class Oval extends GraphicObject {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;

    // 固定連接埠清單，初始化一次
    private List<ConnectionPort> ports;

    public Oval(int centerX, int centerY) {
        super(centerX, centerY);
        this.width = WIDTH;
        this.height = HEIGHT;

        // 初始化連接埠，根據初始寬高偏移設定
        ports = new ArrayList<>();
        int w = getWidth();
        int h = getHeight();
        ports.add(new ConnectionPort(this, 0, -h / 2)); // 上
        ports.add(new ConnectionPort(this, 0, h / 2));  // 下
        ports.add(new ConnectionPort(this, -w / 2, 0)); // 左
        ports.add(new ConnectionPort(this, w / 2, 0));  // 右
    }

    @Override
    public boolean contains(int x, int y) {
        int rx = width / 2;
        int ry = height / 2;
        double dx = (x - this.x) / (double) rx;
        double dy = (y - this.y) / (double) ry;
        return dx * dx + dy * dy <= 1.0;
    }

    @Override
    public List<ConnectionPort> getConnectionPorts() {
        return ports; // 回傳固定清單，不重新產生
    }

    @Override
    public void draw(Graphics g) {
        // 畫橢圓邊框（黑色）
        g.setColor(java.awt.Color.BLACK);
        g.drawOval(x - width / 2, y - height / 2, width, height);

        if (isSelected()) {
            // 畫紅色外框矩形
            g.setColor(java.awt.Color.RED);
            int left = x - width / 2;
            int top = y - height / 2;
            g.drawRect(left, top, width, height);

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
