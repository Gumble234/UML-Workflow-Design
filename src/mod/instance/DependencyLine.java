package mod.instance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;

public class DependencyLine extends JPanel
        implements IFuncComponent, ILinePainter {
    JPanel from;
    int fromSide;
    Point fp = new Point(0, 0);
    JPanel to;
    int toSide;
    Point tp = new Point(0, 0);
    int arrowSize = 15;
    int panelExtendSize = 10;
    boolean isSelect = false;
    int selectBoxSize = 5;
    CanvasPanelHandler cph;

    public DependencyLine(CanvasPanelHandler cph) {
        this.setOpaque(false);
        this.setVisible(true);
        this.setMinimumSize(new Dimension(1, 1));
        this.cph = cph;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(isHighlight ? Color.RED : Color.BLACK);
        Point fpPrime;
        Point tpPrime;
        Graphics2D g2 = (Graphics2D) g;
        renewConnect();
        fpPrime = new Point(fp.x - this.getLocation().x,
                fp.y - this.getLocation().y);
        tpPrime = new Point(tp.x - this.getLocation().x,
                tp.y - this.getLocation().y);
        float[] dash = { 5.0f };
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

        g.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
        paintArrow(g2, tpPrime);
        if (isSelect == true) {
            paintSelect(g2);
        }
    }

    @Override
    public void reSize() {
        Dimension size = new Dimension(
                Math.abs(fp.x - tp.x) + panelExtendSize * 2,
                Math.abs(fp.y - tp.y) + panelExtendSize * 2);
        this.setSize(size);
        this.setLocation(Math.min(fp.x, tp.x) - panelExtendSize,
                Math.min(fp.y, tp.y) - panelExtendSize);
    }

    @Override
    public void paintArrow(Graphics g, Point point) {
        Graphics2D g2 = (Graphics2D) g;
        float[] dash = { 5.0f };
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0));
        // 箭頭形狀作為線段端點的兩條虛線
        double angle = Math.atan2(tp.y - fp.y, tp.x - fp.x);
        for (int sign : new int[] { 1, -1 }) {
            double theta = angle + sign * Math.toRadians(25);
            int xEnd = (int) (point.x - arrowSize * Math.cos(theta));
            int yEnd = (int) (point.y - arrowSize * Math.sin(theta));
            g2.drawLine(point.x, point.y, xEnd, yEnd);
        }
    }

    @Override
    public void setConnect(DragPack dPack) {
        Point mfp = dPack.getFrom();
        Point mtp = dPack.getTo();
        from = (JPanel) dPack.getFromObj();
        to = (JPanel) dPack.getToObj();
        fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(),
                mfp);
        toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);
        renewConnect();
        System.out.println("from side " + fromSide);
        System.out.println("to side " + toSide);
    }

    void renewConnect() {
        try {
            fp = getConnectPoint(from, fromSide);
            tp = getConnectPoint(to, toSide);
            this.reSize();
        } catch (NullPointerException e) {
            this.setVisible(false);
            cph.removeComponent(this);
        }
    }

    Point getConnectPoint(JPanel jp, int side) {
        Point temp = new Point(0, 0);
        Point jpLocation = cph.getAbsLocation(jp);
        if (side == new AreaDefine().TOP) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = jpLocation.y;
        } else if (side == new AreaDefine().RIGHT) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        } else if (side == new AreaDefine().LEFT) {
            temp.x = jpLocation.x;
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        } else if (side == new AreaDefine().BOTTOM) {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
        } else {
            temp = null;
            System.err.println("getConnectPoint fail:" + side);
        }
        return temp;
    }

    @Override
    public void paintSelect(Graphics gra) {
        gra.setColor(Color.BLACK);
        gra.fillRect(fp.x, fp.y, selectBoxSize, selectBoxSize);
        gra.fillRect(tp.x, tp.y, selectBoxSize, selectBoxSize);
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    private boolean isHighlight = false;

    public void setHighlight(boolean h) {
        this.isHighlight = h;
        repaint();
    }

    public JPanel getFromComponent() {
        return from;
    }

    public int getFromSide() {
        return fromSide;
    }

    public JPanel getToComponent() {
        return to;
    }

    public int getToSide() {
        return toSide;
    }
}
