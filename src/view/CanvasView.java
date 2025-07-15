package view;

import controller.CanvasController;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class CanvasView extends JPanel {
    private CanvasController controller;

    public CanvasView(CanvasController controller) {
        this.controller = controller;
        setBackground(java.awt.Color.WHITE);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.onMousePressed(e.getX(), e.getY());
                repaint(); // 更新畫面
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                controller.onMouseReleased(e.getX(), e.getY());
                repaint(); // 執行後重新繪製
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                controller.onMouseDragged(e.getX(), e.getY());
                repaint(); // 拖曳預覽
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        controller.renderAll(g);
    }
}
